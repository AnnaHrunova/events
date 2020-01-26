package lv.events.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@AllArgsConstructor
public class SeriesItem {

    private String id;
    private LocalDateTime time;
    private BigDecimal value;
}
