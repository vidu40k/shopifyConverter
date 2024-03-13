package shopify.converter.controller.whitesmoto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shopify.converter.controller.ProductController;
import shopify.converter.service.whitesmoto.WhitesmotoService;
import shopify.converter.util.FileUtil;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("converter/whitesmoto")
@RequiredArgsConstructor
public class WhitesmotoController extends ProductController {


    public static final String RESULT_DATA_WHITESMOTO_FOLDER = "/products/whitesmoto/";
    public static final String PRODUCTS_FILE_TYPE = "products";
    public static final String INVENTORY_FILE_TYPE = "inventory";

    private final WhitesmotoService whitesmotoService;
    private final FileUtil fileUtil;

    @GetMapping("/downloadProduct")
    public ResponseEntity<?> downloadProductFile() {

        List<String> files = fileUtil.getFilePathsFromFolder(RESULT_DATA_WHITESMOTO_FOLDER,PRODUCTS_FILE_TYPE);
        if (files.isEmpty())
           files = whitesmotoService.parseToProductsCsv().get(PRODUCTS_FILE_TYPE);

        return whitesmotoService.getResourceResponseEntityZip(PRODUCTS_FILE_TYPE, files);
    }

    @GetMapping("/downloadInventory")
    public ResponseEntity<?> downloadInventoryFile() {

        List<String> files = fileUtil.getFilePathsFromFolder(RESULT_DATA_WHITESMOTO_FOLDER,INVENTORY_FILE_TYPE);
        if (files.isEmpty())
            files = whitesmotoService.parseToProductsCsv().get(INVENTORY_FILE_TYPE);

        return whitesmotoService.getResourceResponseEntityZip(INVENTORY_FILE_TYPE, files);
    }


}
