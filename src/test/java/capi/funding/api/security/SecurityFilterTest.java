package capi.funding.api.security;

import capi.funding.api.entity.User;
import capi.funding.api.repository.UserRepository;
import capi.funding.api.utils.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    private User user;

    @InjectMocks
    private SecurityFilter securityFilter;
    @Mock
    private Utils utils;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;
    @Captor
    private ArgumentCaptor<String> tokenCaptor;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "gabriel@gmail.com",
                "$2a$10$WjNOD14Yf.LCe3L6gGT9IemiY.4qtxcpv4AEl8DFjxt3HmyKlPn62",
                "Gabriel",
                true,
                LocalDateTime.now().minusDays(45),
                null
        );
    }

    @Test
    @DisplayName("doFilterInternal - should recover the token")
    void testShouldRecoverTheToken() throws ServletException, IOException {
        securityFilter.addUserCache(user);

        final String token = "testToken";
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + token);

        when(tokenService.validateToken(token)).thenReturn(user.getEmail());

        securityFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        verify(tokenService).validateToken(tokenCaptor.capture());

        assertEquals(token, tokenCaptor.getValue());
    }

    @Test
    @DisplayName("doFilterInternal - should do nothing when request doesn't has authorization header")
    void testShouldDoNothinWhenRequestDoesntHasAuthorizationHeader() throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(null);
        securityFilter.addUserCache(user);

        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(tokenService);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("doFilterInternal - shouldn't fetch user from database when it is in cache")
    void testShouldntFetchUserFromDatabaseWhenItIsInCache() throws ServletException, IOException {
        securityFilter.addUserCache(user);

        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");

        when(tokenService.validateToken("token")).thenReturn(user.getEmail());

        securityFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("doFilterInternal - should fetch user from database and cache when it isn't in cache")
    void testShouldFetchUserFromDatabaseAndCacheWhenItIsntInCache() throws ServletException, IOException {
        securityFilter.clearUsersCache();

        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer token");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(tokenService.validateToken("token")).thenReturn(user.getEmail());

        securityFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        verify(userRepository).findByEmail(user.getEmail());

        assertFalse(securityFilter.getUsersCache().isEmpty());
        assertEquals(user, securityFilter.getUsersCache().get(user.getEmail()));
    }

    @Test
    @DisplayName("doFilterInternal - shouldn't accept null parameters")
    void testShouldntAcceptNullParameters() {
        assertThrows(IllegalArgumentException.class, () ->
                securityFilter.doFilterInternal(null, httpServletResponse, filterChain));

        assertThrows(IllegalArgumentException.class, () ->
                securityFilter.doFilterInternal(httpServletRequest, null, filterChain));

        assertThrows(IllegalArgumentException.class, () ->
                securityFilter.doFilterInternal(httpServletRequest, httpServletResponse, null));
    }
}
