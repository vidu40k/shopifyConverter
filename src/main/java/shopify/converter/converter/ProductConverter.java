package shopify.converter.converter;

import shopify.converter.model.VendorProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.util.List;

public abstract class ProductConverter {

    public abstract List<ProductSchema> convertToProductSchema(List<VendorProduct> product);

    public abstract List<InventorySchema> convertToInventorySchema(List<VendorProduct> vendorProduct);

    protected String convertString(String stringToConvert) {



        stringToConvert = stringToConvert.replaceAll("\\\\n", "");
        stringToConvert = stringToConvert.replace("\"", "\"\"");
        return "\"" + stringToConvert + "\"";

    }

    protected String replaceHeadersWithH6(String htmlBody) {
        htmlBody = htmlBody.replaceAll("<h2", "<h6");
        htmlBody = htmlBody.replaceAll("</h2>", "</h6>");

        htmlBody = htmlBody.replaceAll("<h3", "<h6");
        htmlBody = htmlBody.replaceAll("</h3>", "</h6>");

        return htmlBody;
    }

}
