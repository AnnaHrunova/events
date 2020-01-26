package lv.events.security;

public class InvalidAccessTokenException extends Exception {
    public InvalidAccessTokenException() {
        super("Invalid access token");
    }
}
