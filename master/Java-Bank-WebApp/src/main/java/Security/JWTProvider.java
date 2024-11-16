package Security;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import Account.AccountException;
import Users.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTProvider {

    private static final Logger log = LogManager.getLogger(JWTProvider.class);
    private static final long TOKEN_EXPIRATION_DAYS = 1L;

    private final JWTProperties properties;
    private static List<String> loggedInUsers = new CopyOnWriteArrayList<>();
    private static List<String> validTokens = new CopyOnWriteArrayList<>();

    public String issue(String keeper, User user) {
        try {
            if (keeper == null || user == null) {
                throw new IllegalArgumentException("Keeper or user cannot be null");
            }

            String token = createToken(keeper, user);

            if (loggedInUsers.contains(keeper)) {
                int index = loggedInUsers.indexOf(keeper);
                validTokens.set(index, token);
                log.warn("REVOKED EXISTING JWT FOR {}", keeper);
            } else {
                loggedInUsers.add(keeper);
                validTokens.add(token);
            }

            return token;
        } catch (Exception e) {
            log.error("Error issuing token for {}: {}", keeper, e.getMessage(), e);
            throw new AccountException("Error issuing token");
        }
    }

    private String createToken(String keeper, User user) {
        return JWT.create()
                .withSubject(keeper)
                .withExpiresAt(Instant.now().plus(Duration.of(TOKEN_EXPIRATION_DAYS, ChronoUnit.DAYS)))
                .withClaim("UUID", user.id())
                .withClaim("auth", user.Roles())
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }

    public DecodedJWT verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(properties.getSecretKey());
        JWTVerifier verifier = JWT.require(algorithm)
                .build(); // Reusable verifier instance
        if(validTokens.contains(token)) {
        	return verifier.verify(token);
        }else {
        	throw new AccountException("TOKEN NO LONGER VALID");
        }
    }
    
    public Integer getUuidFromToken(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        return decodedJWT.getClaim("UUID").asInt();
    }
    public String getAuthFromToken(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        return decodedJWT.getClaim("auth").asString();
    }
    
	public String getKeeperFromToken(String token) {
		DecodedJWT decodedJWT = verifyToken(token);
		return decodedJWT.getSubject().toString();
	}   
	
	public void logoutUser(HttpServletRequest request) {
		try {
			String header = request.getHeader("Authorization");
			String token = header.substring(7);
			if (validTokens.contains(token)) {
			loggedInUsers.remove(getKeeperFromToken(token));
			log.info("Logged Out User: "+getKeeperFromToken(token));
			validTokens.remove(token);
			log.info("Token Revoked");
			}
		} catch (Exception e){
			log.error("Unable To Logout");
			log.error(e);
		}
		
	}
	
	public static void deauthorizeAll() {
		loggedInUsers = new CopyOnWriteArrayList<>();
	    validTokens = new CopyOnWriteArrayList<>();
	    log.info("DE-AUTHED ALL");
	}
	
	public static List<String> getUsers() {
		return loggedInUsers;
	}
	
	public static String getUser(String username) {
		if (loggedInUsers.contains(username)) {
			return "True";
		}else {
			return "False";
		}
	}
    
}
