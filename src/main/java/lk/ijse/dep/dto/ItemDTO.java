package lk.ijse.dep.dto;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private String code;
    private String description;
    private int qtyOnHand;
    private BigDecimal unitPrice;
}
