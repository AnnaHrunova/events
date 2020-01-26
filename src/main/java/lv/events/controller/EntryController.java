package lv.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lv.events.anamalies.AnomalyDetectionService;
import lv.events.communication.CommunicationSender;
import lv.events.dao.EntryRepository;
import lv.events.model.EntryItem;
import lv.events.model.EntryStatus;
import lv.events.model.EventItem;
import lv.events.security.AccessTokenValidator;
import lv.events.security.AccessValidationResult;
import lv.events.security.InvalidAccessTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static lv.events.model.EntryStatus.ACCEPTED;
import static lv.events.model.EntryStatus.CANCELED;

@RestController
@Slf4j
@RequiredArgsConstructor
public class EntryController {

    private final EntryRepository entryRepository;
    private final AccessTokenValidator accessTokenValidator;
    private final CommunicationSender emailSendingService;
    private final AnomalyDetectionService anomalyDetectionService;

    @GetMapping(value = "/api/entries-my", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getClientEntries(@CookieValue(name = "user_name", defaultValue = "anonymous") final String userName,
                                              @CookieValue(name = "access_token", defaultValue = "anonymous") final String accessToken) {
        try {
            AccessValidationResult result = accessTokenValidator.validateAccessToken(accessToken);
            val entryItem = entryRepository.findAllByClientFacebookId(result.getUserId());
            return ResponseEntity.ok(entryItem);
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

    @GetMapping(value = "/api/entries-event/{eventCode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getEventEntries(@PathVariable String eventCode,
                                             @CookieValue(name = "user_name", defaultValue = "anonymous") final String userName,
                                             @CookieValue(name = "access_token", defaultValue = "anonymous") final String accessToken) {
        try {
            val allByEventCode = entryRepository.findAllByEventCode(eventCode);
            return ResponseEntity.ok(allByEventCode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incorrect request!");
        }
    }

    @PutMapping(value = "/api/entries-apply")
    public ResponseEntity<String> applyForEntry(@RequestBody EntryItem item,
                                                @CookieValue(name = "user_name", defaultValue = "anonymous") final String userName,
                                                @CookieValue(name = "access_token", defaultValue = "anonymous") final String accessToken) {
        try {
            AccessValidationResult result = accessTokenValidator.validateAccessToken(accessToken);
            BigDecimal clientRate = anomalyDetectionService.getRateForClient(result.getUserId());
            boolean isPositiveAnomaly = anomalyDetectionService.isPositiveAnomaly(clientRate);

            if (!isPositiveAnomaly) {
                val entry = entryRepository.findByCode(item.getCode()).get(0);
                entry.setAvailable(false);
                entry.setClientFacebookId(result.getUserId());
                entry.setClientName(userName);
                entry.setStatus(EntryStatus.RESERVED.name());
                entry.setEmail(item.getEmail());
                entryRepository.deleteById(entry.getId());
                entryRepository.save(entry);

                emailSendingService.sendEmail(item.getEmail(), userName);
                return ResponseEntity.ok("Applied Successfully. Confirmation email sent!");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Reservation denied, because you are a bad client!");
            }
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

    @PutMapping(value = "/api/entries-accept/{entryCode}")
    public ResponseEntity<?> acceptEntry(@PathVariable String entryCode,
                                         @CookieValue(name = "user_name", defaultValue = "anonymous") final String userName,
                                         @CookieValue(name = "access_token", defaultValue = "anonymous") final String accessToken) {
        try {
            val entryItem = entryRepository.findByCode(entryCode).get(0);
            entryItem.setStatus(ACCEPTED.name());
            entryRepository.deleteById(entryItem.getId());
            entryRepository.save(entryItem);
            return ResponseEntity.ok("Entry accepted!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incorrect request!");
        }
    }

    @PutMapping(value = "/api/entries-cancel/{entryCode}")
    public ResponseEntity<?> cancelEntry(@PathVariable String entryCode,
                                         @CookieValue(name = "user_name", defaultValue = "anonymous") final String userName,
                                         @CookieValue(name = "access_token", defaultValue = "anonymous") final String accessToken) {
        try {
            val entryItem = entryRepository.findByCode(entryCode).get(0);
            entryItem.setStatus(CANCELED.name());
            entryRepository.deleteById(entryItem.getId());
            entryRepository.save(entryItem);
            return ResponseEntity.ok("Entry cancelled!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incorrect request!");
        }
    }
}
