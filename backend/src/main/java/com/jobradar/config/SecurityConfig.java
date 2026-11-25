package com.jobradar.config;
import com.jobradar.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthenticationFilter filter)throws Exception{
  return http.csrf(c->c.disable()).cors(Customizer.withDefaults()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a.requestMatchers("/api/auth/**","/api/jobs/**","/api/meta","/actuator/health","/error").permitAll().requestMatchers("/api/workspace/**").authenticated().requestMatchers("/api/admin/**").permitAll().anyRequest().permitAll())
   .addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class).build();
 }
}