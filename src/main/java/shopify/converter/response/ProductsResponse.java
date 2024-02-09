package shopify.converter.response;

import lombok.Data;
import shopify.converter.model.Product;

import java.util.List;

@Data
public class ProductsResponse {

    private List<Product> products;

}
