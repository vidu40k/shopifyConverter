package shopify.converter.converter.motonational;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import shopify.converter.converter.ProductConverter;
import shopify.converter.model.Motonational.MotonationalProduct;
import shopify.converter.model.VendorProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MotivationalConverter extends ProductConverter {


    private Map<String, List<MotonationalProduct>> getProductsByType(List<VendorProduct> vendorProducts) {

        HashMap<String, List<MotonationalProduct>> resultMap = new HashMap<>();

        List<MotonationalProduct> mainProducts = new ArrayList<>();
        List<MotonationalProduct> variations = new ArrayList<>();
        List<MotonationalProduct> simpleProducts = new ArrayList<>();

        for (VendorProduct product : vendorProducts) {
            MotonationalProduct motonationalProduct = (MotonationalProduct) product;

            if ("variable".equals(motonationalProduct.getType())) {
                mainProducts.add(motonationalProduct);
            } else if ("variation".equals(motonationalProduct.getType())) {

                variations.add(motonationalProduct);
            } else if ("simple".equals(motonationalProduct.getType())) {
                simpleProducts.add(motonationalProduct);
            }
        }

        resultMap.put("variable", mainProducts);
        resultMap.put("variation", variations);
        resultMap.put("simple", simpleProducts);

        return resultMap;
    }

    @Override
    public List<InventorySchema> convertToInventorySchema(List<VendorProduct> vendorProducts) {

        List<InventorySchema> inventorySchemas = new ArrayList<>();

        var productsByTypes = getProductsByType(vendorProducts);

        List<MotonationalProduct> mainProducts = productsByTypes.get("variable");
        List<MotonationalProduct> variations = productsByTypes.get("variation");
        List<MotonationalProduct> simpleProducts = productsByTypes.get("simple");

        // Для каждого главного продукта находим соответствующие вариации
        for (MotonationalProduct mainProduct : mainProducts) {

            var parentInventorySchema = createParentInventorySchema(mainProduct);

            for (MotonationalProduct variation : variations) {
                if (mainProduct.getId().equals(extractId(variation.getParent()))) {

                    inventorySchemas.add(createInventorySchema(variation, parentInventorySchema));
                }
            }
        }
        // Записываем простые продукты после вариаций или главных продуктов
        for (MotonationalProduct simpleProduct : simpleProducts) {
            inventorySchemas.add(createSimpleInventorySchema(simpleProduct));
        }

        return inventorySchemas;
    }


    @Override
    public List<ProductSchema> convertToProductSchema(List<VendorProduct> vendorProducts) {

        List<ProductSchema> productSchemas = new ArrayList<>();

        var productsByTypes = getProductsByType(vendorProducts);
        List<MotonationalProduct> mainProducts = productsByTypes.get("variable");
        List<MotonationalProduct> variations = productsByTypes.get("variation");
        List<MotonationalProduct> simpleProducts = productsByTypes.get("simple");

        // Для каждого главного продукта находим соответствующие вариации
        for (MotonationalProduct mainProduct : mainProducts) {

            boolean isFirstVariation = true;
            var parentProductSchema = createVariable(mainProduct);

            List<String> mainProductImages = Arrays.asList(mainProduct.getImages().split(" "));
            List<String> unusedImages = new ArrayList<>(mainProductImages);

            for (MotonationalProduct variation : variations) {
                if (mainProduct.getId().equals(extractId(variation.getParent())) && variation.getName().contains(mainProduct.getName())) {
                    if (isFirstVariation) {
                        productSchemas.add(createFirstVariation(variation, parentProductSchema));
                        isFirstVariation = false;
                    } else {
                        productSchemas.add(createVariation(variation, parentProductSchema));
                    }
                }
            }

            unusedImages.remove(0);
            productSchemas.addAll(getImageProductSchemas(unusedImages, parentProductSchema));
        }

        // Записываем простые продукты после вариаций или главных продуктов
        for (MotonationalProduct simpleProduct : simpleProducts) {

            List<String> mainProductImages = Arrays.asList(simpleProduct.getImages().split(" "));
            List<String> unusedImages = new ArrayList<>(mainProductImages);
            unusedImages.remove(0);

            var parentProductSchema = createSimple(simpleProduct);
            productSchemas.add(parentProductSchema);
            productSchemas.addAll(getImageProductSchemas(unusedImages, parentProductSchema));
        }

        return productSchemas;
    }

    private List<ProductSchema> getImageProductSchemas(List<String> unusedImages, ProductSchema parentProductSchema) {

        List<ProductSchema> productSchemas = new ArrayList<>();
        for (String unusedImage : unusedImages) {
            productSchemas.add(createImageProductSchema(parentProductSchema, unusedImage));
        }
        return productSchemas;
    }

    private ProductSchema createImageProductSchema(ProductSchema parent, String imageSrc) {

        return ProductSchema.builder()
                .imagePosition(parent.getImagePosition())
                .imageSrc(imageSrc)
                .handle(parent.getHandle())
                .build();
    }

    private InventorySchema createSimpleInventorySchema(MotonationalProduct motonationalProduct) {

        return InventorySchema.builder()
                .handle(createHandle(motonationalProduct.getName()))
                .title(motonationalProduct.getName())
                .option1Name(motonationalProduct.getAttribute1Name().isEmpty() ? "Title" : motonationalProduct.getAttribute1Name())
                .option1Value(motonationalProduct.getAttribute1Values().isEmpty() ? "Default Title" : motonationalProduct.getAttribute1Values())
                .option2Name(motonationalProduct.getAttribute2Name())
                .option2Value(motonationalProduct.getAttribute2Values())
                .option3Name(motonationalProduct.getAttribute3Name())
                .option3Value(motonationalProduct.getAttribute3Values())
                .sku(motonationalProduct.getSku())
                .hsCode("")
                .coo("")
                .location("Distribution Warehouse")
                .incoming(null)
                .unavailable(null)
                .committed(null)
                .available(isHasPrice(motonationalProduct) ? 10 : 0)
                .onHand(isHasPrice(motonationalProduct) ? 10 : 0)
                .build();
    }

    private InventorySchema createParentInventorySchema(MotonationalProduct motonationalProduct) {

        return InventorySchema
                .builder()
                .handle(createHandle(motonationalProduct.getName()))
                .title(motonationalProduct.getName())
                .build();
    }

    private InventorySchema createInventorySchema(MotonationalProduct motonationalProduct, InventorySchema parentInventoryProduct) {

        return InventorySchema.builder()
                .handle(parentInventoryProduct.getHandle())
                .title(parentInventoryProduct.getTitle())
                .option1Name(motonationalProduct.getAttribute1Name())
                .option1Value(motonationalProduct.getAttribute1Values())
                .option2Name(motonationalProduct.getAttribute2Name())
                .option2Value(motonationalProduct.getAttribute2Values())
                .option3Name(motonationalProduct.getAttribute3Name())
                .option3Value(motonationalProduct.getAttribute3Values())
                .sku(motonationalProduct.getSku())
                .hsCode("")
                .coo("")
                .location("Distribution Warehouse")
                .incoming(null)
                .unavailable(null)
                .committed(null)
                .available(isHasPrice(motonationalProduct) ? 10 : 0)
                .onHand(isHasPrice(motonationalProduct) ? 10 : 0)
                .build();
    }

    private ProductSchema createFirstVariation(MotonationalProduct motonationalProduct, ProductSchema parentProduct) {

        return ProductSchema.builder()
                .handle(createHandle(parentProduct.getTitle()))
                .title(parentProduct.getTitle())
                .bodyHtml(parentProduct.getBodyHtml())
                .vendor(parentProduct.getVendor())
                .productCategory("")
                .type(parentProduct.getType())
//                        .tags("")
                .published(parentProduct.getPublished())
                .option1Name(motonationalProduct.getAttribute1Name())
                .option1Value(motonationalProduct.getAttribute1Values())
                .option2Name(parentProduct.getOption2Name())
                .option2Value(motonationalProduct.getAttribute2Values())
                .option3Name(parentProduct.getOption3Name())
                .option3Value(motonationalProduct.getAttribute3Values())
                .variantSku(motonationalProduct.getSku())
                .variantGrams(motonationalProduct.getWeight())
                .variantInventoryTracker("shopify")
                .variantInventoryQty(isHasPrice(motonationalProduct) ? "10" : "0")
                .variantInventoryPolicy("deny").variantFulfillmentService("manual")
                .variantPrice(isHasPrice(motonationalProduct) ? motonationalProduct.getRegularPrice() : "")
                .variantCompareAtPrice(null).variantRequiresShipping(true)
                .variantTaxable(motonationalProduct.getTaxStatus().equals("taxable"))
                .variantBarcode("")
                .imageSrc(parentProduct.getImageSrc())
                .imagePosition((motonationalProduct.getPosition()))
                .imageAltText("")
                .giftCard("")
                .seoTitle(motonationalProduct.getName())
                .seoDescription(convertString(extractTextFromHTML(motonationalProduct.getDescription())))
                .variantImage(getFirstImage(motonationalProduct.getImages()))
                .variantWeightUnit("kg")
                .variantTaxCode("")
                .costPerItem("0")
                .includedAustralia(true)
                .priceAustralia(null)
                .compareAtPriceAustralia(null)
                .includedInternational(true)
                .priceInternational(null)
                .compareAtPriceInternational(null)
                .status("draft").build();


    }

    private ProductSchema createVariation(MotonationalProduct motonationalProduct, ProductSchema parentProduct) {

        return ProductSchema.builder()
                .handle(createHandle(parentProduct.getTitle()))
                .option1Value(motonationalProduct.getAttribute1Values())
                .option2Value(motonationalProduct.getAttribute2Values())
                .option3Value(motonationalProduct.getAttribute3Values())
                .variantSku(motonationalProduct.getSku())
                .variantGrams(motonationalProduct.getWeight())
                .variantInventoryTracker("shopify")
                .variantInventoryQty(isHasPrice(motonationalProduct) ? "10" : "0")
                .variantInventoryPolicy("deny")
                .variantFulfillmentService("manual")
                .variantPrice(isHasPrice(motonationalProduct) ? motonationalProduct.getRegularPrice() : "")
                .variantCompareAtPrice(null)
                .variantTaxCode("")
                .variantRequiresShipping(true)
                .variantTaxable(motonationalProduct.getTaxStatus().equals("taxable"))
                .variantBarcode("")
                .imageSrc(parentProduct.getImageSrc())
                .imagePosition((motonationalProduct.getPosition()))
                .giftCard("")
                .variantImage(getFirstImage(motonationalProduct.getImages()))
                .variantTaxCode("")
                .variantWeightUnit("kg")
                .costPerItem("0")
                .build();
    }

    private ProductSchema createVariable(MotonationalProduct motonationalProduct) {
        return ProductSchema.builder()
                .handle(createHandle(motonationalProduct.getName()))
                .title(motonationalProduct.getName())
                .bodyHtml(convertString(replaceHeadersWithH6(motonationalProduct.getDescription().isEmpty() ? motonationalProduct.getShortDescription() : motonationalProduct.getDescription())))
                .vendor("Motonational")
                .productCategory("")
                .type("")//todo
                .published(motonationalProduct.getPublished().equals("1"))
                .option1Name(motonationalProduct.getAttribute1Name())
                .option2Name(motonationalProduct.getAttribute2Name())
                .option3Name(motonationalProduct.getAttribute3Name())
                .giftCard("")
                .seoTitle(motonationalProduct.getName())
                .seoDescription(convertString(extractTextFromHTML(motonationalProduct.getDescription())))
                .includedAustralia(true)
                .includedInternational(true)
                .status("draft")
                .imageSrc(getFirstImage(motonationalProduct.getImages()))
                .build();

    }

    private ProductSchema createSimple(MotonationalProduct motonationalProduct) {
        return ProductSchema.builder().handle(createHandle(motonationalProduct.getName())).title(motonationalProduct.getName())
                .bodyHtml(replaceHeadersWithH6(convertString(motonationalProduct.getDescription())))
                .vendor("Motonational")
                .productCategory("")
                .type("")
//                        .tags("")
                .published(motonationalProduct.getPublished().equals("1"))
                .option1Name(motonationalProduct.getAttribute1Name().isEmpty() ? "Title" : motonationalProduct.getAttribute1Name()) //For products that have no options, this should be set to "Title".
                .option1Value(motonationalProduct.getAttribute1Values().isEmpty() ? "Default Title" : motonationalProduct.getAttribute1Values()) //For products that have no options, this should be set to "Default Title".
                .option2Name(motonationalProduct.getAttribute2Name())
                .option2Value(motonationalProduct.getAttribute2Values())
                .option3Name(motonationalProduct.getAttribute3Name())
                .option3Value(motonationalProduct.getAttribute3Values())
                .variantSku(motonationalProduct.getSku())
                .variantGrams(motonationalProduct.getWeight())
                .variantInventoryTracker("shopify")
                .variantInventoryQty(isHasPrice(motonationalProduct) ? "10" : "0")
                .variantInventoryPolicy("deny")
                .variantFulfillmentService("manual")
                .variantPrice(isHasPrice(motonationalProduct) ? motonationalProduct.getRegularPrice() : "")
                .variantCompareAtPrice(null)
                .variantRequiresShipping(true)
                .variantTaxable(motonationalProduct.getTaxStatus().equals("taxable")).variantBarcode("")
                .imageSrc(getFirstImage(motonationalProduct.getImages()))
                .imagePosition((motonationalProduct.getPosition()))
                .imageAltText("")
                .giftCard("")
                .seoTitle(motonationalProduct.getName())
                .seoDescription(convertString(extractTextFromHTML(motonationalProduct.getDescription())))
                .variantImage("")
                .variantWeightUnit("kg")
                .variantTaxCode("")
                .costPerItem("0")
                .includedAustralia(true)
                .priceAustralia(null)
                .compareAtPriceAustralia(null)
                .includedInternational(true)
                .priceInternational(null)
                .compareAtPriceInternational(null)
                .status("draft").build();
    }


    private String createHandle(String title) {
        String lowerCase = title.toLowerCase();

        String characters = " \\$&`:<>()\\[\\]{}“\\+/'\"^’”‘";
        Pattern pattern = Pattern.compile("[" + Pattern.quote(characters) + "]");
        Matcher matcher = pattern.matcher(lowerCase);
        String handle = matcher.replaceAll("-");

        // Удаление последовательных символов "-"
        handle = handle.replaceAll("-{2,}", "-");

        // Удаление символов "-" в начале и конце строки
        handle = handle.replaceAll("^-|-$", "");

        return handle;
    }

    private static String extractId(String input) {
        int colonIndex = input.indexOf(':');
        if (colonIndex != -1 && colonIndex < input.length() - 1) {
            return input.substring(colonIndex + 1).trim();
        }
        return "";
    }

    public String extractTextFromHTML(String html) {
        StringBuilder textContent = new StringBuilder();

        Document doc = Jsoup.parse(html);

        // Находим все текстовые элементы в документе
        Elements elements = doc.select(":matchesOwn((?i)\\b\\w+\\b)");
        for (Element element : elements) {
            textContent.append(element.text()).append(" ");
        }

        return textContent.toString().trim();
    }

    public String getFirstImage(String images) {
        var imagesList = List.of(images.split(" "));
        if (!imagesList.isEmpty())
            return imagesList.get(0);
        return "";
    }

    private static boolean isHasPrice(MotonationalProduct motonationalProduct) {
        boolean hasPrice = !motonationalProduct.getRegularPrice().isEmpty();
        if (hasPrice)
            hasPrice = Double.parseDouble(motonationalProduct.getRegularPrice()) > 0;
        return hasPrice;
    }

}
