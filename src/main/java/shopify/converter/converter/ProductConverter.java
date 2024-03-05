package shopify.converter.converter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import shopify.converter.model.VendorProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.util.List;

public abstract class ProductConverter {


    public abstract List<ProductSchema> convertToProductSchema(List<VendorProduct> product);

    public abstract List<InventorySchema> convertToInventorySchema(List<VendorProduct> vendorProduct);


    protected String convertString(String stringToConvert) {

        stringToConvert = replaceQuotesInsideTags(stringToConvert);
        stringToConvert = stringToConvert.replaceAll("\\\\n", "");
        stringToConvert = stringToConvert.replace("\"", "\"\"");
        return "\"" + stringToConvert + "\"";
    }

    private String replaceQuotesInsideTags(String html) {
        StringBuilder result = new StringBuilder();
        StringBuilder currentTag = new StringBuilder();
        boolean insideTag = false;

        for (int i = 0; i < html.length(); i++) {
            char currentChar = html.charAt(i);

            if (currentChar == '<') {
                insideTag = true;
                currentTag = new StringBuilder();
            } else if (currentChar == '>') {
                insideTag = false;
                result.append(currentTag);
                result.append(currentChar);
                continue;
            }

            if (insideTag) {
                currentTag.append(currentChar);
            } else {
                if (currentChar == '"') {
                    result.append("&quot;");
                } else {
                    result.append(currentChar);
                }
            }
        }

        return result.toString();
    }

    protected String replaceHeadersWithH6(String htmlBody) {
        htmlBody = htmlBody.replaceAll("<h2", "<h6");
        htmlBody = htmlBody.replaceAll("</h2>", "</h6>");
        htmlBody = htmlBody.replaceAll("<h3", "<h6");
        htmlBody = htmlBody.replaceAll("</h3>", "</h6>");
        return htmlBody;
    }


}
