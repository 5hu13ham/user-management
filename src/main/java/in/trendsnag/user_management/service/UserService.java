package in.trendsnag.user_management.service;


import in.trendsnag.user_management.dto.UserResponseDTO;
import in.trendsnag.user_management.mapper.UserDTOEntityMapper;
import in.trendsnag.user_management.model.*;
import in.trendsnag.user_management.repository.RoleRepository;
import in.trendsnag.user_management.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

interface UserServiceInterface {
	
	User createUser(User user);
	User updateUser(Long id, User user);
	void deleteUser(Long id);
	
	Page<User> getAllUsers(Pageable pageable);
	User findUserById(Long id);
	Optional<User> findUserByUsername(String username);
	Optional<User> findUserByEmail(String email);
	
	List<User> findByRole(Role role);
	List<User> findByActive(boolean active);
	List<User> findByFirstNameContaining(String keyword);
	
	void softDeleteUser (Long id);
	
	boolean isUsernameExists(String username);
	
	long countByRole(String role);
	long countByActive(boolean active);
	
	//UserResponseDTO updateUserByAdmin(Long id, UserRequestDTO requestDTO);
	void toggleUserActiveStatus(Long id, boolean isActive);
	
	Page<User> FilterSearchSortUsers(
		    String keyword,
		    String role,
		    Boolean active,
		    Boolean deleted,
		    String ageGroup,
		    int page,
		    int size,
		    String sortByField,
		    String sortDirection
		);

}

@Service
@Transactional
public class UserService implements UserServiceInterface{
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Override
	public User createUser(User user) {
		// TODO Auto-generated method stub
		
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
		    throw new IllegalArgumentException("Username taken, try another one");
		}
		
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setActive(true);
		
		return userRepository.save(user);
	}

	@Override
	public User updateUser(Long id, User user) {
	    User existingUser = userRepository.findActiveUserById(id)
	        .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
	    
	    if (!existingUser.getUsername().equals(user.getUsername()) &&
	            userRepository.findByUsername(user.getUsername()).isPresent()) {
	            throw new IllegalArgumentException("Username already taken");
	        }

	    if (!existingUser.getEmail().equals(user.getEmail()) &&
	            userRepository.findByEmail(user.getEmail()).isPresent()) {
	            throw new IllegalArgumentException("Email already exists");
	        }
	    existingUser.setUsername(user.getUsername());
	    existingUser.setEmail(user.getEmail());
	    existingUser.setFirstName(user.getFirstName());
	    existingUser.setLastName(user.getLastName());
	    existingUser.setPhone(user.getPhone());
	    existingUser.setAge(user.getAge());
	    existingUser.setUpdatedAt(LocalDateTime.now());

	    String password = user.getPassword();
	    if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
	        existingUser.setPassword(passwordEncoder.encode(password));
	    }

	    return userRepository.save(existingUser);
	  
	}
/*	
	public UserResponseDTO updateUserByAdmin(Long id, UserRequestDTO requestDTO) {
	    User existingUser = userRepository.findActiveUserById(id)
	        .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));

	    // Only allow update if username is not taken by someone else
	    if (!existingUser.getUsername().equals(requestDTO.getUsername()) &&
	        userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
	        throw new IllegalArgumentException("Username already taken");
	    }

	    // Only allow update if email is not taken by someone else
	    if (!existingUser.getEmail().equals(requestDTO.getEmail()) &&
	        userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
	        throw new IllegalArgumentException("Email already exists");
	    }

	    existingUser.setUsername(requestDTO.getUsername());
	    existingUser.setEmail(requestDTO.getEmail());
	    existingUser.setFirstName(requestDTO.getFirstName());
	    existingUser.setLastName(requestDTO.getLastName());
	    existingUser.setPhone(requestDTO.getPhone());
	    existingUser.setAge(requestDTO.getAge());

	    User updatedUser = userRepository.save(existingUser);
	    return UserDTOEntityMapper.toResponseDTO(updatedUser);
	}
	*/
	
	@Override
	public Page<User> FilterSearchSortUsers(
	    String keyword,
	    String role,
	    Boolean active,
	    Boolean deleted,
	    String ageGroup,
	    int page,
	    int size,
	    String sortByField,
	    String sortDirection
	) {
	    return userRepository.FilterSearchSortUsers(
	        keyword, role, active, deleted, ageGroup, page, size, sortByField, sortDirection
	    );
	}

	
	public void toggleUserActiveStatus(Long id, boolean isActive) {
	    User user = userRepository.findActiveUserById(id)
	        .orElseThrow(() -> new NoSuchElementException("User not found"));

	    user.setActive(isActive);
	    user.setDeleted(false);
	    userRepository.save(user);
	}




	@Override
	public void deleteUser(Long id) {
		// TODO Auto-generated method stub
		userRepository.deleteById(id);
	}

	@Override
	public Page<User> getAllUsers(Pageable pageable) {
		// TODO Auto-generated method stub
		
		return userRepository.findByIsDeletedFalse(pageable);
		
	}

	@Override
	public User findUserById(Long id) {
	    return userRepository.findById(id)
	            .filter(user -> !user.isDeleted())
	            .orElseThrow(() -> new NoSuchElementException("No user exists with this ID"));
	}
	@Override
	public Optional<User> findUserByUsername(String username) {
		// TODO Auto-generated method stub
		
		return userRepository.findByUsername(username);
	}

	@Override
	public Optional<User> findUserByEmail(String email) {
		// TODO Auto-generated method stub
		return userRepository.findByEmail(email);
	}

	@Override
	public List<User> findByRole(Role role) {
		// TODO Auto-generated method stub
		
		return userRepository.findByRole(role);
	}

	@Override
	public List<User> findByActive(boolean active) {
		// TODO Auto-generated method stub
		return userRepository.findByActive(active);
	}

	@Override
	public List<User> findByFirstNameContaining(String keyword) {
		// TODO Auto-generated method stub

	    return userRepository.findByFirstNameContaining(keyword);
	}

	@Override
	public void softDeleteUser(Long id) {
		// TODO Auto-generated method stub
		User user = userRepository.findActiveUserById(id)
				.orElseThrow(() -> new NoSuchElementException("User not found with id: "+id));
		
		user.setActive(false);
		user.setDeleted(true);
		userRepository.save(user);
		
	}

	@Override
	public boolean isUsernameExists(String username) {
		// TODO Auto-generated method stub
		return userRepository.findByUsername(username).isPresent();
	}

	@Override
	public long countByRole(String role) {
		// TODO Auto-generated method stub
		try {
	        Role roleEnum = roleRepository.findByName(role.toUpperCase());
	        return userRepository.countByRole(roleEnum);
	    } catch (IllegalArgumentException ex) {
	    	throw new IllegalArgumentException("Invalid role: " + role + ". Allowed: ADMIN, USER, MODERATOR");
	    }
	}

	@Override
	public long countByActive(boolean active) {
		// TODO Auto-generated method stub
		return userRepository.countByActive(active);
	}
	
}
