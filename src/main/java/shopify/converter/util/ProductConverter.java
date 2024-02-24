package shopify.converter.util;

import shopify.converter.model.VendorProduct;
import shopify.converter.model.revit.RevitProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.util.List;

public interface ProductConverter {

    List<ProductSchema> convertToProductSchema(VendorProduct product);

    List<InventorySchema> convertToInventorySchema(VendorProduct vendorProduct);

}
