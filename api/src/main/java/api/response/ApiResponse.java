package api.response;

import exception.Status;
import lombok.Getter;

@Getter
public abstract class ApiResponse {
    private final String code;
    private final String message;

    protected ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    protected ApiResponse(Status status) {
        this.code = status.getCode();
        this.message = status.getContent();
    }
}
