/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package lv.events.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.microsoft.azure.spring.data.cosmosdb.repository.DocumentDbRepository;

import lv.events.model.EventItem;

@Repository
public interface EventRepository extends DocumentDbRepository<EventItem, String> {

    List<EventItem> findAllByOwnerFacebookId(String userId);

    List<EventItem> findAllByOwnerFacebookIdNotIn(String userId);
}
