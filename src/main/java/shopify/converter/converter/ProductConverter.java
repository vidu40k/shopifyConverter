package shopify.converter.converter;

import shopify.converter.model.VendorProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.util.List;

public  abstract class ProductConverter {

    public abstract List<ProductSchema> convertToProductSchema(VendorProduct product);

    public abstract List<InventorySchema> convertToInventorySchema(VendorProduct vendorProduct);

}
