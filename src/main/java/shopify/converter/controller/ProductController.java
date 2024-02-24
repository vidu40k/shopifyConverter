package shopify.converter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shopify.converter.service.revit.RevitService;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/converter")
@RequiredArgsConstructor
public class ProductController {

    private static final String REVIT_REQUEST = "https://www.revitaustralia.com.au/products.json?limit=250&page=1";
    public static final String PRODUCT_CSV_PATH = "products.csv";
    public static final String INVENTORY_CSV_PATH = "inventory.csv";
    private static final String PASSWORD = "rouzer";

    private final RevitService revitService;


    @GetMapping("/index")
    public String getPage() {

        return "main";
    }


    @GetMapping("/downloadProduct")
    public ResponseEntity<Resource> downloadProductFile() {

        revitService.getFileContent(REVIT_REQUEST);

        Path path = Paths.get(PRODUCT_CSV_PATH);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (resource == null || !resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Can't find the file or read it: product.csv");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    }

    @GetMapping("/downloadInventory")
    public ResponseEntity<Resource> downloadInventoryFile() {

        revitService.getFileContent(REVIT_REQUEST);

        Path path = Paths.get(INVENTORY_CSV_PATH);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (resource == null || !resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Can't find the file or read it: inventory.csv");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    }


    @PostMapping("/verifyPassword")
    @ResponseBody
    public Map<String, Boolean> verifyPassword(@RequestBody String password) {
        Map<String, Boolean> response = new HashMap<>();

        boolean isValid = checkPassword(password);

        response.put("valid", isValid);
        return response;
    }

    private boolean checkPassword(String password) {
        return PASSWORD.equals(password);
    }

}
