package shopify.converter.model.whitesmoto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopify.converter.model.VendorProduct;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhitesmotoProduct extends VendorProduct {

    @JsonProperty("productcode")
    private String productCode;

    @JsonProperty("price_rrp")
    private String priceRRP;

    @JsonProperty("price_wholesale")
    private String priceWholesale;

    @JsonProperty("description")
    private String description;

    @JsonProperty("modifieddate")
    private String modifiedDate;

    @JsonProperty("modifiedtime")
    private String modifiedTime;

    @JsonProperty("inactive")
    private String inactive;

    @JsonProperty("discontinued")
    private String discontinued;

    @JsonProperty("stockgroupcode")
    private String stockGroupCode;

    @JsonProperty("salesgroupcode")
    private String salesGroupCode;

    @JsonProperty("z_indent")
    private String indent;

    @JsonProperty("z_webtitle")
    private String webTitle;

    @JsonProperty("quantityinstock")
    private String quantityInStock;

    @JsonProperty("quantityreturned")
    private String quantityReturned;

    @JsonProperty("quantitytransferin")
    private String quantityTransferIn;

    @JsonProperty("quantitytransferout")
    private String quantityTransferOut;

    @JsonProperty("quantityinproduction")
    private String quantityInProduction;

    @JsonProperty("quantitycommitted")
    private String quantityCommitted;

    @JsonProperty("quantityshipped")
    private String quantityShipped;

    @JsonProperty("quantityonorder")
    private String quantityOnOrder;

    @JsonProperty("quantityonbackorder")
    private String quantityOnBackOrder;

    @JsonProperty("quantityallocated")
    private String quantityAllocated;

    @JsonProperty("z_dangerousgoods")
    private String dangerousGoods;

    @JsonProperty("z_isfitment")
    private String isFitment;

    @JsonProperty("nondiminishing")
    private String nonDiminishing;

    @JsonProperty("z_discontinueddate")
    private String discontinuedDate;

    @JsonProperty("z_isnewproductdate")
    private String isNewProductDate;

    @JsonProperty("z_isnewproduct")
    private String isNewProduct;

    @JsonProperty("z_aliasfrom")
    private String zAliasFrom;

    @JsonProperty("category2")
    private String category2;

    @JsonProperty("barcode")
    private String barcode;

    @JsonProperty("manufacturer")
    private String manufacturer;

    @JsonProperty("range")
    private String range;

    @JsonProperty("model")
    private String model;

    @JsonProperty("size")
    private String size;

    @JsonProperty("aliasfrom")
    private String aliasFrom;

    @JsonProperty("images")
    private List<Image> images;

    @JsonProperty("rich_description")
    private String richDescription;

    @JsonProperty("your_price")
    private String yourPrice;

    @Data
    public static class Image {
        @JsonProperty("imagefilename")
        private String imageFilename;

        @JsonProperty("displayorderpreference")
        private String displayOrderPreference;

        @JsonProperty("image_active")
        private String imageActive;

        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("image_order")
        private String imageOrder;
    }
}