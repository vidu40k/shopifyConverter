package shopify.converter.model.Motonational;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import shopify.converter.model.VendorProduct;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MotonationalProduct extends VendorProduct {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("SKU")
    private String sku;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Published")
    private String published;

    @JsonProperty("Is featured?")
    private String isFeatured;

    @JsonProperty("Visibility in catalogue")
    private String visibilityInCatalogue;

    @JsonProperty("Short description")
    private String shortDescription;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Date sale price starts")
    private String dateSalePriceStarts;

    @JsonProperty("Date sale price ends")
    private String dateSalePriceEnds;

    @JsonProperty("Tax status")
    private String taxStatus;

    @JsonProperty("Tax class")
    private String taxClass;

    @JsonProperty("In stock?")
    private String inStock;

    @JsonProperty("Stock")
    private String stock;

    @JsonProperty("Low stock amount")
    private String lowStockAmount;

    @JsonProperty("Backorders allowed?")
    private String backordersAllowed;

    @JsonProperty("Sold individually?")
    private String soldIndividually;

    @JsonProperty("Weight (kg)")
    private String weight;

    @JsonProperty("Length (cm)")
    private String length;

    @JsonProperty("Width (cm)")
    private String width;

    @JsonProperty("Height (cm)")
    private String height;

    @JsonProperty("Allow customer reviews?")
    private String allowCustomerReviews;

    @JsonProperty("Purchase note")
    private String purchaseNote;

    @JsonProperty("Sale price")
    private String salePrice;

    @JsonProperty("Regular price")
    private String regularPrice;

    @JsonProperty("Categories")
    private String categories;

    @JsonProperty("Tags")
    private String tags;

    @JsonProperty("Shipping class")
    private String shippingClass;

    @JsonProperty("Images")
    private String images;

    @JsonProperty("Download limit")
    private String downloadLimit;

    @JsonProperty("Download expiry days")
    private String downloadExpiryDays;

    @JsonProperty("Parent")
    private String parent;

    @JsonProperty("Grouped products")
    private String groupedProducts;

    @JsonProperty("Upsells")
    private String upsells;

    @JsonProperty("Cross-sells")
    private String crossSells;

    @JsonProperty("External URL")
    private String externalUrl;

    @JsonProperty("Button text")
    private String buttonText;

    @JsonProperty("Position")
    private String position;

    @JsonProperty("Attribute 1 name")
    private String attribute1Name = "";

    @JsonProperty("Attribute 1 value(s)")
    private String attribute1Values = "";

    @JsonProperty("Attribute 1 visible")
    private String attribute1Visible;

    @JsonProperty("Attribute 1 global")
    private String attribute1Global;

    @JsonProperty("Attribute 2 name")
    private String attribute2Name;

    @JsonProperty("Attribute 2 value(s)")
    private String attribute2Values;

    @JsonProperty("Attribute 2 visible")
    private String attribute2Visible;

    @JsonProperty("Attribute 2 global")
    private String attribute2Global;

    @JsonProperty("Attribute 3 name")
    private String attribute3Name;

    @JsonProperty("Attribute 3 value(s)")
    private String attribute3Values;

    @JsonProperty("Attribute 3 visible")
    private String attribute3Visible;

    @JsonProperty("Attribute 3 global")
    private String attribute3Global;

    @JsonProperty("Meta: slide_template")
    private String metaSlideTemplate;

    @JsonProperty("Meta: rs_page_bg_color")
    private String metaRsPageBgColor;

    @JsonProperty("Meta: product_image_on_hover")
    private String metaProductImageOnHover;

    @JsonProperty("Meta: custom_tab_priority1")
    private String metaCustomTabPriority1;

    @JsonProperty("Meta: custom_tab_priority2")
    private String metaCustomTabPriority2;

    @JsonProperty("Meta: header_view")
    private String metaHeaderView;

    @JsonProperty("Meta: layout")
    private String metaLayout;

    @JsonProperty("Meta: product_layout")
    private String metaProductLayout;

    @JsonProperty("Meta: custom_tab_title2")
    private String metaCustomTabTitle2;

    @JsonProperty("Meta: _prevent_add_to_cart_button")
    private String metaPreventAddToCartButton;

    @JsonProperty("Meta: _wp_desired_post_slug")
    private String metaWpDesiredPostSlug;

    @JsonProperty("Meta: fb_product_group_id")
    private String metaFbProductGroupId;

    @JsonProperty("Meta: fb_product_item_id")
    private String metaFbProductItemId;

    @JsonProperty("Meta: _wc_facebook_commerce_enabled")
    private String metaWcFacebookCommerceEnabled;

    @JsonProperty("Meta: _wc_facebook_sync_enabled")
    private String metaWcFacebookSyncEnabled;

    @JsonProperty("Meta: fb_visibility")
    private String metaFbVisibility;

    @JsonProperty("Meta: fb_product_description")
    private String metaFbProductDescription;

    @JsonProperty("Meta: _wc_facebook_product_image_source")
    private String metaWcFacebookProductImageSource;

    @JsonProperty("Meta: fb_product_image")
    private String metaFbProductImage;

    @JsonProperty("Meta: fb_product_price")
    private String metaFbProductPrice;

    @JsonProperty("Meta: _wc_facebook_google_product_category")
    private String metaWcFacebookGoogleProductCategory;

    @JsonProperty("Meta: _wc_facebook_enhanced_catalog_attributes_size")
    private String metaWcFacebookEnhancedCatalogAttributesSize;

    @JsonProperty("Meta: _wc_facebook_enhanced_catalog_attributes_brand")
    private String metaWcFacebookEnhancedCatalogAttributesBrand;

    @JsonProperty("Meta: _wc_facebook_enhanced_catalog_attributes_pattern")
    private String metaWcFacebookEnhancedCatalogAttributesPattern;

    @JsonProperty("Meta: _wc_facebook_enhanced_catalog_attributes_material")
    private String metaWcFacebookEnhancedCatalogAttributesMaterial;

    @JsonProperty("Meta: _wc_facebook_enhanced_catalog_attributes_age_group")
    private String metaWcFacebookEnhancedCatalogAttributesAgeGroup;

    @JsonProperty("Meta: _wp_old_date")
    private String metaWpOldDate;

    @JsonProperty("Meta: _wc_facebook_enhanced_catalog_attributes_color")
    private String metaWcFacebookEnhancedCatalogAttributesColor;

    @JsonProperty("Meta: _wc_facebook_enhanced_catalog_attributes_gender")
    private String metaWcFacebookEnhancedCatalogAttributesGender;

    @JsonIgnore
    private Map<String, String> properties = new HashMap<>();

    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
    }

}
