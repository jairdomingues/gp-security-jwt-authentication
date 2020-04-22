package com.bezkoder.springjwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bezkoder.springjwt.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	abstract Optional<User> findByUsername(String username);
	abstract Optional<User> findByEmail(String email);
	abstract Boolean existsByUsername(String username);
	abstract Boolean existsByEmail(String email);
}
