package shopify.converter.controller.revit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shopify.converter.controller.ProductController;
import shopify.converter.service.revit.RevitService;

import java.io.File;

@Controller
@RequestMapping("converter/revit")
@RequiredArgsConstructor
public class RevitController extends ProductController {


    public static final String PRODUCT_CSV_PATH = "/products/revit/products.csv";
    public static final String INVENTORY_CSV_PATH = "/products/revit/inventory.csv";

    private final RevitService revitService;

    @GetMapping("/downloadProduct")
    public ResponseEntity<?> downloadProductFile() {

        File file = new File(PRODUCT_CSV_PATH);
        if (!file.exists())
            revitService.parseToProductsCsv();
        return revitService.getResourceResponseEntity(PRODUCT_CSV_PATH);
    }

    @GetMapping("/downloadInventory")
    public ResponseEntity<?> downloadInventoryFile() {

        File file = new File(INVENTORY_CSV_PATH);
        if (!file.exists())
            revitService.parseToProductsCsv();
        return revitService.getResourceResponseEntity(INVENTORY_CSV_PATH);
    }

}
