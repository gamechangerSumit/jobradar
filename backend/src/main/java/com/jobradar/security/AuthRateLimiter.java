package com.jobradar.security;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
/** Small per-instance fixed-window limiter. Use a shared Redis limiter for multi-replica production deployments. */
@Component
public class AuthRateLimiter {
 private static final long WINDOW_MS=Duration.ofMinutes(10).toMillis();
 private static final int MAX_ATTEMPTS=10;
 private final Map<String,Window> windows=new HashMap<>();
 public synchronized void check(String clientKey){
  long now=System.currentTimeMillis();
  if(windows.size()>10000)windows.entrySet().removeIf(e->now-e.getValue().startedAt()>WINDOW_MS);
  Window w=windows.get(clientKey);
  if(w==null||now-w.startedAt()>=WINDOW_MS){windows.put(clientKey,new Window(now,1));return;}
  if(w.count()>=MAX_ATTEMPTS)throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Too many authentication attempts. Try again in a few minutes.");
  windows.put(clientKey,new Window(w.startedAt(),w.count()+1));
 }
 private record Window(long startedAt,int count){}
}