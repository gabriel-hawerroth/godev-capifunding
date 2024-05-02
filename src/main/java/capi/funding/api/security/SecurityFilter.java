package capi.funding.api.security;

import capi.funding.api.exceptions.NotFoundException;
import capi.funding.api.models.User;
import capi.funding.api.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    public final Map<String, User> usersCache = new ConcurrentHashMap<>();

    private final TokenService tokenService;

    private final UserRepository userRepository;

    @Autowired
    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String token = recoverToken(request);

        if (token != null) {
            final String userMail = tokenService.validateToken(token);
            User user = findUserByMail(userMail);

            final var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

    private User findUserByMail(String email) {
        User user = usersCache.get(email);

        if (user == null) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("user not found"));
            usersCache.put(email, user);
        }

        return user;
    }
}
