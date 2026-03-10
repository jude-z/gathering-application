package api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

    @Value("${jwt.secretKey}")
    private String secretKey;

    public Claims validateToken(String jwtToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
        return claims;
    }
}
