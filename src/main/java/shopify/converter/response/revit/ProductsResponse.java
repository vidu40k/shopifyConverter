package shopify.converter.response.revit;

import lombok.Data;
import shopify.converter.model.revit.RevitProduct;

import java.util.List;

@Data
public class ProductsResponse {

    private List<RevitProduct> products;

}
