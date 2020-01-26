package lv.events.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.microsoft.azure.spring.data.cosmosdb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntryItem {
    private String id;
    private String code;

    private LocalDate day;
    private String dayModel;

    private LocalTime timeFrom;
    private String timeFromModel;

    private LocalTime timeTo;
    private String timeToModel;

    private String eventCode;

    private String email;
    private boolean available;
    private String status;

    private String clientFacebookId;
    private String clientName;
}

