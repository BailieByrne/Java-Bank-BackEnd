package Account;


import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
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
	CheckingAcc getAccount(@PathVariable("id") Integer id){
		log.info("RETRIEVING ACCOUNT "+id);
		Optional<CheckingAcc> acc = accRepository.findById(id);
		if (acc.isEmpty()) {
			log.error("ACCOUNT NOT FOUND WITH ID "+id);
			throw new AccountException("Account Not Found");
		}
		return acc.get();
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
	@PutMapping("/{id}")
	@Transactional
	void updateAccount(@RequestBody CheckingAcc account, @PathVariable("id") Integer id) {
		log.info("UPDATING ACCOUNT " + account.id());
		accRepository.save(account);
	}

	@ResponseStatus(HttpStatus.ACCEPTED)
	@DeleteMapping("/{id}")
	void deleteAccount(@PathVariable("id") Integer id) {
		log.warn("DELETING ACCOUNT "+id);
		accRepository.delete(accRepository.findById(id).get());
	}
}


