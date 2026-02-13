package in.trendsnag.user_management.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.trendsnag.user_management.model.Role;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

@Entity
@Table(name = "users")

public class User implements UserDetails{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "username",nullable = false, unique = true)
	private String username;
	
	@Column(name = "email", nullable = false, unique = true)
	private String email;
	
	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(nullable = true, unique = true)
	private String phone;
	
	@Column
	private Integer age;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", nullable = false, updatable = false)
	private Role role;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

	    Set<GrantedAuthority> authorities = new HashSet<>();

	    // Role authority
	    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

	    // Permission authorities
	    role.getPermissions()
	        .forEach(permission ->
	            authorities.add(new SimpleGrantedAuthority(permission.getName()))
	        );

	    return authorities;
	}

	
	@Column(name = "active", nullable = false)
	private boolean active;
	
	@Column(name = "is_deleted")
	private boolean isDeleted = false;
	

	@Column(nullable = false)
	private String password;
	
	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	@Override
	public boolean isAccountNonExpired() {
	    return true;
	}

	@Override
	public boolean isAccountNonLocked() {
	    return !isDeleted;
	}

	@Override
	public boolean isCredentialsNonExpired() {
	    return true;
	}

	@Override
	public boolean isEnabled() {
	    return active && !isDeleted;
	}

	
	
	public boolean isDeleted() {
		return isDeleted;
	}


	public void setDeleted(boolean deleted) {
		this.isDeleted = deleted;
	}
	
	
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}


	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
		
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
	    try {
	        this.role = role;
	    } catch (IllegalArgumentException ex) {
	        throw new IllegalArgumentException("Invalid role: " + role);
	    }
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getId() {
	return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
		}
		
	public void setEmail(String email) {
		this.email = email;
		}
		
	public String getUsername() {
		return username;
		}
			
	public void setUsername(String username) {
		this.username = username;
		}
			
	public String getFirstName() {
		return firstName;
		}
				
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
				
	public String getLastName() {
		return lastName;
	}
					
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public Integer getAge() {
		return age;
	}
	
	public void setAge(Integer age) {
		this.age = age;
	}	

}
