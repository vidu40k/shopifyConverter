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
import shopify.converter.model.VendorProduct;
import shopify.converter.response.revit.ProductsResponse;
import shopify.converter.service.ProductService;
import shopify.converter.converter.revit.RevitConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevitService extends ProductService {

    private final RevitConverter revitConverter;

    public void parseToProductsCsv(String url) throws RuntimeException {

        ProductsResponse productsResponse = getProductResponse(url);
        if (productsResponse != null) {
            var products = productsResponse.getProducts();

            if (!products.isEmpty()) {
                List<VendorProduct> vendorProducts = new ArrayList<>(products);
                saveCsvFile(vendorProducts, revitConverter);
            }else {
                throw new RuntimeException("cannot get products from api");
            }

        }
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
