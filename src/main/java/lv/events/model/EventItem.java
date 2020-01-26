package lv.events.model;

import com.microsoft.azure.spring.data.cosmosdb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventItem {
    private String id;
    private String code;
    private String name;
    private String description;
    private String ownerFacebookId;
    private String ownerName;
}

