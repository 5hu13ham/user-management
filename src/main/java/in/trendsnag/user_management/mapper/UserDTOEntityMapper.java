package in.trendsnag.user_management.mapper;

import in.trendsnag.user_management.dto.UserResponseDTO;
import in.trendsnag.user_management.model.User;

public final class UserDTOEntityMapper {
    private UserDTOEntityMapper() {}

    public static UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(), user.getPhone(),
                user.getAge(), user.getRole().getName(), user.isActive(),
                user.getCreatedAt(), user.getUpdatedAt());
    }
}
