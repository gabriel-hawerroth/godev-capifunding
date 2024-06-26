package capi.funding.api.security;

import capi.funding.api.entity.User;
import capi.funding.api.infra.exceptions.NotFoundException;
import capi.funding.api.repository.UserRepository;
import capi.funding.api.utils.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    @Getter
    private final Map<String, User> usersCache = new ConcurrentHashMap<>();

    private final Utils utils;
    private final TokenService tokenService;
    private final UserRepository userRepository;

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
        final User user;

        if (usersCache.containsKey(email)) {
            user = usersCache.get(email);
        } else {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("user not found"));
            usersCache.put(email, user);
        }

        return user;
    }

    public void addUserCache(User user) {
        utils.validateObject(user);
        this.usersCache.put(user.getEmail(), user);
    }

    public void clearUsersCache() {
        usersCache.clear();
    }
}
