package capi.funding.api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true, nullable = false, length = 100, updatable = false)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 60)
    @Size(min = 8, message = "The password must be at least 8 characters long")
    @Pattern.List({
            @Pattern(regexp = ".*[a-z].*", message = "The password must contain at least 1 lowercase letter"),
            @Pattern(regexp = ".*[A-Z].*", message = "The password must contain at least 1 uppercase letter"),
            @Pattern(regexp = ".*\\d.*", message = "The password must contain at least 1 number"),
            @Pattern(regexp = ".*[!@#$%^&*()_+{}\\[\\]:;,.<>/?~\\\\].*", message = "The password must contain at least 1 special character")
    })
    private String password;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, length = 5, updatable = false)
    private String role;

    @PastOrPresent
    @Column(nullable = false, updatable = false)
    private LocalDateTime creation_date;

    private byte[] profile_image;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
