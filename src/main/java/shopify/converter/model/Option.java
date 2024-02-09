package shopify.converter.model;

import lombok.Data;

import java.util.List;

@Data
public class Option {

    private String name;
    private Integer position;
    private List<String> values;
}
