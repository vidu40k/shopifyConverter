package shopify.converter.service.motonational;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import shopify.converter.controller.motonational.MotonationalController;
import shopify.converter.converter.motonational.MotivationalConverter;
import shopify.converter.model.Motonational.MotonationalProduct;
import shopify.converter.service.ProductService;
import shopify.converter.util.FileCleanupScheduler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MotonationalService extends ProductService {

    private static final String APPLICATION_NAME = "ShopifyConverter";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES =
            new ArrayList<>(List.of(DriveScopes.DRIVE));
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String CSV_FOLDER_NAME = "CSV";

    private static final String MOTONATIONAL_PRODUCTS_FOLDER = MotonationalController.RESULT_DATA_MOTONATIONAL_FILE;
    private static final String INITIAL_DATA_MOTONATIONAL_FILE = MotonationalController.INITIAL_DATA_MOTONATIONAL_FILE;
    public static final String PRODUCTS_FILE_TYPE = MotonationalController.PRODUCTS_FILE_TYPE;
    public static final String INVENTORY_FILE_TYPE = MotonationalController.INVENTORY_FILE_TYPE;

    private final MotivationalConverter motivationalConverter;
    private final FileCleanupScheduler fileCleanupScheduler;

    @Override
    public Map<String, List<String>> parseToProductsCsv() {

        Map<String, List<String>> productFiles = new LinkedHashMap<>();

        try {
            var fileList = downloadExternalCsv();

//            List<String> fileList = new ArrayList<>();
//            fileList.add("initialData/motonational/FalcoBoot-product-export-21-12-2023-1703118963752.csv");
//            fileList.add("initialData/motonational/Kabuto-product-export-21-12-2023-1703121210293.csv");
//            fileList.add("initialData/motonational/Crocbite_ProductCSV_5-12-23.csv");
//            fileList.add("initialData/motonational/Bobster_ProductCSV_5-12-23.csv");
//            fileList.add("initialData/motonational/Airoh-product-export-21-12-2023-1703113161643.csv");
//            fileList.add("initialData/motonational/Lok-Up-product-export-21-12-2023-1703122402593.csv");
//            fileList.add("initialData/motonational/CUBE-product-export-21-12-2023-1703115647290.csv");
//            fileList.add("initialData/motonational/Motodry-product-export-21-12-2023-1703123181623.csv");
//            fileList.add("initialData/motonational/Five-Gloves-product-export-21-12-2023-1703117676003.csv");
//            fileList.add("initialData/motonational/MotoPlus_ProductCSV_5-12-23.csv");
//            fileList.add("initialData/motonational/MX-Net-product-export-21-12-2023-1703124027448.csv");
//            fileList.add("initialData/motonational/RXT-product-export-21-12-2023-1703130542620.csv");
//            fileList.add("initialData/motonational/Shad-product-export-21-12-2023-1703128792741.csv");
//            fileList.add("initialData/motonational/TBR-product-export-21-12-2023-1703131291545.csv");
//            fileList.add("initialData/motonational/ZanHeadgear_ProductCSV_5-12-23.csv");
//            fileList.add("initialData/motonational/Zero-product-export-21-12-2023-1703132384235.csv");


            // ------------------------------------------------- // download in one file
//            List<MotonationalProduct> motonationalProducts = new ArrayList<>();
//            for (String filePath : fileList){
//                motonationalProducts.addAll(readCsvFile(filePath));
//            }
//            saveCsvFile(new ArrayList<>(motonationalProducts), motivationalConverter, "products.csv", "inventory.csv");
            // ------------------------------------------------- //


            List<String> productPaths = new ArrayList<>();
            List<String> inventoryPaths = new ArrayList<>();

            List<MotonationalProduct> motonationalProducts;
            for (String filePath : fileList) {  // download in many Files
                motonationalProducts = readCsvFile(filePath);

                var fileName = extractFileNameFromPath(filePath);



                var productPath = MOTONATIONAL_PRODUCTS_FOLDER + PRODUCTS_FILE_TYPE + "-" + fileName + ".csv";
                var inventoryPath = MOTONATIONAL_PRODUCTS_FOLDER + INVENTORY_FILE_TYPE + "-" + fileName + ".csv";
                productPaths.add(productPath);
                inventoryPaths.add(inventoryPath);

                fileCleanupScheduler.addFilePath(productPath);
                fileCleanupScheduler.addFilePath(inventoryPath);

                saveCsvFile(new ArrayList<>(motonationalProducts), motivationalConverter, productPath, inventoryPath);
            }

            productFiles.put(PRODUCTS_FILE_TYPE, productPaths);
            productFiles.put(INVENTORY_FILE_TYPE, inventoryPaths);


        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return productFiles;
    }

    private String extractFileNameFromPath(String path) {
        int lastSlashIndex = path.lastIndexOf("/");

        if (lastSlashIndex != -1) {
            String filenamePart = path.substring(lastSlashIndex + 1);

            int firstDashIndex = filenamePart.indexOf("-");
            int firstUnderscoreIndex = filenamePart.indexOf("_");

            int firstSymbolIndex = Math.min(firstDashIndex != -1 ? firstDashIndex : Integer.MAX_VALUE,
                    firstUnderscoreIndex != -1 ? firstUnderscoreIndex : Integer.MAX_VALUE);

            if (firstSymbolIndex != Integer.MAX_VALUE) {
                return filenamePart.substring(0, firstSymbolIndex);
            } else {
                return filenamePart;
            }
        }

        return "";
    }


    public List<String> downloadExternalCsv() throws GeneralSecurityException, IOException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        Map<String, String> vendors = new HashMap<>(
                Map.of(
                        "Airoh", "https://drive.google.com/drive/folders/1ObJH7Zq07dqLzk26x7fYpLJ-BL55DOMX",
                        "Five Gloves", "https://drive.google.com/drive/folders/14OKsESt6JcsSQL_d-lM8-tujxquWTTTK",
                        "SHAD", "https://drive.google.com/drive/folders/1jVLuxUN_ABca6NI-GrrUQFzjNJcWitOx",
                        "Falco", "https://drive.google.com/drive/folders/1vElcqlwhGnQwdj4XxH8d67txND2iGrn5")
        );

        vendors.put("TWOBROTHERS", "https://drive.google.com/drive/folders/15y50FOHqvdQWuPQnHE7T0x7so6yFUTe4");
        vendors.put("MotoDry", "https://drive.google.com/drive/folders/1w-AN5uiPoSgOMNsQYtBWhVz0mZf_PjD2");
        vendors.put("RXT", "https://drive.google.com/drive/folders/1uR3mN2uaPGBHEVh3Ag2M_qlP3u7nMlf9");
        vendors.put("Kabuto", "https://drive.google.com/drive/folders/1hah7SdaK-1omPu3aAPdQQmjTkenkReI9");
        vendors.put("Zero Goggles", "https://drive.google.com/drive/folders/1Ry_gbA7UCmBBaaQN7eJmvtZZn5g6EwBZ");
        vendors.put("Lok-Up", "https://drive.google.com/drive/folders/1_qvwiXF2iKTEzAyMGcyQDPOhY0ew4jeN");
        vendors.put("Cube", "https://drive.google.com/drive/folders/1RHthLWRJPNFo8u3uqKzzjlUDNqcM_5Je");
        vendors.put("MX Net", "https://drive.google.com/drive/folders/1VIVyutDYJIlB1i-wK9RP5OnHJ-pXHZJG");
        vendors.put("Crocbite", "https://drive.google.com/drive/folders/1AKGON2kX14VWu1S8TABfTv_arem_yYG_");
        vendors.put("BOBSTER", "https://drive.google.com/drive/folders/1Iqy38l7sNlmSIi15KR5xGTSKKNui7gUd");
        vendors.put("Moto+", "https://drive.google.com/drive/folders/10JLLuV3Atf0gmorZlmUfycpveftFkES7");
        vendors.put("ZANheadgear", "https://drive.google.com/drive/folders/1Wu65ADEwRvzEJolPbYuDSyZOx2B0zdhA");

        List<String> initialFilePaths = new ArrayList<>();

        List<com.google.api.services.drive.model.File> files = getCSVFilesInFolder(service, new ArrayList<>(vendors.values()));
        for (com.google.api.services.drive.model.File file : files) {

            InputStream csvInputStream = downloadCsvFile(service, file.getId());
            List<CSVRecord> csvRecords = new ArrayList<>(readCSVFromInputStream(csvInputStream));

            var initialDataFilepath = writeCSVToFile(processCSVRecords(csvRecords), file.getName());
            initialFilePaths.add(initialDataFilepath);
            fileCleanupScheduler.addFilePath(initialDataFilepath);

        }

        return initialFilePaths;
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load service account credentials from JSON file
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        GoogleCredential googleCredential = GoogleCredential.fromStream(in, HTTP_TRANSPORT, JSON_FACTORY)
                .createScoped(SCOPES);

        return googleCredential;
    }

    private List<CSVRecord> readCSVFromInputStream(InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        return csvParser.getRecords();
    }

    private static List<MotonationalProduct> readCsvFile(String fileName) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        ObjectReader reader = csvMapper.readerFor(MotonationalProduct.class).with(schema);

        List<MotonationalProduct> products = new ArrayList<>();
        try (MappingIterator<MotonationalProduct> iterator = reader.readValues(new File(fileName))) {
            while (iterator.hasNext()) {
                MotonationalProduct product = iterator.next();
                products.add(product);
            }
        }
        return products;
    }

    private String writeCSVToFile(List<List<String>> records, String fileName) {

        String path = INITIAL_DATA_MOTONATIONAL_FILE + fileName;
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {
            for (List<String> record : records) {
                StringBuilder line = new StringBuilder();
                for (String value : record) {
                    if (value != null) {
                        line.append(value.replaceAll(",", ""));
                    }
                    line.append(",");
                }
                line.deleteCharAt(line.length() - 1);
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    private List<List<String>> processCSVRecords(List<CSVRecord> records) {
        List<List<String>> updatedRecords = new ArrayList<>();

        for (CSVRecord record : records) {
            List<String> updatedFields = new ArrayList<>();

            int filledFieldsCount = 0;
            for (String value : record) {
                filledFieldsCount++;
                if (value != null) {
                    value = value.replace("\uFEFF", "").trim();
                    value = value.replaceAll("\r", "");
                    value = value.replaceAll("\"", "\"\"");
                    value = "\"" + value + "\"";
                }
                updatedFields.add(value);
            }
            while (filledFieldsCount < 72) {
                updatedFields.add("");
                filledFieldsCount++;
            }
            updatedRecords.add(updatedFields);
        }

        return updatedRecords;
    }

    private String findFolderId(Drive service, String parentFolderId, String folderName) throws IOException {
        String pageToken = null;
        do {
            FileList result = service.files().list()
                    .setQ("'" + parentFolderId + "' in parents and mimeType = 'application/vnd.google-apps.folder' and name = '" + folderName + "'")
                    .setFields("nextPageToken, files(id)")
                    .setPageToken(pageToken)
                    .execute();

            List<com.google.api.services.drive.model.File> files = result.getFiles();
            if (files != null) {
                for (com.google.api.services.drive.model.File file : files) {
                    return file.getId();
                }
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        return null;
    }

    private String extractFolderIdFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    private List<com.google.api.services.drive.model.File> getCSVFilesInFolder(Drive service, List<String> parentFolderURLs) throws IOException {
        List<com.google.api.services.drive.model.File> csvFiles = new ArrayList<>();

        for (String parentFolderUrl : parentFolderURLs) {

            System.out.println("get files from:" + parentFolderUrl);

            // Получение списка файлов в папке google
            FileList result = service.files().list()
                    .setQ("'" + findFolderId(service, extractFolderIdFromUrl(parentFolderUrl), CSV_FOLDER_NAME) + "' in parents and mimeType = 'text/csv'")
                    .setFields("files(id, name)")
                    .execute();
            List<com.google.api.services.drive.model.File> files = result.getFiles();

            // Фильтрация файлов, оставляем только CSV файлы
            if (files != null && !files.isEmpty()) {
                if (files.size() > 1) {
                    for (com.google.api.services.drive.model.File file : files) {
                        if (file.getName().contains("product-export")) {
                            csvFiles.add(file);
                        }
                    }
                } else
                    csvFiles.addAll(files);
            }
        }
        return csvFiles;
    }

    private InputStream downloadCsvFile(Drive driveService, String fileId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        driveService.files().get(fileId)
                .executeMediaAndDownloadTo(outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return new ByteArrayInputStream(byteArray);
    }


}
