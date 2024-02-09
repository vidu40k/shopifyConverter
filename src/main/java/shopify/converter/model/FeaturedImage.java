package shopify.converter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FeaturedImage {

    @JsonProperty("position")
    private Integer position;

    @JsonProperty("alt")
    private String alt;

    @JsonProperty("src")
    private String src;

}

