package exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonException extends RuntimeException{
    private final String code;
    private final String content;
    private final HttpStatus httpStatus;

    public CommonException(Status status){
        this.code = status.getCode();
        this.content = status.getContent();
        this.httpStatus = status.getHttpStatus();
    }
}
