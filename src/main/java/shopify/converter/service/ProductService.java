package shopify.converter.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.springframework.stereotype.Service;
import shopify.converter.controller.ProductController;
import shopify.converter.model.Product;
import shopify.converter.response.ProductsResponse;
import shopify.converter.schema.CSVSchema;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;
import shopify.converter.util.ProductConverter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {


    private final ProductConverter productConverter;

    private ProductsResponse getProductResponse(String url) throws RuntimeException {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

                    return objectMapper.readValue(entity.getContent(), ProductsResponse.class);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Request error: " + e.getMessage(), e);
        }

        return null;
    }

    public void getFileContent(String url) throws RuntimeException {

        ProductsResponse productsResponse = getProductResponse(url);
        if (productsResponse != null) {

            List<LinkedHashMap<String, String>> productsMap = new ArrayList<>();
            List<LinkedHashMap<String, String>> inventoriesMap = new ArrayList<>();

            for (Product product : productsResponse.getProducts()) {

                List<ProductSchema> products = productConverter.convertProduct(product);
                for (ProductSchema productSchema : products) {
                    productsMap.add(getProductMap(productSchema));
                }

                List<InventorySchema> inventorySchemas = productConverter.convertToInventory(product);
                for (InventorySchema inventorySchema : inventorySchemas) {
                    inventoriesMap.add(getProductMap(inventorySchema));
                }
            }
            productsMap = removeEmptyFields(productsMap);
            List<String> headLine = getAllKeysFromProduct(productsMap.get(0));

            List<String> inventoryHeadLine = getAllKeysFromProduct(inventoriesMap.get(0));

            writeMapToCsv(headLine, productsMap, ProductController.PRODUCT_CSV_PATH);
            writeMapToCsv(inventoryHeadLine, inventoriesMap, ProductController.INVENTORY_CSV_PATH);

        }

    }

    private List<String> getAllKeysFromProduct(LinkedHashMap<String, String> product) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> entry : product.entrySet()) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    private List<LinkedHashMap<String, String>> removeEmptyFields(List<LinkedHashMap<String, String>> productsMap) {

        HashSet<String> keysToRemove = getKeysToDelete(productsMap);
        for (LinkedHashMap<String, String> product : productsMap) {
            // Iterate through keys to be removed
            for (String key : keysToRemove) {
                // Remove the key if it exists in the product
                product.remove(key);
            }
        }
        return productsMap;
    }

    private HashSet<String> getKeysToDelete(List<LinkedHashMap<String, String>> productsMap) {

        List<String> allProductKeys = getAllKeysFromProduct(productsMap.get(0));
        HashSet<String> keysToRemove = new HashSet<>();

        for (String key : allProductKeys) {
            boolean isValueExist = false;

            for (LinkedHashMap<String, String> product : productsMap) {
                if (product.get(key) != null && !product.get(key).isEmpty()) {
                    isValueExist = true;
                }
            }

            if (!isValueExist) {
                keysToRemove.add(key);
            }
        }

        return keysToRemove;
    }

    private void writeMapToCsv(List<String> headLine, List<LinkedHashMap<String, String>> productsMap, String filePath) {
        List<String> lines = new ArrayList<>();
        lines.add(String.join(",", headLine));
        for (var productMap : productsMap) {
            String csvString = convertMapToCSVString(productMap);
            lines.add(csvString);
        }
        writeToFile(lines, filePath);
    }

    private void writeToFile(List<String> lines, String filePath) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Данные успешно записаны в CSV файл: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    private String convertMapToCSVString(Map<String, String> dataMap) {
        StringBuilder csvBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            csvBuilder.append(entry.getValue()).append(",");
        }

        return csvBuilder.substring(0, csvBuilder.length() - 1);
    }

    private List<String> getHeadList(CSVSchema mainProductRow) {
        List<String> headLine = new ArrayList<>();
        Field[] fields = mainProductRow.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(mainProductRow);
                if (value != null && value != "") {
                    JsonProperty annotation = field.getAnnotation(JsonProperty.class);
                    if (annotation != null) {
                        headLine.add(annotation.value());
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return headLine;
    }

    private LinkedHashMap<String, String> getProductMap(CSVSchema csvSchema) {
        LinkedHashMap<String, String> productMap = new LinkedHashMap<>();
        Field[] fields = csvSchema.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            JsonProperty annotation = field.getAnnotation(JsonProperty.class);
            if (annotation != null) {
                Object value = null;
                try {
                    value = field.get(csvSchema);
                    productMap.put(annotation.value(), value != null ? value.toString() : "");

                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return productMap;
    }


}
