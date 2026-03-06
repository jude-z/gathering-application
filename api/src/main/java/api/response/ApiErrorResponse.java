package api.response;

import exception.CommonException;
import exception.Status;
import lombok.Builder;
import lombok.Getter;

import static exception.Status.*;

@Getter
public class ApiErrorResponse {
    private String code;
    private String message;

    @Builder
    private ApiErrorResponse(String code, String message){
        this.code = code;
        this.message = message;
    }

    public static ApiErrorResponse fromCommonException(CommonException exception){
        return ApiErrorResponse.builder()
                .code(exception.getCode())
                .message(exception.getContent())
                .build();
    }

    public static ApiErrorResponse fromIOException (){
        return ApiErrorResponse.builder()
                .code(UPLOAD_FAIL.getCode())
                .message(UPLOAD_FAIL.getContent())
                .build();
    }
}
