package in.trendsnag.user_management.controller;

import in.trendsnag.user_management.dto.UserRequestDTO;
import in.trendsnag.user_management.dto.UserResponseDTO;
import in.trendsnag.user_management.mapper.UserDTOEntityMapper;
import in.trendsnag.user_management.model.User;
import in.trendsnag.user_management.payload.ApiResponse;
import in.trendsnag.user_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService users;
    public UserController(UserService users) { this.users=users; }

    @GetMapping("/me")
    public ApiResponse<UserResponseDTO> me(Authentication authentication) {
        return new ApiResponse<>("Current user", UserDTOEntityMapper.toResponseDTO((User)authentication.getPrincipal()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ApiResponse<Map<String,Object>> all(@RequestParam(defaultValue="0") int page,
                                                @RequestParam(defaultValue="10") int size) {
        return toPage(users.list(page,size));
    }

    @GetMapping("/filter-search")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ApiResponse<Map<String,Object>> filter(
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) String role,
            @RequestParam(required=false) Boolean active,
            @RequestParam(required=false) Boolean deleted,
            @RequestParam(required=false) String ageGroup,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(defaultValue="username") String sortBy,
            @RequestParam(defaultValue="asc") String sortDir) {
        return toPage(users.filter(keyword,role,active,deleted,ageGroup,page,size,sortBy,sortDir));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ApiResponse<UserResponseDTO> find(@PathVariable String id) {
        return new ApiResponse<>("User found", UserDTOEntityMapper.toResponseDTO(users.findUserById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_WRITE')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> create(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("User created",UserDTOEntityMapper.toResponseDTO(users.createUser(request))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_WRITE')")
    public ApiResponse<UserResponseDTO> update(@PathVariable String id, @Valid @RequestBody UserRequestDTO request) {
        return new ApiResponse<>("User updated",UserDTOEntityMapper.toResponseDTO(users.updateUser(id,request)));
    }

    public record ToggleActiveRequest(boolean active) {}

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAuthority('ADMIN_WRITE')")
    public ApiResponse<UserResponseDTO> active(@PathVariable String id, @RequestBody ToggleActiveRequest request) {
        return new ApiResponse<>("Status updated",UserDTOEntityMapper.toResponseDTO(users.setActive(id,request.active())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_WRITE')")
    public ApiResponse<Void> delete(@PathVariable String id) {
        users.softDeleteUser(id);
        return new ApiResponse<>("User soft deleted",null);
    }

    private ApiResponse<Map<String,Object>> toPage(Page<User> page) {
        Map<String,Object> info=new HashMap<>();
        info.put("users",page.getContent().stream().map(UserDTOEntityMapper::toResponseDTO).toList());
        info.put("currentPage",page.getNumber());
        info.put("totalItems",page.getTotalElements());
        info.put("totalPages",page.getTotalPages());
        info.put("pageSize",page.getSize());
        info.put("hasNext",page.hasNext());
        info.put("hasPrevious",page.hasPrevious());
        return new ApiResponse<>("Users fetched",info);
    }
}
