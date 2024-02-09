package shopify.converter.schema;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonPropertyOrder({
        "Handle", "Title", "Body (HTML)", "Vendor", "Product Category", "Type", "Tags", "Published",
        "Option1 Name", "Option1 Value", "Option2 Name", "Option2 Value", "Option3 Name", "Option3 Value",
        "Variant SKU", "Variant Grams", "Variant Inventory Tracker", "Variant Inventory Qty", "Variant Inventory Policy",
        "Variant Fulfillment Service", "Variant Price", "Variant Compare At Price", "Variant Requires Shipping",
        "Variant Taxable", "Variant Barcode", "Image Src", "Image Position", "Image Alt Text", "Gift Card",
        "SEO Title", "SEO Description", "Google Shopping / Google Product Category", "Google Shopping / Gender",
        "Google Shopping / Age Group", "Google Shopping / MPN", "Google Shopping / Condition",
        "Google Shopping / Custom Product", "Google Shopping / Custom Label 0", "Google Shopping / Custom Label 1",
        "Google Shopping / Custom Label 2", "Google Shopping / Custom Label 3", "Google Shopping / Custom Label 4",
        "Variant Image", "Variant Weight Unit", "Variant Tax Code", "Cost per item", "Included / Australia",
        "Price / Australia", "Compare At Price / Australia", "Included / International", "Price / International",
        "Compare At Price / International", "Status"
})
//@JsonIgnoreProperties({"Product Category", "Gift Card", "Google Shopping / Google Product Category", "Google Shopping / Gender",
//        "Google Shopping / Age Group", "Google Shopping / MPN", "Google Shopping / Condition",
//        "Google Shopping / Custom Product", "Google Shopping / Custom Label 0", "Google Shopping / Custom Label 1",
//        "Google Shopping / Custom Label 2", "Google Shopping / Custom Label 3", "Google Shopping / Custom Label 4",
//        "Variant Tax Code", "Price / Australia", "Compare At Price / Australia", "Included / International",
//        "Price / International", "Compare At Price / International",})
public class ProductSchema implements CSVSchema {


    @JsonProperty("Handle")
    private String handle;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Body (HTML)")
    private String bodyHtml;

    @JsonProperty("Vendor")
    private String vendor;

    @JsonProperty("Product Category")
    private String productCategory;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Tags")
    private String tags;

    @JsonProperty("Published")
    private Boolean published;

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

    @JsonProperty("Variant SKU")
    private String variantSku;

    @JsonProperty("Variant Grams")
    private Integer variantGrams;

    @JsonProperty("Variant Inventory Tracker")
    private String variantInventoryTracker;

    @JsonProperty("Variant Inventory Qty")
    private Integer variantInventoryQty;

    @JsonProperty("Variant Inventory Policy")
    private String variantInventoryPolicy;

    @JsonProperty("Variant Fulfillment Service")
    private String variantFulfillmentService;

    @JsonProperty("Variant Price")
    private Double variantPrice;

    @JsonProperty("Variant Compare At Price")
    private Double variantCompareAtPrice;

    @JsonProperty("Variant Requires Shipping")
    private Boolean variantRequiresShipping;

    @JsonProperty("Variant Taxable")
    private Boolean variantTaxable;

    @JsonProperty("Variant Barcode")
    private String variantBarcode;

    @JsonProperty("Image Src")
    private String imageSrc;

    @JsonProperty("Image Position")
    private Integer imagePosition;

    @JsonProperty("Image Alt Text")
    private String imageAltText;

    @JsonProperty("Gift Card")
    private String giftCard;

    @JsonProperty("SEO Title")
    private String seoTitle;

    @JsonProperty("SEO Description")
    private String seoDescription;

    @JsonProperty("Google Shopping / Google Product Category")
    private String googleShoppingProductCategory;

    @JsonProperty("Google Shopping / Gender")
    private String googleShoppingGender;

    @JsonProperty("Google Shopping / Age Group")
    private String googleShoppingAgeGroup;

    @JsonProperty("Google Shopping / MPN")
    private String googleShoppingMPN;

    @JsonProperty("Google Shopping / Condition")
    private String googleShoppingCondition;

    @JsonProperty("Google Shopping / Custom Product")
    private String googleShoppingCustomProduct;

    @JsonProperty("Google Shopping / Custom Label 0")
    private String googleShoppingCustomLabel0;

    @JsonProperty("Google Shopping / Custom Label 1")
    private String googleShoppingCustomLabel1;

    @JsonProperty("Google Shopping / Custom Label 2")
    private String googleShoppingCustomLabel2;

    @JsonProperty("Google Shopping / Custom Label 3")
    private String googleShoppingCustomLabel3;

    @JsonProperty("Google Shopping / Custom Label 4")
    private String googleShoppingCustomLabel4;

    @JsonProperty("Variant Image")
    private String variantImage;

    @JsonProperty("Variant Weight Unit")
    private String variantWeightUnit;

    @JsonProperty("Variant Tax Code")
    private String variantTaxCode;

    @JsonProperty("Cost per item")
    private Double costPerItem;

    @JsonProperty("Included / Australia")
    private Boolean includedAustralia;

    @JsonProperty("Price / Australia")
    private Double priceAustralia;

    @JsonProperty("Compare At Price / Australia")
    private Double compareAtPriceAustralia;

    @JsonProperty("Included / International")
    private Boolean includedInternational;

    @JsonProperty("Price / International")
    private Double priceInternational;

    @JsonProperty("Compare At Price / International")
    private Double compareAtPriceInternational;

    @JsonProperty("Status")
    private String status;


}
