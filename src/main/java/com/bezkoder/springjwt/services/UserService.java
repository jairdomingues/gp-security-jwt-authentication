package com.bezkoder.springjwt.services;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.bezkoder.springjwt.exception.CustomGenericNotFoundException;
import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.SignupRequest;
import com.bezkoder.springjwt.payload.response.JwtResponse;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUtils jwtUtils;

	public JwtResponse login(String username, String password, String fusoHorario) {

		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new CustomGenericNotFoundException("Username " + username + " inválido."));

		// verifica credenciais válidas
		if (!user.getEmail().equals(username)) {
			throw new CustomGenericNotFoundException("O email e/ou a senha são inválidos.");
		}

		// verifica usuário ativo
		if (!user.getActive()) {
			throw new CustomGenericNotFoundException("Usuário " + username + " não está ativo.");
		}

		// verifica usuario bloqueado
		if (user.getBlock()) {
			throw new CustomGenericNotFoundException("Usuário " + username + " está bloqueado.");
		}

		// atualiza data do login
		user.setLastLoginDate(new Date());
		userRepository.save(user);

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);

	}

//	public List<UserDTO> findAllUsers() {
//		List<User> users = (List<User>) userRepository.findAll();
//		return users.stream().map(this::convertToDTO).collect(Collectors.toList());
//	}

	public void createUser(SignupRequest signUpRequest) {

		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			throw new CustomGenericNotFoundException("Error: Username is already taken!");
		}

			if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new CustomGenericNotFoundException("Error: Email is already in use!");
		}

		User user = this.convertToEntity(signUpRequest);
		user.setPassword(encoder.encode(signUpRequest.getPassword()));
		user.setActive(true);
		user.setAdmin(false);
		user.setBlock(false);
		user.setEmailValid(false);
		user.setLastLoginDate(new Date());
		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();
		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new CustomGenericNotFoundException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new CustomGenericNotFoundException("Error: Role is not found."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new CustomGenericNotFoundException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}
		user.setRoles(roles);
		try {
			userRepository.save(user);
		} catch (Exception e) {
			throw new CustomGenericNotFoundException(e.getMessage());
		}
	}

//	public UserDTO findUserById(Long id) {
//		User user = userRepository.findById(id)
//				.orElseThrow(() -> new CustomGenericNotFoundException("ID: " + id.toString() + " não encontrado"));
//		return convertToDTO(user);
//	}

//	private UserDTO convertToDTO(User user) {
//		ModelMapper modelMapper = new ModelMapper();
//		return modelMapper.map(user, UserDTO.class);
//	}

	private User convertToEntity(SignupRequest signUpRequest) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(signUpRequest, User.class);
	}
	
	@Async	
	public CompletableFuture<String> createNotificationUser() {
	    final String uri = "http://localhost:8080/notification/token";
	    PushNotificationRequest push = new PushNotificationRequest();
	    push.setTitle("Hey you!");
	    push.setMessage("Watch out!");
	    push.setToken("cJ5ydBBWgY9PPynG2Sb9M2:APA91bGZw-WZt7whGKbAxKXBWzLVymWjxB8ZtoSVZYufsrtHgAog0dzDS3I4KI5juHwJOLvTC6WfxMcoy63XKQgEjaykncYO7fLr6417sMA8NLcpn-3KOiFkH9RjxiW9ytNSzF6HZhFZ");
	    push.setTopic("");
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    HttpEntity<PushNotificationRequest> entity = new HttpEntity<PushNotificationRequest>(push, headers);
	    
	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> result = restTemplate.postForEntity(uri, entity, String.class);
	    return CompletableFuture.completedFuture("OK");
	}
	
	@Async	
	public CompletableFuture<String> createAccount(String accountShare) {
		
	    final String uri = "http://localhost:8080/tansaction_history";
	    TransactionHistoryRequest push = new TransactionHistoryRequest();
	    push.setAmount(new BigDecimal("10.00"));
	    push.setHistory("Pagamento indicação de amigos.");
	    push.setIdAccount(new Long(accountShare));
	    push.setOperation("SHARE");
	    push.setOrderId(1l);
	    push.setStatus("BLOCK");
	    push.setTransactionType("CREDIT");
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    HttpEntity<TransactionHistoryRequest> entity = new HttpEntity<TransactionHistoryRequest>(push, headers);
	    
	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> result = restTemplate.postForEntity(uri, entity, String.class);
	    return CompletableFuture.completedFuture("OK");
	}

	
}
