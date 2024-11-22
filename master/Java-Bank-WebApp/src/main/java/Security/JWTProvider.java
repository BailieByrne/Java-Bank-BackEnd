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
    
    private String createTestToken(String type) {
    	if (type.equals("TEST")) {
        return JWT.create()
                .withSubject("TEST")
                .withExpiresAt(Instant.now().plus(Duration.of(TOKEN_EXPIRATION_DAYS, ChronoUnit.MINUTES)))
                .withClaim("UUID", 14)
                .withClaim("auth", "USER")
                .sign(Algorithm.HMAC256("f355d508365411c71fb3a7fe1ff2aef3e511a4a56404a17f99120d9b1b50bd95b72c14c837c3a08c477fa8951bdb058a1a6dd81546c066a4debef9488c1f58f9edbfb2f75107a7fdb87db1c784a88527c8185ca9252bc27657ad256924afff443ce476fab89a06bea4c62281d1d6e4b69abd13df1b1a409f156dc6eaeb0fb1b9a05e4df5bf7a6751c2b1abe82e87c08cca4fc3331d4c07059a63e1b2dd30050cbe9d7b9a35e7dce1391bbf9ed62cad3c841754dc15e8ca5361f4cdab00fa3b9e050502129c95ef0b189741727936db8dc67454b16ccee85336c2dc96c41df6af9bf941818d9cc41765f334b8a4631bcca76ce1808523a39da05723a3569f3da2b634ba5fa78208ab445fa7da29c4710acc0badfab0cb523b23c104d51f36a749a758955ab9813611f7eaf3a805a54ef5edf406178ad8364146d9a1bb1be72ed4"));
    }else {
    	return JWT.create()
                .withSubject("TEST_ADMIN")
                .withExpiresAt(Instant.now().plus(Duration.of(TOKEN_EXPIRATION_DAYS, ChronoUnit.MINUTES)))
                .withClaim("UUID", 0)
                .withClaim("auth", "ADMIN")
                .sign(Algorithm.HMAC256("f355d508365411c71fb3a7fe1ff2aef3e511a4a56404a17f99120d9b1b50bd95b72c14c837c3a08c477fa8951bdb058a1a6dd81546c066a4debef9488c1f58f9edbfb2f75107a7fdb87db1c784a88527c8185ca9252bc27657ad256924afff443ce476fab89a06bea4c62281d1d6e4b69abd13df1b1a409f156dc6eaeb0fb1b9a05e4df5bf7a6751c2b1abe82e87c08cca4fc3331d4c07059a63e1b2dd30050cbe9d7b9a35e7dce1391bbf9ed62cad3c841754dc15e8ca5361f4cdab00fa3b9e050502129c95ef0b189741727936db8dc67454b16ccee85336c2dc96c41df6af9bf941818d9cc41765f334b8a4631bcca76ce1808523a39da05723a3569f3da2b634ba5fa78208ab445fa7da29c4710acc0badfab0cb523b23c104d51f36a749a758955ab9813611f7eaf3a805a54ef5edf406178ad8364146d9a1bb1be72ed4"));
    }	
    }
    
    public String issueTest(String type) {
    		String token = createTestToken(type);
    	
            loggedInUsers.add("TEST");
            validTokens.add(token);
            return token;
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
