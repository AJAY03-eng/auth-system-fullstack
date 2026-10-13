package com.example.demo;

import java.util.Map;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/auth")
public class AuthController 
{
	private final AppuserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	
	public AuthController(AppuserRepository repository , PasswordEncoder passwordEncoder , AuthenticationManager authenticationManager,JwtService jwtService)
	{
		this.repository=repository;
		this.passwordEncoder=passwordEncoder;
		this.authenticationManager=authenticationManager;
		this.jwtService=jwtService;
		
	}
	
	public record RegisterRequest(@NotBlank @Size(max=80) String name,@NotBlank @Email @Size(max=180) String email,@NotBlank @Size(min=8,max=72) String password)
	{}
	
	public record LoginRequest(@NotBlank @Email @Size(max=180) String email,@NotBlank @Size(max=72) String password)
	{}
	
	@PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        String email = request.email().trim().toLowerCase();

        if (repository.existsByEmailIgnoreCase(email)) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "message",
                    "An account with this email already exists."
                ));
        }

        String hashedPassword =
            passwordEncoder.encode(request.password());

        AppUser user = new AppUser();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(hashedPassword);
//        user.setRole("USER");

        repository.save(user);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(Map.of(
                "message",
                "Account created successfully."
            ));
    }
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request)
	{
		try 
		{ Authentication authentication = authenticationManager.authenticate( new UsernamePasswordAuthenticationToken( request.email().trim().toLowerCase(), request.password() ) );
		
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		String token = jwtService.generateToken(userDetails); 
		
		  return ResponseEntity.ok( Map.of( "message", "Login successful.", "token", token, "tokenType", "Bearer", "email", userDetails.getUsername() ) ); 
		} 
		catch (AuthenticationException exception) 
		{ 
			return ResponseEntity .status(HttpStatus.UNAUTHORIZED) .body( Map.of( "message", "Invalid email or password." ) );
		}
	}
	
	
	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser(
	        Authentication authentication) {

	    return ResponseEntity.ok(
	        Map.of(
	            "email", authentication.getName(),
	            "roles", authentication.getAuthorities()
	                    .stream()
	                    .map(GrantedAuthority::getAuthority)
	                    .toList()
	        )
	    );
	}
	

	
	
}
