package exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class StatusTest {

    @Test
    @DisplayName("SUCCESS 상태값 확인")
    void SUCCESS_상태값() {
        assertThat(Status.SUCCESS.getCode()).isEqualTo("SU");
        assertThat(Status.SUCCESS.getContent()).isEqualTo("Success");
        assertThat(Status.SUCCESS.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("NOT_FOUND 계열 상태는 HttpStatus.NOT_FOUND")
    void NOT_FOUND_계열_HttpStatus() {
        assertThat(Status.NOT_FOUND_USER.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(Status.NOT_FOUND_GATHERING.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(Status.NOT_FOUND_MEETING.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(Status.NOT_FOUND_ENROLLMENT.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(Status.NOT_FOUND_ALARM.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("CONFLICT 계열 상태는 HttpStatus.CONFLICT")
    void CONFLICT_계열_HttpStatus() {
        assertThat(Status.EXIST_USER.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(Status.DUPLICATE_EMAIL.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(Status.ALREADY_ATTEND.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(Status.ALREADY_ENROLLMENT.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(Status.ALREADY_LIKE.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("INTERNAL_SERVER_ERROR 계열 상태 확인")
    void INTERNAL_SERVER_ERROR_계열_HttpStatus() {
        assertThat(Status.UPLOAD_FAIL.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(Status.DB_ERROR.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(Status.FAIL_MESSAGE.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("전체 Status enum 개수 확인")
    void 전체_enum_개수() {
        assertThat(Status.values()).hasSize(31);
    }
}
