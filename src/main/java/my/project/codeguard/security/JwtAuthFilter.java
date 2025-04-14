package my.project.codeguard.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.project.codeguard.entity.User;
import my.project.codeguard.repository.UserRepository;
import my.project.codeguard.service.user.UserDetailsServiceImpl;
import my.project.codeguard.service.user.UserService;
import my.project.codeguard.util.JwtUtils;
import my.project.codeguard.util.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Autowired
    public JwtAuthFilter(JwtUtils jwtUtils, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = parseToken(request);
            if (token != null && !token.isBlank()) {
                DecodedJWT decodedJWT = jwtUtils.verifyJwtToken(token);
                String username = jwtUtils.retrieveClaimUsername(decodedJWT);
                Role authority = jwtUtils.retrieveClaimAuthority(decodedJWT);
                Long userId = jwtUtils.retrieveClaimUserId(decodedJWT);
                userRepository.findById(userId).orElseThrow(() -> new BadCredentialsException("User not found"));
                UserDetailsImpl userDetailsImpl = new UserDetailsImpl(new User(userId, username, authority));
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetailsImpl, userDetailsImpl.getPassword(), userDetailsImpl.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (JWTVerificationException exc) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid JWT Token");
        }
        filterChain.doFilter(request, response);
    }

    private String parseToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
