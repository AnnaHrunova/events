package lv.events.anamalies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lv.events.dao.EntryRepository;
import lv.events.dao.SeriesRepository;
import lv.events.model.EntryStatus;
import lv.events.model.SeriesItem;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AnomalyDetectionService {

    @Value("${anomaly.detector.api.key}")
    private String apiKey;

    @Value("${anomaly.detector.api.url}")
    private String url;

    @Value("${demo.data.on}")
    private boolean demoDataOn;

    private final EntryRepository entryRepository;

    private final SeriesRepository seriesRepository;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public boolean isPositiveAnomaly(BigDecimal clientRate) throws JsonProcessingException {
        AnomalyDetectionRequest anomalyDetectionRequest = new AnomalyDetectionRequest();
        anomalyDetectionRequest.setSeries(createTimeSeries(clientRate));
        String requestData = new ObjectMapper().writeValueAsString(anomalyDetectionRequest);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", apiKey);
            request.setEntity(new StringEntity(requestData));
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity respEntity = response.getEntity();
                if (respEntity != null) {
                    JSONObject jsonObject = new JSONObject(EntityUtils.toString(respEntity, "utf-8"));
                    return jsonObject.getBoolean("isPositiveAnomaly");
                }
            } catch (Exception respEx) {
                respEx.printStackTrace();
            }
        } catch (IOException ex) {
            System.err.println("Exception on Anomaly Detector: " + ex.getMessage());
            ex.printStackTrace();
        }
        return true;
    }

    private List<Series> createTimeSeries(BigDecimal clientRate) {
        List<SeriesItem> series = demoDataOn ? generateTestData() : (List<SeriesItem>) seriesRepository.findAll();
        List<Series> result = series
                .stream()
                .sorted(Comparator.comparing(SeriesItem::getTime))
                .map(s -> new Series(formatTimestamp(s.getTime()), s.getValue()))
                .collect(Collectors.toList());
        result.add(new Series(formatTimestamp(LocalDateTime.now()), clientRate));
        return result;
    }

    private String formatTimestamp(LocalDateTime from) {
        return dateTimeFormatter.format(from) + "Z";
    }

    public BigDecimal getRateForClient(String clientId) {
        long allClientEntries = entryRepository.countByClientFacebookId(clientId).size();
        long cancelledEntries = entryRepository.countByClientFacebookIdAndStatus(clientId, EntryStatus.CANCELED.name()).size();

        if (allClientEntries == 0 || cancelledEntries == 0) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(cancelledEntries)
                .divide(new BigDecimal(allClientEntries), RoundingMode.HALF_UP)
                .setScale(5, RoundingMode.HALF_UP);
    }

    private List<SeriesItem> generateTestData() {
        List<SeriesItem> result = new ArrayList<>();
        for (int i = 1; i < 15; i++) {
            BigDecimal value = new BigDecimal(new Random().nextDouble()).setScale(5, RoundingMode.HALF_UP);
            result.add(new SeriesItem(UUID.randomUUID().toString(), LocalDateTime.now().minusDays(i), value));
        }
        return result;
    }
}