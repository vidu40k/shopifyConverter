package shopify.converter.util;


import org.springframework.stereotype.Component;
import shopify.converter.model.revit.FeaturedImage;
import shopify.converter.model.revit.Option;
import shopify.converter.model.revit.RevitProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class RevitConverter {

    public List<InventorySchema> convertToInventorySchema(RevitProduct revitProduct) {

        List<InventorySchema> inventorySchemas = new LinkedList<>();
        List<String> optionsName = revitProduct.getOptions().stream()
                .map(Option::getName)
                .toList();

        for (var variant : revitProduct.getVariants()) {

            inventorySchemas.add(
                    InventorySchema.builder()
                            .handle(revitProduct.getHandle())
                            .title(revitProduct.getTitle())
                            .option1Name(optionsName.isEmpty() ? "Title" : optionsName.get(0)) //For products that have no options, this should be set to "Title".
                            .option1Value(variant.getOption1() == null ? "Default Title" : variant.getOption1()) //For products that have no options, this should be set to "Default Title".
                            .option2Name(optionsName.size() > 1 ? optionsName.get(1) : null)
                            .option2Value(variant.getOption2())
                            .option3Name(optionsName.size() > 2 ? optionsName.get(2) : null)
                            .option3Value(variant.getOption3())
                            .sku(variant.getSku())
                            .hsCode("")
                            .coo("")
                            .location("Distribution Warehouse")
                            .incoming(null)
                            .unavailable(null)
                            .committed(null)
                            .available(variant.getAvailable() ? 10 : 0)
                            .onHand(variant.getAvailable() ? 10 : 0)
                            .build());

        }
        return inventorySchemas;
    }


    public List<ProductSchema> convertToProductSchema(RevitProduct revitProduct) {

        List<ProductSchema> productSchemas = new ArrayList<>();
        List<String> optionsName = revitProduct.getOptions().stream()
                .map(Option::getName)
                .toList();

        boolean isFirstVariant = true;
        for (var variant : revitProduct.getVariants()) {

            ProductSchema productSchema = ProductSchema.builder()
                    .handle(revitProduct.getHandle())
                    .option1Value(variant.getOption1() == null ? "Default Title" : variant.getOption1()) //For products that have no options, this should be set to "Default Title".
                    .option2Value(variant.getOption2())
                    .option3Value(variant.getOption3())
                    .variantSku(variant.getSku())
                    .variantGrams(variant.getGrams())
                    .variantInventoryTracker("shopify")
                    .variantInventoryQty(variant.getAvailable() ? 10 : 0)
                    .variantInventoryPolicy("deny")
                    .variantFulfillmentService("manual")
                    .variantPrice(variant.getPrice())
                    .variantCompareAtPrice(variant.getCompareAtPrice())
                    .variantRequiresShipping(variant.isRequiresShipping())
                    .variantTaxable(variant.getTaxable())
                    .variantBarcode(variant.getBarcode())
                    .imageSrc(variant.getFeaturedImage() == null ? "" : variant.getFeaturedImage().getSrc())
                    .imagePosition(variant.getFeaturedImage() == null ? null : variant.getFeaturedImage().getPosition())
                    .imageAltText(variant.getFeaturedImage() == null ? "" : variant.getFeaturedImage().getAlt())
                    .variantImage(variant.getFeaturedImage() == null ? "" : variant.getFeaturedImage().getSrc())
                    .variantTaxCode("")
                    .costPerItem(variant.getPrice())
                    .variantWeightUnit("kg")
                    .build();

            if (isFirstVariant) {

                isFirstVariant = false;

                productSchema.setTitle(revitProduct.getTitle());
                productSchema.setBodyHtml(convertString(revitProduct.getBodyHtml()));
                productSchema.setVendor(revitProduct.getVendor());
                productSchema.setProductCategory("");
                productSchema.setType(revitProduct.getProductType());
//                productSchema.setTags("\"" + String.join(",", revitProduct.getTags()) + "\"");
                productSchema.setPublished(true);
                productSchema.setOption1Name(optionsName.isEmpty() ? "Title" : optionsName.get(0)); //For products that have no options, this should be set to "Title".
                productSchema.setOption2Name(optionsName.size() > 1 ? optionsName.get(1) : "");
                productSchema.setOption3Name(optionsName.size() > 2 ? optionsName.get(2) : "");
                productSchema.setGiftCard("");
                productSchema.setSeoTitle(convertString(revitProduct.getTitle()));
                productSchema.setSeoDescription(convertString(extractContentInSpans(revitProduct.getBodyHtml())));
                productSchema.setGoogleShoppingProductCategory("");
                productSchema.setGoogleShoppingGender("");
                productSchema.setGoogleShoppingAgeGroup("");
                productSchema.setGoogleShoppingMPN("");
                productSchema.setGoogleShoppingCondition("");
                productSchema.setGoogleShoppingCustomProduct("");
                productSchema.setGoogleShoppingCustomLabel0("");
                productSchema.setGoogleShoppingCustomLabel1("");
                productSchema.setGoogleShoppingCustomLabel2("");
                productSchema.setGoogleShoppingCustomLabel3("");
                productSchema.setGoogleShoppingCustomLabel4("");
                productSchema.setIncludedAustralia(true);
                productSchema.setIncludedInternational(true);
                productSchema.setStatus("draft");
            }

            productSchemas.add(productSchema);
        }
        for (FeaturedImage featuredImage : revitProduct.getImages()){
            if (featuredImage.getVariantIds().isEmpty()){

                ProductSchema productSchema = ProductSchema.builder()
                        .imagePosition(featuredImage.getPosition())
                        .imageSrc(featuredImage.getSrc())
                        .handle(revitProduct.getHandle())
                        .build();
                productSchemas.add(productSchema);
            }
        }

        return productSchemas;
    }

    private String convertString(String stringToConvert) {

        stringToConvert = stringToConvert.replace("\"", "\"\"");
        return "\"" + stringToConvert + "\"";

    }

    private String extractContentInSpans(String inputString) {

        StringBuilder contentBuilder = new StringBuilder();
        int startIndex = inputString.indexOf("<span");

        while (startIndex != -1) {
            int endIndex = inputString.indexOf("</span>", startIndex);

            if (endIndex != -1) {
                String spanContent = inputString.substring(startIndex + 5, endIndex);
                contentBuilder.append(spanContent);
                startIndex = inputString.indexOf("<span", endIndex);
            } else {
                break;
            }
        }
        String contentWithoutTags = contentBuilder.toString().replaceAll("<.*?>", "");
        contentWithoutTags = contentWithoutTags.replaceAll("[<>]", "");

        return contentWithoutTags.trim();
    }

}
