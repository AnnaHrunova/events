package lv.events.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessValidationResult {
    private String userId;
    private boolean isValid;
}
