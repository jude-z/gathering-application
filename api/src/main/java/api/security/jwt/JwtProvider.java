package api.security.jwt;

import entity.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final int accessExpiration;
    private final int refreshExpiration;

    public JwtProvider(@Value("${jwt.secretKey}") String secretKey,
                       @Value("${jwt.access.expiration}") int accessExpiration,
                       @Value("${jwt.refresh.expiration}") int refreshExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secretKey));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String createAccessToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().toString())
                .claim("id", user.getId())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpiration * 60 * 1000L))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().toString())
                .claim("id", user.getId())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpiration * 60 * 1000L))
                .signWith(secretKey)
                .compact();
    }
}
