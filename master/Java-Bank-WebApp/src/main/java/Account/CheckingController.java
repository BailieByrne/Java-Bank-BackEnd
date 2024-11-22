package Account;


import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;

import Users.CrudUserRepository;
import Users.User;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/api/accs")
public class CheckingController{
	
	private static final Logger log = LogManager.getLogger(CheckingController.class);
	private CrudAccRepository accRepository;

	
	
	
	public CheckingController(CrudAccRepository accRepository) {
		this.accRepository = accRepository;
	}
	
	@GetMapping({"/all",""})
	List<CheckingAcc> findall(){
		log.info("RETRIEVING ALL ACCOUNTS");
		return accRepository.findAll();
	}
	
	
	@GetMapping("/{id}")
	List<CheckingAcc> getAccounts(@PathVariable("id") Integer id) {
	    log.info("RETRIEVING ACCOUNTS WITH ID " + id);
	    List<CheckingAcc> accounts = accRepository.findAllAccountsById(id);
	    if (accounts.isEmpty()) {
	        log.error("ACCOUNTS NOT FOUND WITH ID " + id);
	        throw new AccountException("Accounts Not Found");
	    }
	    return accounts;
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("")
	@Transactional
	void createAccount(@Valid @RequestBody CheckingAcc account) {
		log.info("CREATING ACCOUNT");
		if (account.id() != null) {
			log.error("ID PROVIDED FOR NON EXISTING ACCOUNT");
			throw new AccountException("ID PROVIDED DID YOU MEAN PUT");
		}else {
			accRepository.save(account);
			log.info("ACCOUNT CREATED");
		}
	}
	

	@ResponseStatus(HttpStatus.ACCEPTED)
	@DeleteMapping("/{id}")
	void deleteAccount(@PathVariable("id") Integer id) {
		log.warn("DELETING ACCOUNT "+id);
		try {
		accRepository.delete(accRepository.findById(id).get());
		} catch (Exception e){
			log.error("No Checking Account With ID "+id);
		}
	}
	
	
	@PostMapping("/withdraw/{Ownerid}/{account}/{amount}")
	ResponseEntity<String> withdraw(@PathVariable("Ownerid") Integer OwnerId, @PathVariable("account") Integer accID, @PathVariable("amount") double amount) {
		return WithdrawOrDeposit.withdraw(OwnerId, accID ,amount);
	}
	
	@PostMapping("/deposit/{Ownerid}/{account}/{amount}")
	ResponseEntity<String> deposit(@PathVariable("Ownerid") Integer OwnerId, @PathVariable("account") Integer accID, @PathVariable("amount") double amount) {
		return WithdrawOrDeposit.deposit(OwnerId, accID ,amount);
	}
	
	
}


