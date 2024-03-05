package shopify.converter.controller.whitesmoto;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shopify.converter.controller.ProductController;
import shopify.converter.service.whitesmoto.WhitesmotoService;

import java.io.File;
import java.util.Collections;

@Controller
@RequestMapping("converter/whitesmoto")
@RequiredArgsConstructor
public class WhitesmotoController extends ProductController {

//    public static final String PRODUCT_CSV_PATH = "src/main/resources/products/whitesmoto/products.csv";
//    public static final String INVENTORY_CSV_PATH = "src/main/resources/products/whitesmoto/inventoryExampl.csv";
//    private final WhitesmotoService whitesmotoService;

//    @GetMapping("/downloadProduct")
//    public ResponseEntity<?> downloadProductFile() {
//
//        File file = new File(PRODUCT_CSV_PATH);
//        if (!file.exists())
//            whitesmotoService.parseToProductsCsv();
//        return whitesmotoService.getResourceResponseEntity(Collections.singletonList(PRODUCT_CSV_PATH));
//    }
//
//    @GetMapping("/downloadInventory")
//    public ResponseEntity<?> downloadInventoryFile() {
//
//        File file = new File(INVENTORY_CSV_PATH);
//        if (!file.exists())
//            whitesmotoService.parseToProductsCsv();
//        return whitesmotoService.getResourceResponseEntity(Collections.singletonList(INVENTORY_CSV_PATH));
//    }


}
