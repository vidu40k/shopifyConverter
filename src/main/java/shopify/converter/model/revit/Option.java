package shopify.converter.model.revit;

import lombok.Data;

import java.util.List;

@Data
public class Option {

    private String name;
    private String position;
    private List<String> values;
}
