package shopify.converter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shopify.converter.service.motonational.MotonationalService;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/motonational")
@RequiredArgsConstructor
public class MotonationalController {

    private static final String MOTONATIONAL_PRODUCTS = "Motonational.csv";

    private final MotonationalService motonationalService;

    @GetMapping("/downloadProduct")
    public ResponseEntity<Resource> downloadProductFile() {

        motonationalService.parseToProductsCsv();

        Path path = Paths.get(MOTONATIONAL_PRODUCTS);
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    }

}
