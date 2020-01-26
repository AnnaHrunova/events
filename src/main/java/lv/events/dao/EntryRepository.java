/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package lv.events.dao;

import java.util.List;
import java.util.Optional;

import lv.events.model.EntryStatus;
import org.springframework.stereotype.Repository;

import com.microsoft.azure.spring.data.cosmosdb.repository.DocumentDbRepository;

import lv.events.model.EntryItem;

@Repository
public interface EntryRepository extends DocumentDbRepository<EntryItem, String> {

    List<EntryItem> findAllByEventCode(String eventCode);

    List<EntryItem> findAllByClientFacebookId(String userId);

    List<EntryItem> countByClientFacebookId(String userId);

    List<EntryItem>  countByClientFacebookIdAndStatus(String userId, String status);

    List<EntryItem>  countByStatusIn(String status);

    List<EntryItem> findByCode(String code);
}