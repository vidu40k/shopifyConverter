package shopify.converter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shopify.converter.model.Motonational.MotonationalProduct;
import shopify.converter.service.ProductService;
import shopify.converter.service.motonational.MotonationalService;
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

    private static final String PASSWORD = "rouzer";

    @GetMapping("/index")
    public String getPage() {
        return "main";
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
