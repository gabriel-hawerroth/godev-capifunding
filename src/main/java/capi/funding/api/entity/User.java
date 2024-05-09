package capi.funding.api.entity;

import capi.funding.api.dto.CreateUserDTO;
import capi.funding.api.dto.EditUserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @Size(min = 8, message = "a senha deve conter pelo menos 8 caracteres")
    @Pattern(regexp = ".*[a-z].*", message = "a senha deve conter pelo menos 1 letra minúscula")
    @Pattern(regexp = ".*[A-Z].*", message = "a senha deve conter pelo menos 1 letra maiúscula")
    @Pattern(regexp = ".*\\d.*", message = "a senha deve conter pelo menos 1 número")
    @Pattern(regexp = ".*[!@#$%^&*()_+{}\\[\\]:;,.<>/?~\\\\].*", message = "a senha deve conter pelo menos 1 caracter especial")
    private String password;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean active;

    @PastOrPresent
    @Column(nullable = false, updatable = false)
    private LocalDateTime creation_date;

    private byte[] profile_image;

    public User(CreateUserDTO dto) {
        this.email = dto.email();
        this.password = dto.password();
        this.name = dto.name();
        this.active = false;
        this.creation_date = LocalDateTime.now();
    }

    public void updateValues(EditUserDTO dto) {
        this.name = dto.name();
    }

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
