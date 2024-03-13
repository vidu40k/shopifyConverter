package shopify.converter.service.whitesmoto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shopify.converter.controller.whitesmoto.WhitesmotoController;
import shopify.converter.converter.whitesmoto.WhitesmotoConverter;
import shopify.converter.model.whitesmoto.WhitesmotoProduct;
import shopify.converter.response.whitesmoto.ProductQty;
import shopify.converter.service.ProductService;
import shopify.converter.util.FileCleanupScheduler;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WhitesmotoService extends ProductService {

    private static final String INIT_LOGIN = "https://kratos.whitesplatform.com/self-service/login/api";
    private static final String GET_ACCESS_TOKEN = "https://auth.whitesplatform.com/v1/access_token";
    private static final String CREATE_CURSOR_PRODUCT_QTY = "https://api-test.whitesplatform.com/v1/table/product";
    private static final String GET_CURSOR_DATA = "https://api-test.whitesplatform.com/v1/cursor/";
    private static final String GET_PRODUCT = "https://api-test.whitesplatform.com/v1/table/product/";
    private static final String COMPANY_CODE = "wpadev_motoheadz";

    public static final String RESULT_DATA_WHITESMOTO_FOLDER = WhitesmotoController.RESULT_DATA_WHITESMOTO_FOLDER;
    public static final String PRODUCTS_FILE_TYPE = WhitesmotoController.PRODUCTS_FILE_TYPE;
    public static final String INVENTORY_FILE_TYPE = WhitesmotoController.INVENTORY_FILE_TYPE;

    private final FileCleanupScheduler fileCleanupScheduler;
    @Value("${whitesmoto_password}")
    private String PASSWORD;
    @Value("${whitesmoto_password_identifier}")
    private String PASSWORD_IDENTIFIER;
    private String accessToken;


    private final WhitesmotoConverter whitesmotoConverter;

    @Override
    public Map<String, List<String>> parseToProductsCsv() {//todo deleted file

        List<WhitesmotoProduct> whitesmotoProducts = getProducts();
        Map<String, List<WhitesmotoProduct>> sortedProductsByBrands = sortProductMapByListSize(groupByManufacturer(whitesmotoProducts));
        List<String> productPaths = new ArrayList<>();
        List<String> inventoryPaths = new ArrayList<>();

        List<WhitesmotoProduct> groupedProducts = new ArrayList<>();
        int rowCounter = 0;
        int savedFileCounter = 0;
        for (String brand : sortedProductsByBrands.keySet()) {
            var brandsProducts = sortedProductsByBrands.get(brand);

            if (brandsProducts.size() > 2500) {

                if (brand.isEmpty())
                    brand = "unknownBrand";
                saveFile(brand, productPaths, inventoryPaths, brandsProducts);

            } else if (rowCounter + brandsProducts.size() > 2500) {

                saveFile("otherBrands" + ++savedFileCounter, productPaths, inventoryPaths, groupedProducts); //save group
                groupedProducts.clear();
                rowCounter = 0;

                rowCounter += brandsProducts.size();
                groupedProducts.addAll(brandsProducts);

            } else if (rowCounter + brandsProducts.size() < 2500) { //add brand to group
                rowCounter += brandsProducts.size();
                groupedProducts.addAll(brandsProducts);
            }
        }
        if (!groupedProducts.isEmpty()) {
            saveFile("otherBrands" + ++savedFileCounter, productPaths, inventoryPaths, groupedProducts); //save group
        }

        Map<String, List<String>> map = new HashMap<>();
        map.put("products", productPaths);
        map.put("inventory", inventoryPaths);
        return map;
    }

    private void saveFile(String fileName, List<String> productPaths, List<String> inventoryPaths, List<WhitesmotoProduct> brandsProducts) {
        var productPath = RESULT_DATA_WHITESMOTO_FOLDER + PRODUCTS_FILE_TYPE + "-" + fileName + ".csv";
        var inventoryPath = RESULT_DATA_WHITESMOTO_FOLDER + INVENTORY_FILE_TYPE + "-" + fileName + ".csv";

        productPaths.add(productPath);
        fileCleanupScheduler.addFilePath(productPath);

        inventoryPaths.add(inventoryPath);
        fileCleanupScheduler.addFilePath(inventoryPath);

        saveCsvFile(new ArrayList<>(brandsProducts), whitesmotoConverter, productPath, inventoryPath);
    }


    private static Map<String, List<WhitesmotoProduct>> sortProductMapByListSize(Map<String, List<WhitesmotoProduct>> productMap) {
        // Преобразование карты в список элементов
        List<Map.Entry<String, List<WhitesmotoProduct>>> list = new LinkedList<>(productMap.entrySet());

        // Сортировка списка с помощью компаратора
        list.sort(Comparator.comparingInt(o -> o.getValue().size()));

        // Создание новой упорядоченной карты
        LinkedHashMap<String, List<WhitesmotoProduct>> sortedProductMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<WhitesmotoProduct>> entry : list) {
            sortedProductMap.put(entry.getKey(), entry.getValue());
        }

        return sortedProductMap;
    }

    public Map<String, List<WhitesmotoProduct>> groupByManufacturer(List<WhitesmotoProduct> products) {
        Map<String, List<WhitesmotoProduct>> productMap = new LinkedHashMap<>();

        for (WhitesmotoProduct product : products) {
            String manufacturer = product.getManufacturer();
            List<WhitesmotoProduct> productListForManufacturer = productMap.getOrDefault(manufacturer, new ArrayList<>());
            productListForManufacturer.add(product);
            productMap.put(manufacturer, productListForManufacturer);
        }
        return productMap;
    }

    public List<WhitesmotoProduct> getProducts() {

        refreshAccessToken();

        var cursorId = createProductsCursor();
        var products = getCursorData(cursorId);

        return products;
    }

    private List<WhitesmotoProduct> getProductsByCodes(List<ProductQty> productQtyList) {

        List<WhitesmotoProduct> products = new ArrayList<>();
        for (ProductQty productQty : productQtyList) {

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(GET_PRODUCT + productQty.getProductCode());

                request.addHeader("X-Whites-Access-Token", this.accessToken);
                request.addHeader("X-Company-Code", COMPANY_CODE);

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {

                        String responseBody = EntityUtils.toString(entity);
                        ObjectMapper objectMapper = new ObjectMapper();

                        var product = objectMapper.readValue(responseBody, WhitesmotoProduct.class);

                        products.add(product);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Request error: " + e.getMessage(), e);
            }

        }

        return products;
    }


    private List<WhitesmotoProduct> getCursorData(String cursorId) {
        List<WhitesmotoProduct> products = new ArrayList<>();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String responseBody;
            boolean hasData = true;

            int currentPage = 1;
            while (hasData) {
                HttpGet request = new HttpGet(GET_CURSOR_DATA + cursorId);

                request.addHeader("X-Whites-Access-Token", this.accessToken);
                request.addHeader("X-Company-Code", COMPANY_CODE);

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {
                        responseBody = EntityUtils.toString(entity);
                        ObjectMapper objectMapper = new ObjectMapper();

                        try {
                            List<WhitesmotoProduct> pageProducts = objectMapper.readValue(responseBody, new TypeReference<>() {});
                            if (pageProducts.isEmpty()) {
                                hasData = false;

                            } else {
                                products.addAll(pageProducts);
                            }
                        }
                        catch (Exception e){
                            System.out.println(new Gson().fromJson(responseBody, JsonObject.class).get("msg"));
                        }
                    }
                }
                System.out.println(currentPage++);
            }
        } catch (IOException e) {
            throw new RuntimeException("Request error: " + e.getMessage(), e);
        }
        return products;
    }

    private void refreshAccessToken() {
        String authUrl = getAuthenticateURL(INIT_LOGIN);
        String sessionToken = getSessionToken(authUrl);
        this.accessToken = getAccessToken(sessionToken);
    }

    private String createProductsCursor() {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost httpPost = new HttpPost(CREATE_CURSOR_PRODUCT_QTY);
            httpPost.addHeader("X-Whites-Access-Token", this.accessToken);
            httpPost.addHeader("X-Company-Code", COMPANY_CODE);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                    try {
                        return jsonObject.get("id").getAsString();
                    }
                    catch (Exception e){
                        System.out.println(jsonObject.get("msg"));
                    }

                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Request error: " + e.getMessage(), e);
        }
        return null;

    }

    private String getAccessToken(String sessionToken) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(GET_ACCESS_TOKEN);
            request.addHeader("X-Session-Token", sessionToken);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);

                    JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                    String access_token = jsonObject.get("access_token").getAsString();

                    return access_token;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Request error: " + e.getMessage(), e);
        }
        return null;

    }

    private String getSessionToken(String authUrl) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Создание объекта HttpPost с указанным URL
            HttpPost httpPost = new HttpPost(authUrl);

            // Создание тела запроса в формате JSON
            String jsonBody = "{\"password_identifier\":\"" + PASSWORD_IDENTIFIER + "\", \"password\":\"" + PASSWORD + "\", \"method\":\"password\"}";

            // Установка тела запроса
            StringEntity requestEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);

                    JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                    String sessionToken = jsonObject.get("session_token").getAsString();

                    return sessionToken;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Request error: " + e.getMessage(), e);
        }
        return null;
    }

    private String getAuthenticateURL(String url) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);

                    JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                    String actionUrl = jsonObject.getAsJsonObject("ui").get("action").getAsString();

                    return actionUrl;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Request error: " + e.getMessage(), e);
        }
        return null;
    }


}
