package Security;

import java.io.IOException;
import java.util.Collections;
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
        // The authorization header contains 7 redundant characters "Bearer "
        // So stripping this leaves you with the token
        String header = request.getHeader("Authorization");
        
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var claims = jwtProvider.verifyToken(token);
                Integer tokenUuid = jwtProvider.getUuidFromToken(token);
                String auths = jwtProvider.getAuthFromToken(token);
                String name = jwtProvider.getKeeperFromToken(token);

                String requestUri = request.getRequestURI();

                // Updated patterns to handle the payment deposit path
                Pattern accsPattern = Pattern.compile("/api/accs/(?!withdraw|deposit)([^/]+)");
                Pattern devPattern = Pattern.compile("/api/dev/.*");
                Pattern withdrawPattern = Pattern.compile("/api/accs/withdraw/([^/]+)/.*");
                Pattern depositPattern = Pattern.compile("/api/accs/deposit/([^/]+)/.*");
                Pattern paymentDepositPattern = Pattern.compile("/api/payment/deposit/([^/]+)"); 


                Matcher accsMatcher = accsPattern.matcher(requestUri);
                Matcher devMatcher = devPattern.matcher(requestUri);
                Matcher withdrawMatcher = withdrawPattern.matcher(requestUri);
                Matcher depositMatcher = depositPattern.matcher(requestUri);
                Matcher paymentDepositMatcher = paymentDepositPattern.matcher(requestUri); // Matcher for the new path

                
                
                // Handle all matched patterns
                if (accsMatcher.matches() || withdrawMatcher.matches() || depositMatcher.matches() || devMatcher.matches() || paymentDepositMatcher.matches()) {
                    String requestUuid = 
                        accsMatcher.matches() ? accsMatcher.group(1) :
                        withdrawMatcher.matches() ? withdrawMatcher.group(1) :
                        depositMatcher.matches() ? depositMatcher.group(1) :
                        paymentDepositMatcher.matches() ? paymentDepositMatcher.group(1) : 
                        "dev-request";
                    
         

                    // Check if the token's UUID matches the request's UUID or if the user has ADMIN rights
                    if (tokenUuid != null && tokenUuid.toString().equals(requestUuid) || auths.equals("ADMIN")) {
                        // Set the authentication context with the user's information
                        AnonymousAuthenticationToken authentication = 
                                new AnonymousAuthenticationToken(token, name, 
                                List.of(new SimpleGrantedAuthority(auths)));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
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

