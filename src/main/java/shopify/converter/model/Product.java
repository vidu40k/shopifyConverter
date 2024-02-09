package shopify.converter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.util.List;

@Data
public class Product {

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


    @JsonProperty("Images")
    private List<FeaturedImage> images;

    @JsonProperty("options")
    private List<Option> options;


}
