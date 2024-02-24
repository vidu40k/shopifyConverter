package shopify.converter.service.revit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.classic.RequestFailedException;
import org.apache.hc.core5.http.HttpEntity;
import org.springframework.stereotype.Service;
import shopify.converter.model.VendorProduct;
import shopify.converter.response.ProductsResponse;
import shopify.converter.service.ProductService;
import shopify.converter.util.ProductConverter;
import shopify.converter.util.RevitConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevitService extends ProductService {


    private final RevitConverter revitConverter;

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
            var products = productsResponse.getRevitProducts();

//            if (!products.isEmpty()) {
//                List<VendorProduct> vendorProducts = new ArrayList<>(products);
//                saveCsvFile(vendorProducts, (ProductConverter) revitConverter);
//            }else {
//                throw new RuntimeException("cannot get products from api");
//            }

        }
    }

//    public void saveCsvFile(List<VendorProduct> vendorProducts){
//
//        List<LinkedHashMap<String, String>> productsMap = new ArrayList<>();
//        List<LinkedHashMap<String, String>> inventoriesMap = new ArrayList<>();
//
//        for (VendorProduct revitProduct : vendorProducts) {
//
//            List<ProductSchema> products = revitConverter.convertToProductSchema((RevitProduct) revitProduct);
//            for (ProductSchema productSchema : products) {
//                productsMap.add(getProductMap(productSchema));
//            }
//
//            List<InventorySchema> inventorySchemas = revitConverter.convertToInventorySchema((RevitProduct) revitProduct);
//            for (InventorySchema inventorySchema : inventorySchemas) {
//                inventoriesMap.add(getProductMap(inventorySchema));
//            }
//        }
//
//        productsMap = removeEmptyFields(productsMap);
//        List<String> headLine = getAllKeysFromProduct(productsMap.get(0));
//
//        List<String> inventoryHeadLine = getAllKeysFromProduct(inventoriesMap.get(0));
//
//        writeMapToCsv(headLine, productsMap, ProductController.PRODUCT_CSV_PATH);
//        writeMapToCsv(inventoryHeadLine, inventoriesMap, ProductController.INVENTORY_CSV_PATH);
//
//    }

//    private List<String> getHeadList(CSVSchema mainProductRow) {
//        List<String> headLine = new ArrayList<>();
//        Field[] fields = mainProductRow.getClass().getDeclaredFields();
//        for (Field field : fields) {
//            field.setAccessible(true);
//            try {
//                Object value = field.get(mainProductRow);
//                if (value != null && value != "") {
//                    JsonProperty annotation = field.getAnnotation(JsonProperty.class);
//                    if (annotation != null) {
//                        headLine.add(annotation.value());
//                    }
//                }
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        return headLine;
//    }

//    private LinkedHashMap<String, String> getProductMap(CSVSchema csvSchema) {
//        LinkedHashMap<String, String> productMap = new LinkedHashMap<>();
//        Field[] fields = csvSchema.getClass().getDeclaredFields();
//        for (Field field : fields) {
//            field.setAccessible(true);
//            JsonProperty annotation = field.getAnnotation(JsonProperty.class);
//            if (annotation != null) {
//                Object value = null;
//                try {
//                    value = field.get(csvSchema);
//                    productMap.put(annotation.value(), value != null ? value.toString() : "");
//
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//        return productMap;
//    }


}
