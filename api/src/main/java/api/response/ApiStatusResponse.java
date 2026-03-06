package api.response;

import exception.Status;

public class ApiStatusResponse extends ApiResponse {

    private ApiStatusResponse(Status status) {
        super(status);
    }

    public static ApiStatusResponse of(Status status) {
        return new ApiStatusResponse(status);
    }
}
