package com.juns.pay.utils;

import com.juns.pay.room.model.Room;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    @Value("${spring.jwt.secret}")
    private String SECRET_KEY;
    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;


    private Key getKey() {
        SecretKey key1 = Keys.secretKeyFor(SignatureAlgorithm.HS256); //or HS384 or HS512
        String base64Key = Encoders.BASE64.encode(key1.getEncoded());
        Key key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(this.SECRET_KEY),
            this.signatureAlgorithm.getJcaName());
        return key1;
    }

    private Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder()
            .setSigningKey(this.getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Long getRoomId(String token) {
        return this.extractAllClaims(token).get("roomId", Long.class);
    }

    public String doGenerateToken(long roomId) {

        Claims claims = Jwts.claims();
        claims.put("roomId", roomId);

        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
//        .setSubject(userId)
            .setClaims(claims)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(this.getKey(), this.signatureAlgorithm)
            .compact();
    }

    public Boolean validateToken(String token, Room room) {
        final Long roomId = this.getRoomId(token);
        return (roomId != null && roomId.equals(room.getId()));
    }

}