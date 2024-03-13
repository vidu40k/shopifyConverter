package shopify.converter.converter.whitesmoto;

import org.springframework.stereotype.Component;
import shopify.converter.converter.ProductConverter;
import shopify.converter.model.VendorProduct;
import shopify.converter.model.whitesmoto.WhitesmotoProduct;
import shopify.converter.schema.InventorySchema;
import shopify.converter.schema.ProductSchema;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class WhitesmotoConverter extends ProductConverter {
    @Override
    public List<ProductSchema> convertToProductSchema(List<VendorProduct> vendorProducts) {

        List<ProductSchema> productSchemas = new ArrayList<>();
        Map<String, List<WhitesmotoProduct>> productsMap = groupProductsByRange(vendorProducts);

        for (String range : productsMap.keySet()) {

            List<WhitesmotoProduct> rangeProducts = productsMap.get(range);

            if (range.isEmpty()) { //create simple products

                for (var product : rangeProducts) {

                    var simpleSchema = createSimple(product);
                    productSchemas.add(simpleSchema);

                    addImagesToSchemas(new LinkedHashSet<>(product.getImages()), productSchemas, simpleSchema);

                }
            } else { //create products with variants

                var parentProductSchema = createVariable(rangeProducts.get(0));
                Set<WhitesmotoProduct.Image> images = new LinkedHashSet<>();

                boolean isFirstVariation = true;
                for (var product : rangeProducts) {
                    if (isFirstVariation) {
                        productSchemas.add(createFirstVariation(product, parentProductSchema));
                        isFirstVariation = false;
                    } else
                        productSchemas.add(createVariation(product, parentProductSchema));
                    images.addAll(product.getImages());
                }

                addImagesToSchemas(images, productSchemas, parentProductSchema);
            }

        }

        return productSchemas;
    }

    private void addImagesToSchemas(Set<WhitesmotoProduct.Image> images, List<ProductSchema> productSchemas, ProductSchema parentProductSchema) {
        if (images == null || images.isEmpty())
            return;

        AtomicBoolean isFirstSkipped = new AtomicBoolean(false); // Флаг для отслеживания, был ли уже пропущен первый элемент
        images.stream() // Добавление изображений в схемы
                .filter(image -> {
                    String active = image.getImageActive();
                    return active != null && active.equals("true");
                })
                .sorted(Comparator.comparingInt(image -> Integer.parseInt(image.getImageOrder())))
                .forEach(image -> {
                    if (!isFirstSkipped.getAndSet(true)) { // Проверяем, был ли уже пропущен первый элемент
                        return; // Пропускаем первый элемент
                    }
                    productSchemas.add(createImageProductSchema(parentProductSchema, image.getImageUrl())); // Добавляем схему продукта
                });
    }

    @Override
    public List<InventorySchema> convertToInventorySchema(List<VendorProduct> vendorProducts) {

        List<InventorySchema> inventorySchemas = new ArrayList<>();
        Map<String, List<WhitesmotoProduct>> productsMap = groupProductsByRange(vendorProducts);

        for (String range : productsMap.keySet()) {

            List<WhitesmotoProduct> rangeProducts = productsMap.get(range);
            if (range.isEmpty()) {
                for (var product : rangeProducts) {
                    inventorySchemas.add(createSimpleInventorySchema(product));
                }
            } else {

                var parentInventorySchema = createParentInventorySchema(rangeProducts.get(0));
                for (var product : rangeProducts) {
                    inventorySchemas.add(createInventorySchema(product, parentInventorySchema));
                }
            }
        }

        return inventorySchemas;

    }

    private InventorySchema createSimpleInventorySchema(WhitesmotoProduct product) {

        return InventorySchema.builder()
                .handle(createHandle(product.getRange().isEmpty() ? product.getDescription() : product.getRange()))
                .title(convertString(product.getWebTitle().isEmpty() ? product.getDescription() : product.getWebTitle()))
                .option1Name(product.getSize().isEmpty() ? "Title" : "Size")
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
                .available(product.getQuantityInStock())
                .onHand(product.getQuantityInStock())
                .build();


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
                .option1Name(product.getSize().isEmpty() ? "Title" : "Size")
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
                .available(product.getQuantityInStock())
                .onHand(product.getQuantityInStock())
                .build();

    }

    private ProductSchema createSimple(WhitesmotoProduct product) {
        return ProductSchema.builder()
                .handle(createHandle(product.getRange().isEmpty() ? product.getDescription() : product.getRange()))
                .title(convertString(product.getWebTitle().isEmpty() ? product.getDescription() : product.getWebTitle()))
                .bodyHtml(getBodyHtml(product))
                .vendor("whitesmoto")
                .published(!Boolean.parseBoolean(product.getInactive()))
                .option1Name(product.getSize().isEmpty() ? "Title" : "Size")
                .option1Value(product.getSize().isEmpty() ? "Default Title" : product.getSize())
                .option2Name("")
                .option2Value("")
                .option3Name("")
                .option3Value("")
                .variantSku(product.getProductCode())
                .variantGrams("")
                .variantInventoryTracker("shopify")
                .variantInventoryQty(product.getQuantityInStock())
                .variantInventoryPolicy("deny")
                .variantFulfillmentService("manual")
                .variantPrice(isHasPrice(product) ? product.getYourPrice() : "")
                .variantCompareAtPrice(null)
                .variantRequiresShipping(true)
                .variantTaxable(true)
                .variantBarcode(product.getBarcode())
                .imageSrc(getFirstImageByOrder(product.getImages()))
                .imagePosition("")
                .imageAltText("")
                .giftCard("")
                .seoTitle(convertString(product.getWebTitle().isEmpty() ? product.getDescription() : product.getWebTitle()))
                .seoDescription(convertString(removeQuotesFromText(extractTextFromHTML(getBodyHtml(product)))))
                .variantImage(getFirstImageByOrder(product.getImages()))
                .variantWeightUnit("kg")
                .variantTaxCode("")
                .costPerItem("0")
                .includedAustralia(true)
                .priceAustralia(null)
                .compareAtPriceAustralia(null)
                .includedInternational(true)
                .priceInternational(null)
                .compareAtPriceInternational(null)
                .status("draft")
                .build();
    }


    private ProductSchema createVariable(WhitesmotoProduct product) {
        return ProductSchema.builder()
                .handle(createHandle(product.getRange()))
                .title(convertString(product.getWebTitle()))
                .bodyHtml(getBodyHtml(product))
                .vendor("Whitesmoto")
                .published(!Boolean.parseBoolean(product.getInactive()))
                .option1Name(product.getSize().isEmpty() ? "Title" : "Size")
                .seoTitle(convertString(product.getWebTitle()))
                .seoDescription(convertString(removeQuotesFromText(extractTextFromHTML(getBodyHtml(product)))))
                .includedAustralia(true)
                .includedInternational(true)
                .status("draft")
                .imageSrc(getFirstImageByOrder(product.getImages()))
                .build();

    }

    private ProductSchema createFirstVariation(WhitesmotoProduct product, ProductSchema parentProduct) {

        return ProductSchema.builder()
                .handle(createHandle(parentProduct.getHandle()))
                .title(parentProduct.getTitle())
                .bodyHtml(parentProduct.getBodyHtml())
                .vendor(parentProduct.getVendor())
                .type(parentProduct.getType())
                .published(parentProduct.getPublished())
                .option1Name(parentProduct.getOption1Name())
                .option1Value(product.getSize().isEmpty() ? "Default Title" : product.getSize())
                .option2Name(parentProduct.getOption2Name())
                .option3Name(parentProduct.getOption3Name())
                .variantSku(product.getProductCode())
                .variantInventoryTracker("shopify")
                .variantInventoryQty(product.getQuantityInStock())
                .variantInventoryPolicy("deny")
                .variantFulfillmentService("manual")
                .variantPrice(isHasPrice(product) ? product.getYourPrice() : "")
                .variantRequiresShipping(true)
                .variantTaxable(true)
                .variantBarcode(product.getBarcode())
                .imageSrc(getFirstImageByOrder(product.getImages()))
                .seoTitle(convertString(product.getWebTitle()))
                .seoDescription(convertString(removeQuotesFromText(extractTextFromHTML(getBodyHtml(product)))))
                .variantImage(getFirstImageByOrder(product.getImages()))
                .variantWeightUnit("kg")
                .costPerItem("0")
                .includedAustralia(true)
                .includedInternational(true)
                .status("draft").build();
    }

    private ProductSchema createVariation(WhitesmotoProduct product, ProductSchema parentProduct) {

        return ProductSchema.builder()
                .handle(createHandle(parentProduct.getHandle()))
                .option1Value(product.getSize().isEmpty() ? "Default Title" : product.getSize())
                .option2Value("")
                .option3Value("")
                .variantSku(product.getProductCode())
                .variantGrams("")
                .variantInventoryTracker("shopify")
                .variantInventoryQty(product.getQuantityInStock())
                .variantInventoryPolicy("deny")
                .variantFulfillmentService("manual")
                .variantPrice(isHasPrice(product) ? product.getYourPrice() : "")
                .variantCompareAtPrice(null)
                .variantTaxCode("")
                .variantRequiresShipping(true)
                .variantTaxable(true)
                .variantBarcode("")
                .imageSrc(parentProduct.getImageSrc())
                .imagePosition("")
                .giftCard("")
                .variantImage(getFirstImageByOrder(product.getImages()))
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

    private String removeQuotesFromText(String string) {
        return string.replaceAll("\"", "");
    }

    private List<ProductSchema> getImageProductSchemas(List<String> unusedImages, ProductSchema parentProductSchema) {

        List<ProductSchema> productSchemas = new ArrayList<>();
        for (String unusedImage : unusedImages) {
            productSchemas.add(createImageProductSchema(parentProductSchema, unusedImage));
        }
        return productSchemas;
    }

    private String createHandle(String title) {

        String lowerCase = title.toLowerCase();
        String handle = lowerCase.replaceAll("[^a-z0-9-]", "-");
        handle = handle.replaceAll("-{2,}", "-");
        handle = handle.replaceAll("^-|-$", "");

        return handle;
    }

    private static boolean isHasPrice(WhitesmotoProduct product) {
        boolean hasPrice = !product.getYourPrice().isEmpty();
        if (hasPrice)
            hasPrice = Double.parseDouble(product.getYourPrice()) > 0;
        else
            System.out.println(product.getYourPrice());
        return hasPrice;
    }


    private String getBodyHtml(WhitesmotoProduct product) {

        String resultStr;
        if (product.getRichDescription() == null)
            resultStr = product.getDescription();
        else if (product.getRichDescription().isEmpty())
            resultStr = product.getDescription();
        else resultStr = product.getRichDescription();

        return convertString(replaceHeadersWithH6(resultStr));
    }

    private String getFirstImageByOrder(List<WhitesmotoProduct.Image> images) {

        if (images == null || images.isEmpty())
            return null;

        return  images.stream()
                .filter(image -> {
                    String active = image.getImageActive();
                    return active != null && active.equals("true");
                })
                .min(Comparator.comparingInt(image -> Integer.parseInt(image.getImageOrder()))) // возврат первого подходящего изображения
                .map(WhitesmotoProduct.Image::getImageUrl) // получение URL изображения
                .orElse(null); // если не найдено подходящего изображения, возвращаем null
    }

    private Map<String, List<WhitesmotoProduct>> groupProductsByRange(List<VendorProduct> productList) {
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

    private List<WhitesmotoProduct.Image> getUnusedImages(WhitesmotoProduct product) {

        if (product.getImages().isEmpty()){
            return new ArrayList<>();
        }

        var images = product.getImages();
        var usedImg = images.stream()
                .filter(image -> "true".equals(image.getImageActive()))
                .min(Comparator.comparingInt(image -> Integer.parseInt(image.getImageOrder())))
                .orElseThrow(RuntimeException::new); // возврат первого подходящего изображения

        images.remove(usedImg);

        return new ArrayList<>(images);
    }
}
