package lv.events.anamalies;

import lombok.AllArgsConstructor;
import lv.events.dao.EntryRepository;
import lv.events.dao.SeriesRepository;
import lv.events.model.EntryStatus;
import lv.events.model.SeriesItem;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class DataCollectionScheduler {

    private final EntryRepository entryRepository;

    private final SeriesRepository seriesRepository;

    private final AnomalyDetectionService anomalyDetectionService;

    @Scheduled(cron = "59 10 0 * * *")
    public void collectAnomalies() {
        long cancelledItems = entryRepository.countByStatus(EntryStatus.CANCELED.name()).size();
        long allItems = entryRepository.count();

        BigDecimal rate = anomalyDetectionService.score(cancelledItems, allItems);
        SeriesItem latestSeries = new SeriesItem(UUID.randomUUID().toString(), LocalDateTime.now(), rate);
        seriesRepository.save(latestSeries);
    }
}
