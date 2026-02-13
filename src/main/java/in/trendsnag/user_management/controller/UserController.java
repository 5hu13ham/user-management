package in.trendsnag.user_management.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import in.trendsnag.user_management.dto.UserRequestDTO;
import in.trendsnag.user_management.dto.UserResponseDTO;
import in.trendsnag.user_management.exception.GlobalExceptionHandler;
import in.trendsnag.user_management.mapper.UserDTOEntityMapper;
import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.payload.ApiErrorResponse;
import in.trendsnag.user_management.payload.ApiResponse;
import in.trendsnag.user_management.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.*;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@PostMapping
	@PreAuthorize("hasAuthority('USER_WRITE')")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
	    logger.info("Received request to create a new user with username: {}", requestDTO.getUsername());
	    
	    try {
	        User user = UserDTOEntityMapper.toEntity(requestDTO);
	        User created = userService.createUser(user);
	        UserResponseDTO responseDTO = UserDTOEntityMapper.toResponseDTO(created);

	        logger.info("User created successfully with ID: {}", created.getId());
	        return ResponseEntity.ok(new ApiResponse<>("User Creation Successful", responseDTO));

	    } catch (Exception ex) {
	        logger.error("Error occurred while creating user: {}", ex.getMessage(), ex);

	        ApiErrorResponse errorResponse = new ApiErrorResponse(
	                HttpStatus.INTERNAL_SERVER_ERROR.value(),
	                "User creation failed",
	                ex.getMessage(), // or a generic message
	                "/users"
	            );
	        
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}

	
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('USER_WRITE')")
	public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@Valid @PathVariable Long id,@RequestBody UserRequestDTO requestDTO){
		User user = UserDTOEntityMapper.toEntity(requestDTO);
		User updated = userService.updateUser(id, user);
		UserResponseDTO responseDTO = UserDTOEntityMapper.toResponseDTO(updated);
		ApiResponse<UserResponseDTO> apiResponse = new ApiResponse<>("User updated to new values", responseDTO);
		
		logger.info("Updated all information for the user with ID: {} and username: {} in the database.", id, user.getUsername());

		
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('ADMIN_READ')")
	public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size,
	        @RequestParam(defaultValue = "id,asc") String[] sort) {

	    Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
	    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

	    Page<User> users = userService.getAllUsers(pageable); // Make sure this returns Page<User>

	    List<UserResponseDTO> responseDTO = users.getContent().stream()
	            .map(user -> UserDTOEntityMapper.toResponseDTO(user))
	            .toList();

	    ApiResponse<List<UserResponseDTO>> apiResponse = new ApiResponse<>("Data of all users", responseDTO);
	    
	    logger.info("Request received to fetch all users, page: {}, size: {}, sort: {} {}", page, size, sort[0], sort[1]);
	    
	    return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	@GetMapping("/filter-search")
	@PreAuthorize("hasAuthority('ADMIN_READ')")
	public ResponseEntity<ApiResponse<Map<String, Object>>> filterAndSearchUsers(
	    @RequestParam(required = false) String keyword,
	    @RequestParam(required = false) String role,
	    @RequestParam(required = false) Boolean active,
	    @RequestParam(required = false) Boolean deleted,
	    @RequestParam(required = false) String ageGroup,
	    @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "10") int size,
	    @RequestParam(defaultValue = "id") String sortBy,
	    @RequestParam(defaultValue = "asc") String sortDir
	) {
	    Page<User> userPage = userService.FilterSearchSortUsers(
	        keyword, role, active, deleted, ageGroup, page, size, sortBy, sortDir
	    );

	    List<UserResponseDTO> userDTOs = userPage.getContent()
	        .stream()
	        .map(user -> UserDTOEntityMapper.toResponseDTO(user))
	        .toList();

	    Map<String, Object> data = new HashMap<>();
	    data.put("users", userDTOs);
	    data.put("currentPage", userPage.getNumber());
	    data.put("totalItems", userPage.getTotalElements());
	    data.put("totalPages", userPage.getTotalPages());
	    data.put("pageSize", userPage.getSize());
	    data.put("hasNext", userPage.hasNext());
	    data.put("hasPrevious", userPage.hasPrevious());

	    ApiResponse<Map<String, Object>> response = new ApiResponse<>(
	        "Filtered user list fetched successfully",
	        data
	    );

	    logger.info("Filtering users with keyword: {}, role: {}, active: {}, deleted: {}, ageGroup: {}", 
	             keyword, role, active, deleted, ageGroup);

	    
	    return ResponseEntity.ok(response);
	}

	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
		logger.info("GET /api/users/{} called", id);
		User user = userService.findUserById(id);
		UserResponseDTO responseDTO = UserDTOEntityMapper.toResponseDTO(user);
		ApiResponse<UserResponseDTO> apiResponse = new ApiResponse<>("Here's the information you require", responseDTO);
        
		logger.info("Response: {}", apiResponse);
		
		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN_WRITE')")

	public ResponseEntity<ApiResponse<UserResponseDTO>> softDeleteUser(@PathVariable Long id){
		userService.softDeleteUser(id);
		ApiResponse<UserResponseDTO> apiResponse = new ApiResponse<>("User deleted successfully", null);
		
		logger.warn("Soft delete requested for user with ID: {}", id);

		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}
}
