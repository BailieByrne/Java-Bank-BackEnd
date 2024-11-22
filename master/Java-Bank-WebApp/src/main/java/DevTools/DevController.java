package DevTools;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import Account.AccountException;
import Account.CheckingAcc;
import Account.CrudAccRepository;
import Account.WithdrawOrDeposit;
import Security.JWTProvider;
import Users.CrudUserRepository;
import Users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.With;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevController {
	private final CrudUserRepository crudUserRepository;
	private final CrudAccRepository crudAccountRepository;
	private static final Logger log = LogManager.getLogger(DevController.class);
	private final JWTProvider jwtProvider;
	
	
	@GetMapping("/getusers")
	public List<String> response() {
		return jwtProvider.getUsers();
	}
	
	
	@GetMapping("/manage/{user}")
	public User manageruser(@PathVariable("user") String Username) {
		User user = crudUserRepository.findByUsername(Username);
		log.info("MANAGING "+ Username);
		return user.withHiddenPassword();
	}
	
	
	@PutMapping("/update")
	void updateAccount(@Valid @RequestBody CheckingAcc account) {
		System.out.println(account);
		crudAccountRepository.save(account);
}
	
	@GetMapping("/getall")
	public List<User> allusers(){
		return crudUserRepository.findAll();
	}
	
	
}
