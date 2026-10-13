package com.example.demo;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppuserRepository extends JpaRepository<AppUser,Long> 
{

	Optional<AppUser> findByEmailIgnoreCase(String email);
	
	boolean existsByEmailIgnoreCase(String email);
}
