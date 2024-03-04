package shopify.converter.controller.motonational;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shopify.converter.controller.ProductController;
import shopify.converter.service.motonational.MotonationalService;
import shopify.converter.util.FileUtil;

import java.util.List;


@Controller
@RequestMapping("converter/motonational")
@RequiredArgsConstructor
public class MotonationalController extends ProductController {

    public static final String INITIAL_DATA_MOTONATIONAL_FILE = "/initialData/motonational/";
    public static final String RESULT_DATA_MOTONATIONAL_FILE = "/products/motonational/";
    public static final String PRODUCTS_FILE_TYPE = "products";
    public static final String INVENTORY_FILE_TYPE = "inventory";
    private final MotonationalService motonationalService;
    private final FileUtil fileUtil;


    @GetMapping("/downloadProduct")
    public ResponseEntity<?> downloadProductFile() {

        List<String> files;
        if (fileUtil.checkFileConsistency(RESULT_DATA_MOTONATIONAL_FILE, INITIAL_DATA_MOTONATIONAL_FILE))
            files = fileUtil.getFilePathsFromFolder(RESULT_DATA_MOTONATIONAL_FILE, PRODUCTS_FILE_TYPE);
        else
            files = motonationalService.parseToProductsCsv().get(PRODUCTS_FILE_TYPE);

        return motonationalService.getResourceResponseEntityZip(PRODUCTS_FILE_TYPE, files);
    }

    @GetMapping("/downloadInventory")
    public ResponseEntity<?> downloadInventoryFile() {

        List<String> files;
        if (fileUtil.checkFileConsistency(RESULT_DATA_MOTONATIONAL_FILE, INITIAL_DATA_MOTONATIONAL_FILE)) {
            files = fileUtil.getFilePathsFromFolder(RESULT_DATA_MOTONATIONAL_FILE, INVENTORY_FILE_TYPE);
        } else
            files = motonationalService.parseToProductsCsv().get(INVENTORY_FILE_TYPE);
        return motonationalService.getResourceResponseEntityZip(INVENTORY_FILE_TYPE, files);
    }
}
