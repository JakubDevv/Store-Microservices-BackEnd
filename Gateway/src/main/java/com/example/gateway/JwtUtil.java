package com.example.gateway;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    public static String ACCESS_SECRET = "1292104-0FOAipnafniasf-0";

    public static String REFRESH_SECRET = ";OIH1214oihasihioIOASDHI";

    public boolean validateAccessToken(final String token){
        return JWT.require(Algorithm.HMAC256(ACCESS_SECRET)).build()
                .verify(token).getExpiresAt().after(Date.from(Instant.now()));
    }
}
