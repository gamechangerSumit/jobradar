package com.jobradar.security;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
class AuthRateLimiterTest {
 @Test void allowsTenAttemptsThenReturnsTooManyRequests(){
  AuthRateLimiter limiter=new AuthRateLimiter();
  for(int i=0;i<10;i++)assertDoesNotThrow(()->limiter.check("127.0.0.1:login"));
  ResponseStatusException ex=assertThrows(ResponseStatusException.class,()->limiter.check("127.0.0.1:login"));
  assertEquals(429,ex.getStatusCode().value());
 }
 @Test void tracksDifferentClientKeysIndependently(){
  AuthRateLimiter limiter=new AuthRateLimiter();
  for(int i=0;i<10;i++)limiter.check("client-a");
  assertDoesNotThrow(()->limiter.check("client-b"));
 }
}