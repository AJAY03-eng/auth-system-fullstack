package com.example.demo;

import java.util.List;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig 
{

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter)
	{
		this.jwtAuthenticationFilter=jwtAuthenticationFilter;
	}
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(
	        HttpSecurity http,
	        AuthenticationProvider authenticationProvider)
	        throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())
	        .cors(Customizer.withDefaults())
	        .sessionManagement(session ->
	            session.sessionCreationPolicy(
	                SessionCreationPolicy.STATELESS
	            )
	        )
	        .authenticationProvider(authenticationProvider)
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
	            .requestMatchers(
	                "/api/auth/register",
	                "/api/auth/login"
	            ).permitAll()
	            .anyRequest().authenticated()
	        )
	        .addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class)
	    .exceptionHandling(exception -> exception
	    	    .authenticationEntryPoint(
	    	        (request, response, authException) ->
	    	            response.sendError(
	    	                HttpServletResponse.SC_UNAUTHORIZED
	    	            )
	    	    )
	    	);
	    return http.build();
	}
	

	
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource()
	{
		CorsConfiguration config=new CorsConfiguration();
		
		config.setAllowedOrigins(List.of("http://localhost:5173"));
		config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
		config.setAllowedHeaders(List.of("Content-Type","Authorization"));
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		
		source.registerCorsConfiguration("/**", config);
		
		return source;
		
	}
	
	
	
	@Bean
	public AuthenticationProvider authenticationProvider(
	        UserDetailsService userDetailsService) {

	    DaoAuthenticationProvider provider =
	            new DaoAuthenticationProvider(userDetailsService);

	    provider.setPasswordEncoder(passwordEncoder());

	    return provider;
	}


	@Bean
	public AuthenticationManager authenticationManager(
	        AuthenticationConfiguration configuration)
	        throws Exception {

	    return configuration.getAuthenticationManager();
	}
	

}
