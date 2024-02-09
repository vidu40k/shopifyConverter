package shopify.converter.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Variants {

    private String option1;
    private String option2;
    private String option3;
    private String sku;
    private Integer grams;
    private String inventoryTracker;//todo
    private Integer inventoryQty;//todo
    private String inventoryPolicy;//todo
    private String fulfillmentService;//todo
    private Double price;
    private Double compareAtPrice;
    private Boolean taxable;
    private String barcode;
    private Boolean available;

    @JsonProperty("requires_shipping")
    private boolean requiresShipping;

    @JsonIgnore
    private Map<String, String> properties = new HashMap<>();

    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
    }


    @JsonProperty("featured_image")
    private FeaturedImage featuredImage;

}
