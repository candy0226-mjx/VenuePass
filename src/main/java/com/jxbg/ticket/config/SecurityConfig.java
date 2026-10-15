package com.jxbg.ticket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        ApiResult.fail(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage())))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                                        ApiResult.fail(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMessage()))))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers("/doc.html", "/webjars/**", "/v3/api-docs/**", "/swagger-ui/**", "/review-images/**", "/ws", "/ws/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                        .requestMatchers("/notifications/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/venues/history").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/venues/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/venues/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/venues/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/venues/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/areas/*/seats").authenticated()
                        .requestMatchers(HttpMethod.POST, "/areas/*/seats").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/activities/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/activities/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/activities/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/price-rules/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/price-rules/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/sessions/*/seats").authenticated()

                        .requestMatchers(HttpMethod.POST, "/orders/timeout/close").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/orders/submit-token").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/orders/*/checkin-code").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/orders/*/cancel").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/orders/*/pay").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/orders").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers("/checkin/**").hasAnyRole("CHECKER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/reviews/pending").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/reviews/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/reviews/*/reject").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/reviews/my").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/reviews").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/reviews/*/like").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/reviews/*/like").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/reviews/*").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/reviews/*").authenticated()

                        .requestMatchers(HttpMethod.POST, "/uploads/review-images").hasRole("USER")
                        .requestMatchers("/payments/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard/**").hasRole("ADMIN")
                        .requestMatchers("/operation-logs/**").hasRole("ADMIN")
                        .requestMatchers("/export/my-orders").hasRole("USER")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void writeJson(HttpServletResponse response, int httpStatus, ApiResult<Void> result) throws java.io.IOException {
        response.setStatus(httpStatus);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
