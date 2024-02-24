package shopify.converter.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;
import shopify.converter.controller.ProductController;
import shopify.converter.model.VendorProduct;
import shopify.converter.model.revit.RevitProduct;
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
public class ProductService {


    public void saveCsvFile(List<VendorProduct> vendorProducts, ProductConverter productConverter) {

        List<LinkedHashMap<String, String>> productsMap = new ArrayList<>();
        List<LinkedHashMap<String, String>> inventoriesMap = new ArrayList<>();

        for (VendorProduct revitProduct : vendorProducts) {

            List<ProductSchema> products = productConverter.convertToProductSchema(revitProduct);
            for (ProductSchema productSchema : products) {
                productsMap.add(getProductMap(productSchema));
            }

            List<InventorySchema> inventorySchemas = productConverter.convertToInventorySchema(revitProduct);
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


    protected LinkedHashMap<String, String> getProductMap(CSVSchema csvSchema) {
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

    protected List<LinkedHashMap<String, String>> removeEmptyFields(List<LinkedHashMap<String, String>> productsMap) {

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

    protected List<String> getAllKeysFromProduct(LinkedHashMap<String, String> product) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> entry : product.entrySet()) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    protected void writeMapToCsv(List<String> headLine, List<LinkedHashMap<String, String>> productsMap, String filePath) {
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


}
