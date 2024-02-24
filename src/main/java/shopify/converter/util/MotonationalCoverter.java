package shopify.converter.util;

import org.springframework.stereotype.Component;
import shopify.converter.model.Motonational.MotonationalProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.util.ArrayList;
import java.util.List;

@Component
public class MotonationalCoverter {

    public List<InventorySchema> convertToInventorySchema(MotonationalProduct product) {




        return new ArrayList<>();
    }


    public List<ProductSchema> convertToProductSchema(MotonationalProduct product){



        return new ArrayList<>();
    }


}
