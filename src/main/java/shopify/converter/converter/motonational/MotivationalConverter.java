package shopify.converter.converter.motonational;

import org.springframework.stereotype.Component;
import shopify.converter.converter.ProductConverter;
import shopify.converter.model.Motonational.MotonationalProduct;
import shopify.converter.model.VendorProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.util.ArrayList;
import java.util.List;

@Component
public class MotivationalConverter extends ProductConverter {


    @Override
    public List<ProductSchema> convertToProductSchema(VendorProduct vendorProduct) {

        MotonationalProduct motonationalProduct = (MotonationalProduct) vendorProduct;
        List<ProductSchema> productSchemas = new ArrayList<>();


        return null;
    }

    @Override
    public List<InventorySchema> convertToInventorySchema(VendorProduct vendorProduct) {


        return null;
    }
}
