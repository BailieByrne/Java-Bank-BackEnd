package Users;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import Security.RegisterRequest;

public class UserMapper {
	private final static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static User mapToUser(RegisterRequest registerRequest) {
        return new User(
            null, // Assuming the id is auto-generated by the database
            registerRequest.getKeeper(),
            "USER", 
            encoder.encode(registerRequest.getPassword())
        );
    }
}