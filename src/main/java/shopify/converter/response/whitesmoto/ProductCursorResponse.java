package shopify.converter.response.whitesmoto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductCursorResponse {

    List<ProductQty> productQtyList;

}
