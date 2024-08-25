package Security;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	public static final Logger log = LogManager.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JWTProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // Verify token and extract claims
                var claims = jwtProvider.verifyToken(token);
                Integer tokenUuid = jwtProvider.getUuidFromToken(token);
                String auths = jwtProvider.getAuthFromToken(token);
                String name = jwtProvider.getKeeperFromToken(token);

                // Match request URI and extract UUID
                String requestUri = request.getRequestURI();
                Pattern pattern = Pattern.compile("/api/accs/(.+)");
                Matcher matcher = pattern.matcher(requestUri);

                if (matcher.matches()) {
                    String requestUuid = matcher.group(1);
                    if (tokenUuid != null && tokenUuid.toString().equals(requestUuid)) {
                        // Directly set the SecurityContext as authenticated
                        AnonymousAuthenticationToken authentication = 
                                new AnonymousAuthenticationToken(token, name, 
                                List.of(new SimpleGrantedAuthority(auths)));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("AUTHENTICATED "+ name);
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid UUID");
                        return;
                    }
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
