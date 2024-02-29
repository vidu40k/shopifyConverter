package shopify.converter.service.motonational;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
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
    private static final String TOKENS_DIRECTORY_PATH = "/tokens";
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String CSV_FOLDER_NAME = "CSV";
    private static final String MOTONATIONAL_EXTERNAL_PRODUCTS = "Motonational.csv";
    private static final String MOTONATIONAL_EXTERNAL_PRODUCTS_NO_BOM = "MotonationalNoBom.csv";

    private static final String MOTONATIONAL_PRODUCTS_CSV = "src/main/resources/products/motonational/products.csv";
    private static final String MOTONATIONAL_INVENTORY_CSV = "src/main/resources/products/motonational/inventory.csv";

    private final MotivationalConverter motivationalConverter;
    private final FileCleanupScheduler fileCleanupScheduler;

    public void parseToProductsCsv() {

        try {

            List<MotonationalProduct> motonationalProducts = new ArrayList<>();
           var fileList =  downloadExternalCsv();

//            List<String> fileList = new ArrayList<>();
//            fileList.add("initialData/motonational/FalcoBoot-product-export-21-12-2023-1703118963752.csv");
//            fileList.add("initialData/motonational/Kabuto-product-export-21-12-2023-1703121210293.csv");
//            fileList.add("initialData/motonational/Bobster_ProductCSV_5-12-23.csv");
//            fileList.add("initialData/motonational/Crocbite_ProductCSV_5-12-23.csv");
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

            for (String filePath : fileList){
                motonationalProducts.addAll(readCsvFile(filePath));
            }
            saveCsvFile(new ArrayList<>(motonationalProducts), motivationalConverter, MOTONATIONAL_PRODUCTS_CSV, MOTONATIONAL_INVENTORY_CSV);

//            fileCleanupScheduler.addFilePath(MOTONATIONAL_EXTERNAL_PRODUCTS);
//            fileCleanupScheduler.addFilePath(MOTONATIONAL_EXTERNAL_PRODUCTS_NO_BOM);
//            fileCleanupScheduler.addFilePath(MOTONATIONAL_PRODUCTS_CSV);
//            fileCleanupScheduler.addFilePath(MOTONATIONAL_INVENTORY_CSV);

        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> downloadExternalCsv() throws GeneralSecurityException, IOException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        Map<String, String> vendors = new HashMap<>(Map.of(
                "Airoh", "https://drive.google.com/drive/folders/1ObJH7Zq07dqLzk26x7fYpLJ-BL55DOMX",
                "Five Gloves", "https://drive.google.com/drive/folders/14OKsESt6JcsSQL_d-lM8-tujxquWTTTK",
                "SHAD", "https://drive.google.com/drive/folders/1jVLuxUN_ABca6NI-GrrUQFzjNJcWitOx",
                "Falco", "https://drive.google.com/drive/folders/1vElcqlwhGnQwdj4XxH8d67txND2iGrn5"));

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
            initialFilePaths.add(writeCSVToFile(processCSVRecords(csvRecords), file.getName()));
//            removeBOM(MOTONATIONAL_EXTERNAL_PRODUCTS, MOTONATIONAL_EXTERNAL_PRODUCTS_NO_BOM);
        }

        return initialFilePaths;
    }

    private static void removeBOM(String inputFilePath, String outputFilePath) {
        try (InputStream inputStream = new FileInputStream(inputFilePath);
             OutputStream outputStream = new FileOutputStream(outputFilePath);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {

            // Проверяем, есть ли BOM
            byte[] bomBytes = new byte[3];
            bufferedInputStream.mark(3);
            bufferedInputStream.read(bomBytes, 0, 3);
            bufferedInputStream.reset();

            // Если BOM есть, пропускаем его
            if (bomBytes[0] == (byte) 0xEF && bomBytes[1] == (byte) 0xBB && bomBytes[2] == (byte) 0xBF) {
                bufferedInputStream.skip(3);
            }

            // Копируем оставшееся содержимое файла
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = MotonationalService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
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

        String path = "initialData/motonational/" + fileName;
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
