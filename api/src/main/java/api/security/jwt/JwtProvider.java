package api.security.jwt;

import entity.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {


    private final String secretKey;
    private final int accessExpiration;
    private final int refreshExpiration;
    private final Key SECRET_KEY;

    public JwtProvider(@Value("${jwt.secretKey}") String secretKey,
                       @Value("${jwt.access.expiration}") int accessExpiration,
                       @Value("${jwt.refresh.expiration}") int refreshExpiration) {
        this.secretKey = secretKey;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }


    public String createAccessToken(User user){
            Claims claims = Jwts.claims().setSubject(user.getUsername());
            claims.put("role", user.getRole().toString());
            claims.put("id",user.getId());
            Date now = new Date();
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime()+accessExpiration*60*1000L))
                    .signWith(SECRET_KEY)
                    .compact();
            return token;
    }

    public String createRefreshToken(User user){
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("role", user.getRole().toString());
        claims.put("id",user.getId());
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+refreshExpiration*60*1000L))
                .signWith(SECRET_KEY)
                .compact();
        return token;
    }


}
