package Security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegisterController {
	private final RegisterFormHandlerService registerFormHandlerService;
	public static final Logger log = LogManager.getLogger(AuthController.class);
	
	@PostMapping(value = "/register",consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<String> registerrequestHTMl(@ModelAttribute RegisterRequest request) {
		log.info("HANDLING HTML REGISTRATION");
		registerFormHandlerService.createUser(request);
		return ResponseEntity.ok("Registration Successfull");
	}
	
	@PostMapping(value = "/register",consumes = "application/json")
	public ResponseEntity<String> registerrequestJSON(@RequestBody RegisterRequest request) {
		log.info("HANDLING JSON REGISTRATION");
		registerFormHandlerService.createUser(request);
		return ResponseEntity.ok("Registration Successfull");
		
	}
}
