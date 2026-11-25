package com.jobradar.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class JwtService {
 private final SecretKey key; private final long hours;
 public JwtService(@Value("${jobradar.security.jwt-secret}") String secret,@Value("${jobradar.security.expiration-hours:24}") long hours){
  if(secret==null||secret.getBytes(StandardCharsets.UTF_8).length<32||secret.startsWith("replace-")||secret.contains("change-this")||secret.contains("local-development")) throw new IllegalStateException("Set a unique JOBRADAR_JWT_SECRET with at least 32 random characters.");
  key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.hours=hours;
 }
 public String generate(String email){Instant now=Instant.now();return Jwts.builder().subject(email).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(hours*3600))).signWith(key).compact();}
 public String subject(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();}
}