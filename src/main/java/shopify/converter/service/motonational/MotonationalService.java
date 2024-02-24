package shopify.converter.service.motonational;

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
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import shopify.converter.controller.ProductController;
import shopify.converter.model.Motonational.MotonationalProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;
import shopify.converter.service.ProductService;
import shopify.converter.util.MotonationalCoverter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MotonationalService extends ProductService {

    private static final String APPLICATION_NAME = "ShopifyConverter";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String MOTONATIONAL_PRODUCTS = "Motonational.csv";
    private static final String PARENT_FOLDER_ID = "1ObJH7Zq07dqLzk26x7fYpLJ-BL55DOMX";
    private static final String CSV_FOLDER_NAME = "CSV";

    private final MotonationalCoverter motonationalCoverter;

    public void parseToProductsCsv() {

        try {
            downloadExternalCsv();
            List<MotonationalProduct> motonationalProducts = getMotonationalProductFromFile(MOTONATIONAL_PRODUCTS);

            List<LinkedHashMap<String, String>> productsMap = new ArrayList<>();
            List<LinkedHashMap<String, String>> inventoriesMap = new ArrayList<>();

            for (MotonationalProduct product : motonationalProducts) {

                List<ProductSchema> products = motonationalCoverter.convertToProductSchema(product);
                for (ProductSchema productSchema : products) {
                    productsMap.add(getProductMap(productSchema));
                }

                List<InventorySchema> inventorySchemas = motonationalCoverter.convertToInventorySchema(product);
                for (InventorySchema inventorySchema : inventorySchemas) {
                    inventoriesMap.add(getProductMap(inventorySchema));
                }

            }
            productsMap = removeEmptyFields(productsMap);

            List<String> headLine = getAllKeysFromProduct(productsMap.get(0));
            List<String> inventoryHeadLine = getAllKeysFromProduct(inventoriesMap.get(0));

            writeMapToCsv(headLine, productsMap, ProductController.PRODUCT_CSV_PATH);
            writeMapToCsv(inventoryHeadLine, inventoriesMap, ProductController.INVENTORY_CSV_PATH);

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void downloadExternalCsv() throws GeneralSecurityException, IOException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        List<com.google.api.services.drive.model.File> files = getCSVFilesInFolder(service);
        for (com.google.api.services.drive.model.File file : files) {

            InputStream csvInputStream = downloadCsvFile(service, file.getId());
            var f = readCSVFromInputStream(csvInputStream);
            writeCSVToFile(processCSVRecords(f), MOTONATIONAL_PRODUCTS);
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


    private List<MotonationalProduct> getMotonationalProductFromFile(String fileName) throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(fileName))) {
            return new CsvToBeanBuilder<MotonationalProduct>(reader)
                    .withType(MotonationalProduct.class)
                    .build()
                    .parse();
        }
    }

    private void writeCSVToFile(List<List<String>> records, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
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
    }

    private List<List<String>> processCSVRecords(List<CSVRecord> records) {
        List<List<String>> updatedRecords = new ArrayList<>();

        for (CSVRecord record : records) {
            List<String> updatedFields = new ArrayList<>();

            int filledFieldsCount = 0;
            for (String value : record) {
                filledFieldsCount++;
                if (value != null) {
                    value = value.replaceAll("\r", "");
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


    private List<com.google.api.services.drive.model.File> getCSVFilesInFolder(Drive service) throws IOException {
        List<com.google.api.services.drive.model.File> csvFiles = new ArrayList<>();

        // Получение списка файлов в папке
        FileList result = service.files().list()
                .setQ("'" + findFolderId(service, PARENT_FOLDER_ID, CSV_FOLDER_NAME) + "' in parents and mimeType = 'text/csv'")
                .setFields("files(id, name)")
                .execute();
        List<com.google.api.services.drive.model.File> files = result.getFiles();

        // Фильтрация файлов, оставляем только CSV файлы
        if (files != null && !files.isEmpty()) {
            csvFiles.addAll(files);
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
