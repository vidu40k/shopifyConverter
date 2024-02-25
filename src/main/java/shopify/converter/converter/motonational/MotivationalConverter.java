package shopify.converter.converter.motonational;

import org.springframework.stereotype.Component;
import shopify.converter.converter.ProductConverter;
import shopify.converter.model.Motonational.MotonationalProduct;
import shopify.converter.model.VendorProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.util.*;

@Component
public class MotivationalConverter extends ProductConverter {

    @Override
    public List<InventorySchema> convertToInventorySchema(List<VendorProduct> vendorProducts) {

        List<InventorySchema> inventorySchemas = new LinkedList<>();
        Map<String, InventorySchema> variableProducts = new LinkedHashMap<>();

        for (VendorProduct vendorProduct : vendorProducts) {

            MotonationalProduct motonationalProduct = (MotonationalProduct) vendorProduct;

            if (motonationalProduct.getType().equals("variable")) {
                InventorySchema productSchema = createParentInventorySchema(motonationalProduct);
                variableProducts.put(String.valueOf(motonationalProduct.getId()), productSchema);
                continue;
            }

            if (motonationalProduct.getType().equals("variation")) {
                var parentId = extractId(motonationalProduct.getParent());
                var parentProduct = variableProducts.get(parentId);
                inventorySchemas.add(createInventorySchema(motonationalProduct, parentProduct));
                continue;
            }

            if (motonationalProduct.getType().equals("simple")) {
                inventorySchemas.add(createSimpleInventorySchema(motonationalProduct));
            }
        }

        return inventorySchemas;
    }


    @Override
    public List<ProductSchema> convertToProductSchema(List<VendorProduct> vendorProducts) {

        List<ProductSchema> productSchemas = new ArrayList<>();
        Map<String, ProductSchema> variableProducts = new LinkedHashMap<>();
        Map<String, Boolean> productAlreadyHaveFirstVariation = new HashMap<>();

        for (VendorProduct vendorProduct : vendorProducts) {

            MotonationalProduct motonationalProduct = (MotonationalProduct) vendorProduct;

            if (motonationalProduct.getType().equals("variable")) {

                var productSchema = createVariable(motonationalProduct);
                variableProducts.put(String.valueOf(motonationalProduct.getId()), productSchema);
                productAlreadyHaveFirstVariation.put(String.valueOf(motonationalProduct.getId()), false);
                continue;
            }

            if (motonationalProduct.getType().equals("variation")) {

                var parentId = extractId(motonationalProduct.getParent());
                var parentProduct = variableProducts.get(parentId);

                if (!productAlreadyHaveFirstVariation.get(parentId)) {
                    productAlreadyHaveFirstVariation.put(parentId, true);
                    var productSchema = createFirstVariation(motonationalProduct, parentProduct);
                    productSchemas.add(productSchema);
                } else {
                    var productSchema = createVariation(motonationalProduct, parentProduct);
                    productSchemas.add(productSchema);
                }
                continue;
            }
            if (motonationalProduct.getType().equals("simple")) {

                ProductSchema product = createSimple(motonationalProduct);
                productSchemas.add(product);
            }
        }

        return productSchemas;
    }

    private InventorySchema createSimpleInventorySchema(MotonationalProduct motonationalProduct) {

        return InventorySchema.builder().handle(createHandle(motonationalProduct.getName())).title(motonationalProduct.getName()).option1Name(motonationalProduct.getAttribute1Name().isEmpty() ? "Title" : motonationalProduct.getAttribute1Name()) //For products that have no options, this should be set to "Title".
                .option1Value(motonationalProduct.getAttribute1Values().isEmpty() ? "Default Title" : motonationalProduct.getAttribute1Values()) //For products that have no options, this should be set to "Default Title".
                .option2Name("").option2Value("").option3Name("").option3Value("").sku(motonationalProduct.getSku()).hsCode("").coo("").location("Distribution Warehouse").incoming(null).unavailable(null).committed(null).available(motonationalProduct.getInStock().equals("1") ? 10 : 0).onHand(motonationalProduct.getInStock().equals("1") ? 10 : 0).build();
    }

    private InventorySchema createParentInventorySchema(MotonationalProduct motonationalProduct) {

        return InventorySchema.builder().handle(createHandle(motonationalProduct.getName())).title(motonationalProduct.getName()).build();
    }

    private InventorySchema createInventorySchema(MotonationalProduct motonationalProduct, InventorySchema parentInventoryProduct) {

        return InventorySchema.builder().handle(parentInventoryProduct.getHandle()).title(parentInventoryProduct.getTitle()).option1Name(motonationalProduct.getAttribute1Name().isEmpty() ? "Title" : motonationalProduct.getAttribute1Name()) //For products that have no options, this should be set to "Title".
                .option1Value(motonationalProduct.getAttribute1Values().isEmpty() ? "Default Title" : motonationalProduct.getAttribute1Values()) //For products that have no options, this should be set to "Default Title".
                .option2Name("").option2Value("").option3Name("").option3Value("").sku(motonationalProduct.getSku()).hsCode("").coo("").location("Distribution Warehouse").incoming(null).unavailable(null).committed(null).available(motonationalProduct.getInStock().equals("1") ? 10 : 0).onHand(motonationalProduct.getInStock().equals("1") ? 10 : 0).build();
    }

    private ProductSchema createFirstVariation(MotonationalProduct motonationalProduct, ProductSchema parentProduct) {

        return ProductSchema.builder().handle(createHandle(parentProduct.getTitle())).title(parentProduct.getTitle()).bodyHtml(parentProduct.getBodyHtml())//todo
                .vendor(parentProduct.getVendor()).productCategory(parentProduct.getProductCategory())//todo
                .type(parentProduct.getType())//todo
//                        .tags("")
                .published(parentProduct.getPublished()).option1Name(parentProduct.getOption1Name()).option1Value(motonationalProduct.getAttribute1Values()).option2Name("").option2Value("").option3Name("").option3Value("").variantSku(motonationalProduct.getSku()).variantGrams(motonationalProduct.getWeight().isEmpty() ? null : Integer.valueOf(motonationalProduct.getWeight())).variantInventoryTracker("shopify")
//                        .variantInventoryQty(0)//todo
                .variantInventoryPolicy("deny").variantFulfillmentService("manual").variantPrice(Double.valueOf(motonationalProduct.getRegularPrice().equals("default") ? motonationalProduct.getSalePrice() : motonationalProduct.getRegularPrice())).variantCompareAtPrice(null).variantRequiresShipping(true).variantTaxable(motonationalProduct.getTaxStatus().equals("taxable")).variantBarcode("").imageSrc(parentProduct.getImageSrc()).imagePosition(Integer.valueOf(motonationalProduct.getPosition())).imageAltText("").giftCard("").seoTitle(motonationalProduct.getShortDescription()).seoDescription(motonationalProduct.getDescription()).googleShoppingProductCategory("").googleShoppingGender("").googleShoppingAgeGroup("").googleShoppingMPN("").googleShoppingCondition("").googleShoppingCustomProduct("").googleShoppingCustomLabel0("").googleShoppingCustomLabel1("").googleShoppingCustomLabel2("").googleShoppingCustomLabel3("").googleShoppingCustomLabel4("").variantImage("").variantWeightUnit("kg").variantTaxCode("").costPerItem(Double.valueOf(motonationalProduct.getRegularPrice().equals("default") ? motonationalProduct.getSalePrice() : motonationalProduct.getRegularPrice())).includedAustralia(true).priceAustralia(null).compareAtPriceAustralia(null).includedInternational(true).priceInternational(null).compareAtPriceInternational(null).status("draft").build();


    }

    private ProductSchema createVariation(MotonationalProduct motonationalProduct, ProductSchema parentProduct) {

        return ProductSchema.builder().handle(createHandle(parentProduct.getTitle())).option1Value(motonationalProduct.getAttribute1Values()).option2Value("").option3Value("").variantSku(motonationalProduct.getSku()).variantGrams(motonationalProduct.getWeight().isEmpty() ? null : Integer.valueOf(motonationalProduct.getWeight())).variantInventoryTracker("shopify")
//                .variantInventoryQty( )//todo
                .variantInventoryPolicy("deny").variantFulfillmentService("manual").variantPrice(Double.valueOf(motonationalProduct.getRegularPrice().equals("default") ? motonationalProduct.getSalePrice() : motonationalProduct.getRegularPrice())).variantCompareAtPrice(null).variantTaxCode("").variantRequiresShipping(true).variantTaxable(motonationalProduct.getTaxStatus().equals("taxable")).variantBarcode("").imageSrc(parentProduct.getImageSrc()).imagePosition(Integer.valueOf(motonationalProduct.getPosition())).giftCard("").variantImage("").variantTaxCode("").variantWeightUnit("kg").costPerItem(Double.valueOf(motonationalProduct.getRegularPrice().equals("default") ? motonationalProduct.getSalePrice() : motonationalProduct.getRegularPrice())).build();
    }

    private ProductSchema createVariable(MotonationalProduct motonationalProduct) {

        return ProductSchema.builder().handle(createHandle(motonationalProduct.getName())).title(motonationalProduct.getName())
//                        .bodyHtml(convertString(revitProduct.getBodyHtml()))//todo
                .vendor("Motonational").productCategory(motonationalProduct.getCategories())//todo
                .type("")//todo
                .published(motonationalProduct.getPublished().equals("1")).option1Name(motonationalProduct.getAttribute1Name()).option2Name("").option3Name("").giftCard("").seoTitle(motonationalProduct.getShortDescription()).seoDescription(motonationalProduct.getDescription()).googleShoppingProductCategory("").googleShoppingGender("").googleShoppingAgeGroup("").googleShoppingMPN("").googleShoppingCondition("").googleShoppingCustomProduct("").googleShoppingCustomLabel0("").googleShoppingCustomLabel1("").googleShoppingCustomLabel2("").googleShoppingCustomLabel3("").googleShoppingCustomLabel4("").includedAustralia(true).includedInternational(true).status("draft").imageSrc(motonationalProduct.getImages()).build();

    }

    private ProductSchema createSimple(MotonationalProduct motonationalProduct) {
        return ProductSchema.builder().handle(createHandle(motonationalProduct.getName())).title(motonationalProduct.getName())
//                        .bodyHtml()//todo
//                        .vendor("Motonational")//todo
//                        .productCategory("")//todo
//                        .type("")
//                        .tags("")
                .published(Boolean.valueOf(motonationalProduct.getPublished())).option1Name(motonationalProduct.getAttribute1Name()).option1Value(motonationalProduct.getAttribute1Values()).option2Name("").option2Value("").option3Name("").option3Value("").variantSku(motonationalProduct.getSku()).variantGrams(motonationalProduct.getWeight().isEmpty() ? null : Integer.valueOf(motonationalProduct.getWeight())).variantInventoryTracker("shopify")
//                        .variantInventoryQty(0)//todo
                .variantInventoryPolicy("deny").variantFulfillmentService("manual").variantPrice(Double.valueOf(motonationalProduct.getRegularPrice().equals("default") ? motonationalProduct.getSalePrice() : motonationalProduct.getRegularPrice())).variantCompareAtPrice(null).variantRequiresShipping(true).variantTaxable(motonationalProduct.getTaxStatus().equals("taxable")).variantBarcode("").imageSrc(motonationalProduct.getImages()).imagePosition(Integer.valueOf(motonationalProduct.getPosition())).imageAltText("").giftCard("").seoTitle(motonationalProduct.getShortDescription()).seoDescription(motonationalProduct.getDescription()).googleShoppingProductCategory("").googleShoppingGender("").googleShoppingAgeGroup("").googleShoppingMPN("").googleShoppingCondition("").googleShoppingCustomProduct("").googleShoppingCustomLabel0("").googleShoppingCustomLabel1("").googleShoppingCustomLabel2("").googleShoppingCustomLabel3("").googleShoppingCustomLabel4("").variantImage("").variantWeightUnit("kg").variantTaxCode("").costPerItem(null).includedAustralia(true).priceAustralia(null).compareAtPriceAustralia(null).includedInternational(true).priceInternational(null).compareAtPriceInternational(null).status("draft").build();
    }


    private String createHandle(String title) {
        String lowerCase = title.toLowerCase();
        return lowerCase.replace(" ", "-");
    }

    private static String extractId(String input) {
        int colonIndex = input.indexOf(':');
        if (colonIndex != -1 && colonIndex < input.length() - 1) {
            return input.substring(colonIndex + 1).trim();
        }
        return "";
    }
}
