package api.security.jwt;

import lombok.Getter;

@Getter
public class JwtSubject {
    private Long userId;
    private String role;
    private String username;

    public static JwtSubject of(Long userId,String role,String username) {
        JwtSubject jwtSubject = new JwtSubject();
        jwtSubject.userId = userId;
        jwtSubject.role = role;
        jwtSubject.username = username;
        return jwtSubject;
    }
}
