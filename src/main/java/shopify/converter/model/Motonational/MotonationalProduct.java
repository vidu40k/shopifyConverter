package shopify.converter.model.Motonational;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import shopify.converter.model.VendorProduct;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MotonationalProduct extends VendorProduct {

    @CsvBindByName(column = "ID")
    private String id;

    @CsvBindByName(column = "Type")
    private String type;

    @CsvBindByName(column = "SKU")
    private String sku;

    @CsvBindByName(column = "Name")
    private String name;

    @CsvBindByName(column = "Published")
    private String published;

    @CsvBindByName(column = "Is featured?")
    private String isFeatured;

    @CsvBindByName(column = "Visibility in catalogue")
    private String visibilityInCatalogue;

    @CsvBindByName(column = "Short description")
    private String shortDescription;

    @CsvBindByName(column = "Description")
    private String description;

    @CsvBindByName(column = "Date sale price starts")
    private String dateSalePriceStarts;

    @CsvBindByName(column = "Date sale price ends")
    private String dateSalePriceEnds;

    @CsvBindByName(column = "Tax status")
    private String taxStatus;

    @CsvBindByName(column = "Tax class")
    private String taxClass;

    @CsvBindByName(column = "In stock?")
    private String inStock;

    @CsvBindByName(column = "Stock")
    private String stock;

    @CsvBindByName(column = "Low stock amount")
    private String lowStockAmount;

    @CsvBindByName(column = "Backorders allowed?")
    private String backordersAllowed;

    @CsvBindByName(column = "Sold individually?")
    private String soldIndividually;

    @CsvBindByName(column = "Weight (kg)")
    private String weight;

    @CsvBindByName(column = "Length (cm)")
    private String length;

    @CsvBindByName(column = "Width (cm)")
    private String width;

    @CsvBindByName(column = "Height (cm)")
    private String height;

    @CsvBindByName(column = "Allow customer reviews?")
    private String allowCustomerReviews;

    @CsvBindByName(column = "Purchase note")
    private String purchaseNote;

    @CsvBindByName(column = "Sale price")
    private String salePrice;

    @CsvBindByName(column = "Regular price")
    private String regularPrice;

    @CsvBindByName(column = "Categories")
    private String categories;

    @CsvBindByName(column = "Tags")
    private String tags;

    @CsvBindByName(column = "Shipping class")
    private String shippingClass;

    @CsvBindByName(column = "Images")
    private String images;

    @CsvBindByName(column = "Download limit")
    private String downloadLimit;

    @CsvBindByName(column = "Download expiry days")
    private String downloadExpiryDays;

    @CsvBindByName(column = "Parent")
    private String parent;

    @CsvBindByName(column = "Grouped products")
    private String groupedProducts;

    @CsvBindByName(column = "Upsells")
    private String upsells;

    @CsvBindByName(column = "Cross-sells")
    private String crossSells;

    @CsvBindByName(column = "External URL")
    private String externalUrl;

    @CsvBindByName(column = "Button text")
    private String buttonText;

    @CsvBindByName(column = "Position")
    private String position;

    @CsvBindByName(column = "Attribute 1 name")
    private String attribute1Name;

    @CsvBindByName(column = "Attribute 1 value(s)")
    private String attribute1Values;

    @CsvBindByName(column = "Attribute 1 visible")
    private String attribute1Visible;

    @CsvBindByName(column = "Attribute 1 global")
    private String attribute1Global;

    @CsvBindByName(column = "Meta: slide_template")
    private String metaSlideTemplate;

    @CsvBindByName(column = "Meta: rs_page_bg_color")
    private String metaRsPageBgColor;

    @CsvBindByName(column = "Meta: product_image_on_hover")
    private String metaProductImageOnHover;

    @CsvBindByName(column = "Meta: custom_tab_priority1")
    private String metaCustomTabPriority1;

    @CsvBindByName(column = "Meta: custom_tab_priority2")
    private String metaCustomTabPriority2;

    @CsvBindByName(column = "Meta: header_view")
    private String metaHeaderView;

    @CsvBindByName(column = "Meta: layout")
    private String metaLayout;

    @CsvBindByName(column = "Meta: product_layout")
    private String metaProductLayout;

    @CsvBindByName(column = "Meta: custom_tab_title2")
    private String metaCustomTabTitle2;

    @CsvBindByName(column = "Meta: _prevent_add_to_cart_button")
    private String metaPreventAddToCartButton;

    @CsvBindByName(column = "Meta: _wp_desired_post_slug")
    private String metaWpDesiredPostSlug;

    @CsvBindByName(column = "Meta: fb_product_group_id")
    private String metaFbProductGroupId;

    @CsvBindByName(column = "Meta: fb_product_item_id")
    private String metaFbProductItemId;

    @CsvBindByName(column = "Meta: _wc_facebook_commerce_enabled")
    private String metaWcFacebookCommerceEnabled;

    @CsvBindByName(column = "Meta: _wc_facebook_sync_enabled")
    private String metaWcFacebookSyncEnabled;

    @CsvBindByName(column = "Meta: fb_visibility")
    private String metaFbVisibility;

    @CsvBindByName(column = "Meta: fb_product_description")
    private String metaFbProductDescription;

    @CsvBindByName(column = "Meta: _wc_facebook_product_image_source")
    private String metaWcFacebookProductImageSource;

    @CsvBindByName(column = "Meta: fb_product_image")
    private String metaFbProductImage;

    @CsvBindByName(column = "Meta: fb_product_price")
    private String metaFbProductPrice;

    @CsvBindByName(column = "Meta: _wc_facebook_google_product_category")
    private String metaWcFacebookGoogleProductCategory;

    @CsvBindByName(column = "Meta: _wc_facebook_enhanced_catalog_attributes_size")
    private String metaWcFacebookEnhancedCatalogAttributesSize;

    @CsvBindByName(column = "Meta: _wc_facebook_enhanced_catalog_attributes_brand")
    private String metaWcFacebookEnhancedCatalogAttributesBrand;

    @CsvBindByName(column = "Meta: _wc_facebook_enhanced_catalog_attributes_pattern")
    private String metaWcFacebookEnhancedCatalogAttributesPattern;

    @CsvBindByName(column = "Meta: _wc_facebook_enhanced_catalog_attributes_material")
    private String metaWcFacebookEnhancedCatalogAttributesMaterial;

    @CsvBindByName(column = "Meta: _wc_facebook_enhanced_catalog_attributes_age_group")
    private String metaWcFacebookEnhancedCatalogAttributesAgeGroup;

    @CsvBindByName(column = "Meta: _wp_old_date")
    private String metaWpOldDate;

    @CsvBindByName(column = "Meta: _wc_facebook_enhanced_catalog_attributes_color")
    private String metaWcFacebookEnhancedCatalogAttributesColor;

    @CsvBindByName(column = "Meta: _wc_facebook_enhanced_catalog_attributes_gender")
    private String metaWcFacebookEnhancedCatalogAttributesGender;

}