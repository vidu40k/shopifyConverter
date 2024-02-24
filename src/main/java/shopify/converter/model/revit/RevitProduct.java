package shopify.converter.model.revit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import shopify.converter.model.VendorProduct;

import java.util.List;

@Data
public class RevitProduct extends VendorProduct {

    @JsonProperty("title")
    private String title;

    @JsonProperty("handle")
    private String handle;

    @JsonProperty("body_html")
    private String bodyHtml;

    @JsonProperty("vendor")
    private String vendor;

    @JsonProperty("product_type")
    private String productType;

    @JsonUnwrapped
    @JsonProperty("tags")
    private List<String> tags;

    @JsonUnwrapped
    @JsonProperty("variants")
    private List<Variants> variants;


    @JsonProperty("images")
    private List<FeaturedImage> images;

    @JsonProperty("options")
    private List<Option> options;


}
