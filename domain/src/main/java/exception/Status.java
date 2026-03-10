package exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Status {

    SUCCESS("SU", "Success", HttpStatus.OK),
    NOT_FOUND_USER("NFU", "Not Found User", HttpStatus.NOT_FOUND),
    EXIST_USER("EU", "Already Exist User", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL("DE", "Duplicate Email", HttpStatus.CONFLICT),
    UN_CORRECT_PASSWORD("UCP", "UnCorrect Password", HttpStatus.UNAUTHORIZED),
    NOT_FOUND_CERTIFICATION("NFCT", "Not Found Certification", HttpStatus.NOT_FOUND),
    UN_CORRECT_CERTIFICATION("UCC", "Un Correct Certification", HttpStatus.BAD_REQUEST),
    UN_VALID_TOKEN("UVT", "Unvalid Token", HttpStatus.UNAUTHORIZED),
    NOT_FOUND_ALARM("NFA", "Not Found Alarm", HttpStatus.NOT_FOUND),
    NOT_FOUND_GATHERING("NFG", "Not Found Gathering", HttpStatus.NOT_FOUND),
    NOT_FOUND_CATEGORY("NFCA", "Not Found Category", HttpStatus.NOT_FOUND),
    NOT_FOUND_MEETING("NFM", "Not Found Meeting", HttpStatus.NOT_FOUND),
    NOT_AUTHORIZE("NA", "Not Authorized", HttpStatus.FORBIDDEN),
    ALREADY_ATTEND("AA", "Already Attend Meeting", HttpStatus.CONFLICT),
    NOT_WITHDRAW("NW", "Not Withdraw Meeting", HttpStatus.BAD_REQUEST),
    NOT_FOUND_ATTEND("NFAT", "Not Found Attend", HttpStatus.NOT_FOUND),
    ALREADY_ENROLLMENT("AEN", "Already Enrolled", HttpStatus.CONFLICT),
    NOT_FOUND_ENROLLMENT("NFE", "Not Found Enrollment", HttpStatus.NOT_FOUND),
    NOT_DIS_ENROLLMENT("NDE", "Opener Cannot Dis-Enrollment", HttpStatus.FORBIDDEN),
    ALREADY_LIKE("AL", "Already Like", HttpStatus.CONFLICT),
    NOT_FOUND_LIKE("NFL", "Not Found Like", HttpStatus.NOT_FOUND),
    NOT_FOUND_BOARD("NFB", "Not Found Board", HttpStatus.NOT_FOUND),
    NOT_FOUND_CHAT_ROOM("NFCR", "Not Found Chat Room", HttpStatus.NOT_FOUND),
    NOT_FOUND_CHAT_PARTICIPANT("NFCP", "Not Found Chat Participant", HttpStatus.NOT_FOUND),
    NOT_FOUND_IMAGE("NFI", "Not Found Image", HttpStatus.NOT_FOUND),
    UPLOAD_FAIL("UF", "Upload Fail", HttpStatus.INTERNAL_SERVER_ERROR),
    DB_ERROR("DBE", "Database Error", HttpStatus.INTERNAL_SERVER_ERROR),
    FAIL_MESSAGE("FM", "Fail Messaging From SMTP", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND_TOPIC("NFT", "Not Found Topic", HttpStatus.NOT_FOUND),
    ALREADY_SUBSCRIBE_TOPIC("AST", "Already Subscribe Topic", HttpStatus.CONFLICT),
    METHOD_NOT_ALLOWED("MNA", "Method Not Allowed", HttpStatus.METHOD_NOT_ALLOWED);

    private final String code;
    private final String content;
    private final HttpStatus httpStatus;
}
