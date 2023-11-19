package tech.konso.toolsmanagement.system.commons.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BPException extends RuntimeException {

    private final String message;
    private final HttpStatus status;

    public BPException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }

    public static final class BadRequest extends BPException {
        public BadRequest(String message) {
            super(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static final class NotFound extends BPException {
        public NotFound(String message) {
            super(message, HttpStatus.NOT_FOUND);
        }
    }

    public static final class ServiceUnavailable extends BPException {
        public ServiceUnavailable(String message) {
            super(message, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
