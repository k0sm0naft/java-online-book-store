package ua.bookstore.online.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final SecretKey secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    private JwtUtil(@Value("${jwt.secret}") String secretString) {
        this.secret = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userName) {
        return Jwts.builder()
                   .subject(userName)
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + expiration))
                   .signWith(secret)
                   .compact();
    }

    public boolean isValidToken(String token) {
        if (token == null) {
            return false;
        }
        final Jws<Claims> claims = Jwts.parser()
                                       .verifyWith(secret)
                                       .build()
                .parseSignedClaims(token);
        return claims.getPayload().getExpiration().after(new Date());
    }

    public String getUserName(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }
}
