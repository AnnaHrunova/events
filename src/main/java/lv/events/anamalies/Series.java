package lv.events.anamalies;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Series {

    private String timestamp;
    private BigDecimal value;

}
