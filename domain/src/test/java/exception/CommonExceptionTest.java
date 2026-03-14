package exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommonExceptionTest {

    @Test
    @DisplayName("Status로 CommonException 생성 시 code, content, httpStatus 정상 설정")
    void 생성자_Status로_초기화() {
        CommonException exception = new CommonException(Status.NOT_FOUND_USER);

        assertThat(exception.getCode()).isEqualTo("NFU");
        assertThat(exception.getContent()).isEqualTo("Not Found User");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("CommonException은 RuntimeException을 상속")
    void RuntimeException_상속_확인() {
        CommonException exception = new CommonException(Status.DB_ERROR);

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("CommonException을 throw하고 catch할 수 있음")
    void throw_catch_동작_확인() {
        assertThatThrownBy(() -> {
            throw new CommonException(Status.NOT_AUTHORIZE);
        }).isInstanceOf(CommonException.class);
    }
}
