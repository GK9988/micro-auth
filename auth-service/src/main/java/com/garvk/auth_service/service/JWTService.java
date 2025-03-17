package com.garvk.auth_service.service;

import com.garvk.auth_service.model.UserCred;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    private String SECRET_KEY;

    public JWTService(){
        SECRET_KEY = generateSecretKey();
    }

    public String generateSecretKey(){
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGen.generateKey();
            System.out.println("Secret Key: " + secretKey.toString());
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String aInUsername){
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims(claims)
                .subject(aInUsername)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60*5))
                .signWith(getKey()).compact();
    }

    public SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String aInToken){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(aInToken).getPayload();
    }

    private <T> T extractClaim(String aInToken, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(aInToken);
        return claimResolver.apply(claims);
    }

    public String extractUserName(String aInToken){
        return extractClaim(aInToken, Claims::getSubject);
    }

    public void validateTokenFields(final String aInToken){
//        Jwts.parser().setSigningKey(getKey()).build().parseClaimsJws(aInToken);
        Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(aInToken);
    }

    public boolean validateToken(String aInToken, UserCred aInUserCred){
        final String userName = extractUserName(aInToken);
        return (userName.equals(aInUserCred.getName()) && !isTokenExpired(aInToken));
    }

    private Date extractExpiration(String aInToken){
        return extractClaim(aInToken, Claims::getExpiration);
    }

    private boolean isTokenExpired(String aInToken){
        return extractExpiration(aInToken).before(new Date());
    }
}
