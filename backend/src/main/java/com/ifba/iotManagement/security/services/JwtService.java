package com.ifba.iotManagement.security.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public final class JwtService {
    @Value("${jwt.expiration}")
    private Long jwtExpirationInSeconds;
    @Value("${jwt.secret}")
    private String jwtSecret;


    public String generateToken(UUID userPublicId, List<GrantedAuthority> authorities) {

        SecretKey secretKey = new OctetSequenceKey.Builder(jwtSecret.getBytes()).algorithm(JWSAlgorithm.HS256).build().toSecretKey();
        final var header = new JWSHeader(JWSAlgorithm.HS256);

        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer("iot-management")
                .issueTime(Date.from(now))
                .claim("jti", UUID.randomUUID().toString())
                .expirationTime(Date.from(now.plusSeconds(jwtExpirationInSeconds)))
                .subject(userPublicId.toString())
                .claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        var jwt = new SignedJWT(header, claims);
        try{
            var signer = new MACSigner(secretKey);
            jwt.sign(signer);
        }catch (JOSEException e){
            throw new RuntimeException("Error signing the JWT token", e);
        }
        return jwt.serialize();
    }
}
