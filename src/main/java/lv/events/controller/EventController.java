package lv.events.controller;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lv.events.model.EntryStatus;
import lv.events.security.AccessTokenValidator;
import lv.events.security.AccessValidationResult;
import lv.events.security.InvalidAccessTokenException;
import lv.events.dao.EntryRepository;
import lv.events.dao.EventRepository;
import lv.events.model.EntryItem;
import lv.events.model.EventItem;

@RestController
@Slf4j
@AllArgsConstructor
public class EventController {

    private final EventRepository eventItemRepository;
    private final EntryRepository entryItemRepository;
    private final AccessTokenValidator accessTokenValidator;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm");

    @GetMapping(value = "/api/events", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAllEvents(@CookieValue(name = "user_name", defaultValue = "anonymous") final String userName,
                                          @CookieValue(name = "access_token", defaultValue = "anonymous") final String accessToken) {
        try {
            Iterable<EventItem> events;
            if (!"anonymoys".equals(userName)) {
                events = eventItemRepository.findAll();
            } else {
                AccessValidationResult result = accessTokenValidator.validateAccessToken(accessToken);
                events = eventItemRepository.findAllByOwnerFacebookIdNotIn(result.getUserId());
            }
            return ResponseEntity.ok(events);
        } catch (InvalidAccessTokenException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No Access To This Resource. Please login!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incorrect request!");
        }
    }


    @GetMapping(value = "/api/events-my", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUserEvents(@CookieValue(name = "user_name", defaultValue = "anonymous") final String userName,
                                           @CookieValue(name = "access_token", defaultValue = "anonymous") final String accessToken) {
        try {
            AccessValidationResult result = accessTokenValidator.validateAccessToken(accessToken);
            val events = eventItemRepository.findAllByOwnerFacebookId(result.getUserId());
            return ResponseEntity.ok(events);
        } catch (InvalidAccessTokenException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No Access To This Resource. Please login!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incorrect request!");
        }
    }

    @PostMapping(value = "/api/events", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addNewEvent(@RequestBody EventItem item,
                                              @CookieValue(name = "user_name", defaultValue = "anonymous") final String userName,
                                              @CookieValue(name = "access_token", defaultValue = "anonymous") final String accessToken) {

        try {
            AccessValidationResult result = accessTokenValidator.validateAccessToken(accessToken);

            String code = "QU-" + UUID.randomUUID().toString().substring(0, 5);
            item.setId(UUID.randomUUID().toString());
            item.setCode(code);
            item.setOwnerFacebookId(result.getUserId());
            item.setOwnerName(userName);

            eventItemRepository.save(item);
            generateEntriesForEvent(code);
            return ResponseEntity.ok("Event created successfully");

        } catch (InvalidAccessTokenException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No Access To This Resource. Please login!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incorrect request!");
        }
    }

    @PutMapping(value = "/api/events", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateEventDescription(@RequestBody EventItem item) {
        try {
            val eventItem = eventItemRepository.findById(item.getCode()).get();
            eventItem.setDescription(item.getDescription());
            eventItemRepository.deleteById(eventItem.getId());
            eventItemRepository.save(eventItem);

            return ResponseEntity.ok("Event updated");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incorrect request!");
        }
    }

    private void generateEntriesForEvent(final String eventCode) {
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 10; k += 2) {
                createEntry(eventCode, i, k);
            }
        }
    }

    private void createEntry(final String eventCode,
                             final int plusDays,
                             final int plusHours) {
        String entryCode = "EN-" + UUID.randomUUID().toString().substring(0, 5);
        EntryItem entry = new EntryItem();
        entry.setId(UUID.randomUUID().toString());
        entry.setAvailable(true);
        entry.setCode(entryCode);
        entry.setEventCode(eventCode);
        entry.setDay(LocalDate.now().plusDays(plusDays));
        entry.setTimeFrom(LocalTime.now().plusHours(plusHours));
        entry.setTimeTo(LocalTime.now().plusHours(plusHours + 1));
        entry.setDayModel(DATE_FORMATTER.format(entry.getDay()));
        entry.setTimeFromModel(TIME_FORMATTER.format(entry.getTimeFrom()));
        entry.setTimeToModel(TIME_FORMATTER.format(entry.getTimeTo()));
        entry.setStatus(EntryStatus.NEW.name());
        entryItemRepository.save(entry);
    }
}
