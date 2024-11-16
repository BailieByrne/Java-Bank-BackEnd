package Users;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import Account.CheckingAcc;
import Security.RegisterRequest;

public class UserMapper {
	private final static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static User mapToUser(RegisterRequest registerRequest) {
        return new User(
            null,
            registerRequest.getKeeper(),
            "USER", 
            encoder.encode(registerRequest.getPassword())
        );
     
    }
    public static CheckingAcc mapToAccount(RegisterRequest registerRequest) {
    	return new CheckingAcc(
    			null,
    			registerRequest.getKeeper(),
    			LocalDateTime.now(),
    			0.0,
    			-1);
    }
}