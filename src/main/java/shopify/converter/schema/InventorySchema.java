package shopify.converter.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({
        "Handle", "Title", "Option1 Name", "Option1 Value", "Option2 Name", "Option2 Value",
        "Option3 Name", "Option3 Value", "SKU", "HS Code", "COO", "Location", "Incoming",
        "Unavailable", "Committed", "Available", "On hand"
})
//@JsonIgnoreProperties({"HS Code", "COO","Incoming","Unavailable", "Committed"})
public class InventorySchema implements CSVSchema {

    @JsonProperty("Handle")
    private String handle;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Option1 Name")
    private String option1Name;

    @JsonProperty("Option1 Value")
    private String option1Value;

    @JsonProperty("Option2 Name")
    private String option2Name;

    @JsonProperty("Option2 Value")
    private String option2Value;

    @JsonProperty("Option3 Name")
    private String option3Name;

    @JsonProperty("Option3 Value")
    private String option3Value;

    @JsonProperty("SKU")
    private String sku;

    @JsonProperty("HS Code")
    private String hsCode;

    @JsonProperty("COO")
    private String coo;

    @JsonProperty("Location")
    private String location;

    @JsonProperty("Incoming")
    private Integer incoming;

    @JsonProperty("Unavailable")
    private Integer unavailable;

    @JsonProperty("Committed")
    private Integer committed;

    @JsonProperty("Available")
    private Integer available;

    @JsonProperty("On hand")
    private Integer onHand;

}
