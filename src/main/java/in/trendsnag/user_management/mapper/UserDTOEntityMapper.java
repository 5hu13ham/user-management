package in.trendsnag.user_management.mapper;


import in.trendsnag.user_management.dto.UserRequestDTO;
import in.trendsnag.user_management.dto.UserResponseDTO;
import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.model.Role;


public class UserDTOEntityMapper {

	public static User toEntity(UserRequestDTO dto) {
		User user = new User();
		user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setAge(dto.getAge());
        //user.setRole(Role.valueOf(dto.getRole().toUpperCase())); // Assumes Role is already parsed from String enum
        user.setPassword(dto.getPassword());
		return user;
	}
	
	public static UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhone(),
            user.getAge(),
            user.getRole().getName(),
            user.isActive(),
            user.getCreatedAt(),
            user.getUpdatedAt());
	}
}
