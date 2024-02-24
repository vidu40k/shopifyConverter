package shopify.converter.model.revit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FeaturedImage {

    @JsonProperty("position")
    private Integer position;

    @JsonProperty("alt")
    private String alt;

    @JsonProperty("src")
    private String src;

    @JsonProperty("variant_ids")
    private List<String> variantIds;
}

