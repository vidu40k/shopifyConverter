package shopify.converter.response.whitesmoto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
public class ProductQty {

    @JsonProperty("productcode")
    private String productCode;
    @JsonProperty("z_masterlocation")
    private String zMasterLocation;
    @JsonProperty("sum")
    private String sum;
}
