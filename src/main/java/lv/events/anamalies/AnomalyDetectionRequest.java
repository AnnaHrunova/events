package lv.events.anamalies;

import lombok.Data;

import java.util.List;

@Data
class AnomalyDetectionRequest {

    private String granularity = "daily";
    private List<Series> series;
}
