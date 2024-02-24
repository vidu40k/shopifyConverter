package shopify.converter.response;

import lombok.Data;
import shopify.converter.model.revit.RevitProduct;

import java.util.List;

@Data
public class ProductsResponse {

    private List<RevitProduct> revitProducts;

}
