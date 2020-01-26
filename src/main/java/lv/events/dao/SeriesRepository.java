/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package lv.events.dao;

import com.microsoft.azure.spring.data.cosmosdb.repository.DocumentDbRepository;
import lv.events.anamalies.Series;
import lv.events.model.EntryItem;
import lv.events.model.EntryStatus;
import lv.events.model.SeriesItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends DocumentDbRepository<SeriesItem, String> {

}
