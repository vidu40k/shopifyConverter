package shopify.converter.service.revit;

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
import shopify.converter.controller.revit.RevitController;
import shopify.converter.controller.whitesmoto.WhitesmotoController;
import shopify.converter.model.VendorProduct;
import shopify.converter.response.revit.ProductsResponse;
import shopify.converter.service.ProductService;
import shopify.converter.converter.revit.RevitConverter;
import shopify.converter.util.FileCleanupScheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RevitService extends ProductService {

    private static final String REVIT_REQUEST = "https://www.revitaustralia.com.au/products.json?limit=250&page=1";
    private final RevitConverter revitConverter;
    private final FileCleanupScheduler fileCleanupScheduler;

    @Override
    public Map<String,List<String>> parseToProductsCsv() throws RuntimeException {

        ProductsResponse productsResponse = getProductResponse(REVIT_REQUEST);
        if (productsResponse != null) {
            var products = productsResponse.getProducts();

            if (!products.isEmpty()) {
                List<VendorProduct> vendorProducts = new ArrayList<>(products);
                saveCsvFile(vendorProducts, revitConverter, RevitController.PRODUCT_CSV_PATH, RevitController.INVENTORY_CSV_PATH);

                fileCleanupScheduler.addFilePath(RevitController.PRODUCT_CSV_PATH);
                fileCleanupScheduler.addFilePath(RevitController.INVENTORY_CSV_PATH);

            } else {
                throw new RuntimeException("cannot get products from api");
            }
        }

        Map<String,List<String>> map = new HashMap<>();
        map.put("products",new ArrayList<>(List.of(RevitController.PRODUCT_CSV_PATH)));
        map.put("inventory",new ArrayList<>(List.of(RevitController.INVENTORY_CSV_PATH)));
        return map;
    }

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

}
