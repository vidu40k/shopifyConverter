package shopify.converter.controller.motonational;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shopify.converter.controller.ProductController;
import shopify.converter.service.motonational.MotonationalService;

import java.io.File;


@Controller
@RequestMapping("converter/motonational")
@RequiredArgsConstructor
public class MotonationalController extends ProductController {

    private static final String MOTONATIONAL_PRODUCTS_CSV = "src/main/resources/products/motonational/products.csv";
    private static final String MOTONATIONAL_INVENTORY_CSV = "src/main/resources/products/motonational/inventory.csv";
    private final MotonationalService motonationalService;

    @GetMapping("/downloadProduct")
    public ResponseEntity<Resource> downloadProductFile() {

        File file = new File(MOTONATIONAL_PRODUCTS_CSV);
        if (!file.exists())
            motonationalService.parseToProductsCsv();
        return motonationalService.getResourceResponseEntity(MOTONATIONAL_PRODUCTS_CSV);

    }

    @GetMapping("/downloadInventory")
    public ResponseEntity<Resource> downloadInventoryFile() {

        File file = new File(MOTONATIONAL_INVENTORY_CSV);
        if (!file.exists())
            motonationalService.parseToProductsCsv();
        return motonationalService.getResourceResponseEntity(MOTONATIONAL_INVENTORY_CSV);
    }
}
