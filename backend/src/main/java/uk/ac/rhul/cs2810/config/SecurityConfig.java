package uk.ac.rhul.cs2810.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** Configures Spring Security and CORS settings for the application. */
@Configuration
public class SecurityConfig {

  /**
   * Configures the security filter chain.
   * - Disables CSRF (dev-friendly for APIs)
   * - Enables CORS (your existing config)
   * - Permits /api/** so the frontend can call the backend without logging in
   * - Permits /h2-console/** (and allows frames) so H2 console works
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(withDefaults())
      // H2 console needs frames
      .headers(headers -> headers.frameOptions(frame -> frame.disable()))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/**").permitAll()
        .requestMatchers("/h2-console/**").permitAll()
        .anyRequest().permitAll()
      );

    return http.build();
  }

  /** Configures CORS to allow all origins, headers, and methods without credentials. */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(Arrays.asList("*"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setAllowedMethods(Arrays.asList("*"));
    config.setAllowCredentials(false);
    config.applyPermitDefaultValues();

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return source;
  }
}