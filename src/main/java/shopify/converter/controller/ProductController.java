package shopify.converter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shopify.converter.service.ProductService;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/converter")
@RequiredArgsConstructor
public class ProductController {

    private static final String PRODUCT_CSV_PATH = "src/main/resources/static/products.csv";
    private static final String INVENTORY_CSV_PATH = "src/main/resources/static/inventory.csv";
    private final ProductService productService;

    @GetMapping("/convert")
    public String convert(@RequestParam(name = "requestUrl") String request, Model model) {

        productService.getFileContent(request);

        model.addAttribute("createDate", getFileCreationDateString(PRODUCT_CSV_PATH));
        model.addAttribute("request", request);

        return "result";
    }

    @GetMapping("/main")
    public String getPage() {

        new File(PRODUCT_CSV_PATH).delete();
        new File(INVENTORY_CSV_PATH).delete();

        return "main";
    }

    @GetMapping("/downloadProduct")
    public ResponseEntity<Resource> downloadProductFile() {
        Path path = Paths.get(PRODUCT_CSV_PATH);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (resource == null || !resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Не удается найти файл или его прочитать: product.csv");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    }

    @GetMapping("/downloadInventory")
    public ResponseEntity<Resource> downloadInventoryFile() {
        Path path = Paths.get(INVENTORY_CSV_PATH);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (resource == null || !resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Не удается найти файл или его прочитать: inventory.csv");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    }


    private String getFileCreationDateString(String filePath) {
        Path path = Paths.get(filePath);
        try {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime creationTime = attr.lastModifiedTime();
            Date date = new Date(creationTime.toMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
