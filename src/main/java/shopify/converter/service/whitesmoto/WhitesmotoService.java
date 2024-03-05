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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WhitesmotoService extends ProductService {

    private static final String INIT_LOGIN = "https://kratos.whitesplatform.com/self-service/login/api";
    private static final String GET_ACCESS_TOKEN = "https://auth.whitesplatform.com/v1/access_token";
    private static final String CREATE_CURSOR_PRODUCT_QTY = "https://api-test.whitesplatform.com/v1/table/product_qty";
    private static final String GET_CURSOR_DATA = "https://api-test.whitesplatform.com/v1/cursor/";
    private static final String GET_PRODUCT = "https://api-test.whitesplatform.com/v1/table/product/";
    private static final String COMPANY_CODE = "wpadev_motoheadz";

    @Value("${whitesmoto_password}")
    private static String PASSWORD;
    @Value("${whitesmoto_password_identifier}")
    private static String PASSWORD_IDENTIFIER;
    private String accessToken;

    private final WhitesmotoConverter whitesmotoConverter;

    @Override
    public Map<String,List<String>> parseToProductsCsv() {

        List<WhitesmotoProduct> whitesmotoProducts = getProducts();

//        saveCsvFile(new ArrayList<>(whitesmotoProducts),whitesmotoConverter, WhitesmotoController.PRODUCT_CSV_PATH,WhitesmotoController.INVENTORY_CSV_PATH);
//
        Map<String,List<String>> map = new HashMap<>();
//        map.put("products",new ArrayList<>(List.of(WhitesmotoController.PRODUCT_CSV_PATH)));
//        map.put("inventory",new ArrayList<>(List.of(WhitesmotoController.INVENTORY_CSV_PATH)));
        return map;
    }

    public List<WhitesmotoProduct> getProducts() {

        refreshAccessToken();

        var cursorId = createProductsCursor();
        var productsQty = getCursorData(cursorId);//todo many pages
        var products = getProductsByCodes(productsQty);

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
                        products.add(objectMapper.readValue(responseBody, new TypeReference<>() {
                        }));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Request error: " + e.getMessage(), e);
            }

        }

        return products;
    }


    private List<ProductQty> getCursorData(String cursorId) {

        List<ProductQty> productQtyList = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(GET_CURSOR_DATA + cursorId);

            request.addHeader("X-Whites-Access-Token", this.accessToken);
            request.addHeader("X-Company-Code", COMPANY_CODE);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {

                    String responseBody = EntityUtils.toString(entity);
                    ObjectMapper objectMapper = new ObjectMapper();

                   productQtyList.add(objectMapper.readValue(responseBody, new TypeReference<>() {
                   }));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Request error: " + e.getMessage(), e);
        }
        return productQtyList;

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
                    return jsonObject.get("id").getAsString();
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
