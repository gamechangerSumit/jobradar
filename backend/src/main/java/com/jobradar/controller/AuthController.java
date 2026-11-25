package com.jobradar.controller;
import com.jobradar.domain.UserAccount;
import com.jobradar.repository.UserAccountRepository;
import com.jobradar.security.JwtService;
import com.jobradar.security.AuthRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 public record RegisterRequest(@Email @NotBlank String email,@NotBlank @Size(min=2,max=120) String fullName,@NotBlank @Size(min=8,max=72) String password){}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank @Size(min=8,max=72) String password){}
 private final UserAccountRepository users;private final PasswordEncoder encoder;private final JwtService jwt;private final AuthRateLimiter rateLimiter;
 public AuthController(UserAccountRepository users,PasswordEncoder encoder,JwtService jwt,AuthRateLimiter rateLimiter){this.users=users;this.encoder=encoder;this.jwt=jwt;this.rateLimiter=rateLimiter;}
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
 public Map<String,Object> register(@Valid @RequestBody RegisterRequest r,HttpServletRequest request){
  rateLimiter.check(clientIp(request)+":register");
  String email=r.email().trim().toLowerCase(Locale.ROOT);
  if(users.existsByEmailIgnoreCase(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists.");
  return response(users.save(new UserAccount(email,r.fullName().trim(),encoder.encode(r.password()))));
 }
 @PostMapping("/login") public Map<String,Object> login(@Valid @RequestBody LoginRequest r,HttpServletRequest request){
  rateLimiter.check(clientIp(request)+":login");
  UserAccount u=users.findByEmailIgnoreCase(r.email().trim()).filter(x->encoder.matches(r.password(),x.getPasswordHash())).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password."));
  return response(u);
 }
 private String clientIp(HttpServletRequest request){String forwarded=request.getHeader("X-Real-IP");return forwarded==null||forwarded.isBlank()?request.getRemoteAddr():forwarded.trim();}
 private Map<String,Object> response(UserAccount u){return Map.of("token",jwt.generate(u.getEmail()),"tokenType","Bearer","userId",u.getId(),"email",u.getEmail(),"fullName",u.getFullName());}
}