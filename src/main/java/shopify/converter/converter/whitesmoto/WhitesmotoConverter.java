package shopify.converter.converter.whitesmoto;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import shopify.converter.converter.ProductConverter;
import shopify.converter.model.Motonational.MotonationalProduct;
import shopify.converter.model.VendorProduct;
import shopify.converter.model.whitesmoto.WhitesmotoProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.util.*;

@Component
public class WhitesmotoConverter extends ProductConverter {
    @Override
    public List<ProductSchema> convertToProductSchema(List<VendorProduct> vendorProducts) {

        List<ProductSchema> productSchemas = new ArrayList<>();
        Map<String, List<WhitesmotoProduct>> productsMap = groupProductsByRange(vendorProducts);

        for (String range : productsMap.keySet()) {

            List<WhitesmotoProduct> rangeProducts = productsMap.get(range);
            var parentProductSchema = createVariable(rangeProducts.get(0));
            Set<WhitesmotoProduct.Image> images = new LinkedHashSet<>();

            boolean isFirstVariation = true;
            for (var product : rangeProducts) {

                if (isFirstVariation) {
                    productSchemas.add(createFirstVariation(product, parentProductSchema));
                    isFirstVariation = false;
                } else
                    productSchemas.add(createVariation(product, parentProductSchema));

                images.addAll(getUnusedImages(product));
            }

            for (var image : images) {
                productSchemas.add(createImageProductSchema(parentProductSchema, image.getImageUrl()));
            }
        }

        return productSchemas;
    }

    @Override
    public List<InventorySchema> convertToInventorySchema(List<VendorProduct> vendorProducts) {

        List<InventorySchema> inventorySchemas = new ArrayList<>();
        Map<String, List<WhitesmotoProduct>> productsMap = groupProductsByRange(vendorProducts);

        for (String range : productsMap.keySet()) {

            List<WhitesmotoProduct> rangeProducts = productsMap.get(range);
            var parentInventorySchema = createParentInventorySchema(rangeProducts.get(0));

            for (var product : rangeProducts) {
                inventorySchemas.add(createInventorySchema(product, parentInventorySchema));
            }

        }

        return inventorySchemas;

    }


    private InventorySchema createParentInventorySchema(WhitesmotoProduct product) {

        return InventorySchema
                .builder()
                .handle(createHandle(product.getRange()))
                .title(convertString(product.getWebTitle()))
                .build();
    }

    private InventorySchema createInventorySchema(WhitesmotoProduct product, InventorySchema parentInventoryProduct) {

        return InventorySchema.builder()
                .handle(parentInventoryProduct.getHandle())
                .title(parentInventoryProduct.getTitle())
                .option1Name( product.getSize().isEmpty() ? "Title" : "Size")
                .option1Value(product.getSize().isEmpty() ? "Default Title" : product.getSize())
                .option2Name("")
                .option2Value("")
                .option3Name("")
                .option3Value("")
                .sku(product.getProductCode())
                .hsCode("")
                .coo("")
                .location("Distribution Warehouse")
                .incoming(null)
                .unavailable(null)
                .committed(null)
                .available(Integer.valueOf(product.getQuantityInStock()))
                .onHand(Integer.valueOf(product.getQuantityInStock()))
                .build();

    }

    public Map<String, List<WhitesmotoProduct>> groupProductsByRange(List<VendorProduct> productList) {
        Map<String, List<WhitesmotoProduct>> productMap = new HashMap<>();

        for (VendorProduct vendorProduct : productList) {
            var product = (WhitesmotoProduct) vendorProduct;

            String range = product.getRange();

            List<WhitesmotoProduct> productListForRange = productMap.getOrDefault(range, new ArrayList<>());
            productListForRange.add(product);
            productMap.put(range, productListForRange);
        }

        return productMap;
    }

    private ProductSchema createVariable(WhitesmotoProduct product) {
        return ProductSchema.builder()
                .handle(createHandle(product.getRange()))
                .title(convertString(product.getWebTitle()))
                .bodyHtml(convertString(replaceHeadersWithH6(product.getRichDescription().isEmpty() ? product.getRichDescription() : product.getDescription())))
                .vendor("Whitesmoto")
                .productCategory("")
                .published(!Boolean.parseBoolean(product.getInactive()))
                .option1Name(product.getSize().isEmpty() ? "Title" : "Size")
                .option2Name("")
                .option3Name("")
                .giftCard("")
                .seoTitle(convertString(product.getWebTitle()))
                .seoDescription(convertString(extractTextFromHTML(product.getRichDescription())))
                .includedAustralia(true)
                .includedInternational(true)
                .status("draft")
                .imageSrc(getFirstImage(product.getImages().get(0).getImageUrl()))
                .build();

    }

    private ProductSchema createFirstVariation(WhitesmotoProduct product, ProductSchema parentProduct) {

        return ProductSchema.builder()
                .handle(createHandle(parentProduct.getTitle()))
                .title(parentProduct.getTitle())
                .bodyHtml(parentProduct.getBodyHtml())
                .vendor(parentProduct.getVendor())
                .type(parentProduct.getType())
                .published(parentProduct.getPublished())
                .option1Name(parentProduct.getOption1Name())
                .option1Value(product.getSize().isEmpty() ? "Default Title" : product.getSize())
                .option2Name(parentProduct.getOption2Name())
                .option2Value("")
                .option3Name(parentProduct.getOption3Name())
                .option3Value("")
                .variantSku(product.getProductCode())
                .variantGrams("")
                .variantInventoryTracker("shopify")
//                .variantInventoryQty(product.) //todo
                .variantInventoryPolicy("deny")
                .variantFulfillmentService("manual")
                .variantPrice(isHasPrice(product) ? product.getYourPrice() : "")
                .variantCompareAtPrice(null)
                .variantRequiresShipping(true)
                .variantTaxable(true) //todo
                .variantBarcode(product.getBarcode())
                .imageSrc(parentProduct.getImageSrc())
                .imagePosition("")
                .imageAltText("")
                .giftCard("")
                .seoTitle(convertString(product.getWebTitle()))
                .seoDescription(convertString(extractTextFromHTML(product.getRichDescription())))
                .variantImage(product.getImages().get(0).getImageUrl())
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

    private ProductSchema createVariation(WhitesmotoProduct product, ProductSchema parentProduct) {

        return ProductSchema.builder()
                .handle(createHandle(parentProduct.getTitle()))
                .option1Value(product.getSize().isEmpty() ? "Default Title" : product.getSize())
                .option2Value("")
                .option3Value("")
                .variantSku(product.getProductCode())
                .variantGrams("")
                .variantInventoryTracker("shopify")
                .variantInventoryQty("")//todo
                .variantInventoryPolicy("deny")
                .variantFulfillmentService("manual")
                .variantPrice(isHasPrice(product) ? product.getYourPrice() : "")
                .variantCompareAtPrice(null)
                .variantTaxCode("")
                .variantRequiresShipping(true)
                .variantTaxable(true) //todo
                .variantBarcode("")
                .imageSrc(parentProduct.getImageSrc())
                .imagePosition("")
                .giftCard("")
                .variantImage(product.getImages().get(0).getImageUrl())
                .variantTaxCode("")
                .variantWeightUnit("kg")
                .costPerItem("0")
                .build();
    }


    private ProductSchema createImageProductSchema(ProductSchema parent, String imageSrc) {

        return ProductSchema.builder()
                .imagePosition(parent.getImagePosition())
                .imageSrc(imageSrc)
                .handle(parent.getHandle())
                .build();
    }

    private String createHandle(String title) {

        String lowerCase = title.toLowerCase();
        String handle = lowerCase.replaceAll("[^a-z0-9-]", "-");
        handle = handle.replaceAll("-{2,}", "-");
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

    private static boolean isHasPrice(WhitesmotoProduct motonationalProduct) {
        boolean hasPrice = !motonationalProduct.getYourPrice().isEmpty();
        if (hasPrice)
            hasPrice = Double.parseDouble(motonationalProduct.getYourPrice()) > 0;
        return hasPrice;
    }


    private List<WhitesmotoProduct.Image> getUnusedImages(WhitesmotoProduct product) {
        var unusedImg = product.getImages();
        unusedImg.remove(0);
        return new ArrayList<>(unusedImg);
    }
}
