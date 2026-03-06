package api.response;

import exception.Status;
import lombok.Getter;

@Getter
public class ApiDataResponse<T> extends ApiResponse {
    private final T data;

    private ApiDataResponse(T data, Status status) {
        super(status);
        this.data = data;
    }

    public static <T> ApiDataResponse<T> of(T data, Status status) {
        return new ApiDataResponse<>(data, status);
    }
}
