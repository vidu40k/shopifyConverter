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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {


    private final ProductConverter productConverter;

    private ProductsResponse getProductResponse(String url) {

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
            throw new RuntimeException("Ошибка при выполнении запроса: " + e.getMessage(), e);
        }

        return null;
    }

    public void getFileContent(String url) {

        ProductsResponse productsResponse = getProductResponse(url);

        if (productsResponse != null) {
            List<String> headLine = getHeadList(productConverter.convertProduct(productsResponse.getProducts().get(0)).get(0));
            List<String> inventoryHeadLine = getHeadList(productConverter.convertToInventory(productsResponse.getProducts().get(0)).get(0));

            List<LinkedHashMap<String, String>> productsMap = new ArrayList<>();
            List<LinkedHashMap<String, String>> inventoriesMap = new ArrayList<>();

            for (Product product : productsResponse.getProducts()) {

                List<ProductSchema> products = productConverter.convertProduct(product);
                for (ProductSchema productSchema : products) {
                    productsMap.add(getProductMap(productSchema, headLine));
                }

                List<InventorySchema> inventorySchemas = productConverter.convertToInventory(product);
                for (InventorySchema inventorySchema : inventorySchemas) {
                    inventoriesMap.add(getProductMap(inventorySchema, inventoryHeadLine));
                }
            }

            writeMapToCsv(headLine, productsMap, "src/main/resources/static/products.csv");
            writeMapToCsv(inventoryHeadLine, inventoriesMap, "src/main/resources/static/inventory.csv");

        }

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

    private LinkedHashMap<String, String> getProductMap(CSVSchema csvSchema, List<String> headLine) {
        LinkedHashMap<String, String> productMap = new LinkedHashMap<>();
        Field[] fields = csvSchema.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            JsonProperty annotation = field.getAnnotation(JsonProperty.class);
            if (headLine.contains(annotation.value())) { //if header contains this field name, field added to productLine
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
