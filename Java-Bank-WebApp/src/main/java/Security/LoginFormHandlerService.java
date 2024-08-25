package Security;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import Account.AccountException;
import Users.CrudUserRepository;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class LoginFormHandlerService {
	
	private final JWTProvider jwtProvider;
	private final CrudUserRepository crudUserRepository;
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	public static final Logger log = LogManager.getLogger(LoginFormHandlerService.class);
	
	public ResponseEntity<String> validateCredentials(@RequestBody @Validated LoginRequest request) {
		
		var user = crudUserRepository.findByUsername(request.getKeeper());
		
		if(user == null) {
			log.info("Account Does Not Exist");
			throw new AccountException("Account Does Not Exist");
		}else if (encoder.matches(request.getPassword(), user.password())) {
			var token = jwtProvider.issue(request.getKeeper(), user);
			log.info("JWT ISSUED TO "+request.getKeeper());
			throw new CorrectLogin(token); //For some reason response entity refused to return a body but this works
		}else {
			log.info("Invalid Credentials");
			throw new AccountException("Invalid Credentials");
		}
	}
}
