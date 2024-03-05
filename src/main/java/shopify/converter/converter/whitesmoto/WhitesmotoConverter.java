package shopify.converter.converter.whitesmoto;

import org.springframework.stereotype.Component;
import shopify.converter.converter.ProductConverter;
import shopify.converter.model.VendorProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.util.List;

@Component
public class WhitesmotoConverter extends ProductConverter {
    @Override
    public List<ProductSchema> convertToProductSchema(List<VendorProduct> product) {
        return null;
    }

    @Override
    public List<InventorySchema> convertToInventorySchema(List<VendorProduct> vendorProduct) {
        return null;
    }
}
