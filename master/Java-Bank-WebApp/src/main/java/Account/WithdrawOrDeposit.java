package Account;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.RequiredArgsConstructor;
import Account.CrudAccRepository;

@Service
@RequiredArgsConstructor
public class WithdrawOrDeposit {
	private static final Logger log = LogManager.getLogger(WithdrawOrDeposit.class);
	
	private static CrudAccRepository accRepository;
	
	@Autowired
	
	public void setAccRepo(CrudAccRepository accRepository) {
		WithdrawOrDeposit.accRepository = accRepository;
	}


	
	@Transactional
	public static ResponseEntity<String> withdraw(Integer OwnerID, Integer AccID, double amount) {		
		
		
		if (amount < 0 || amount == 0) {
			return new ResponseEntity<>("Invalid Withdraw Amount", HttpStatus.BAD_REQUEST);
		}
		
		
	    try {
	        Optional<Integer> prevBalOpt = accRepository.getBalance(OwnerID, AccID);
	        
	        if (prevBalOpt.isEmpty()) {
	            log.warn("Account not found or balance is null");
	            return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
	        }
	        
	        Integer prevBal = prevBalOpt.get();
	        
	        if (amount > prevBal) {
	            log.warn("Insufficient Funds");
	            return new ResponseEntity<>("Insufficient Funds", HttpStatus.BAD_REQUEST);
	        } else {
	            accRepository.updateBalance((prevBal - amount), OwnerID, AccID);
	            log.info("Balance updated for User " + OwnerID + " for Account " + AccID);
	            return new ResponseEntity<>("Withdrawal Success", HttpStatus.OK);
	        }
	    } catch (Exception e) {
	        log.error("Error during withdrawal", e);
	        return new ResponseEntity<>("Error", HttpStatus.UNAUTHORIZED);
	    }
	}

	@Transactional
	public static ResponseEntity<String> deposit(Integer OwnerID, Integer AccID, double amount) {
		
		
		
		if (amount < 0 || amount == 0) {
			return new ResponseEntity<>("Invalid Deposit Amount", HttpStatus.BAD_REQUEST);
		}
		
		
	    try {
	        Optional<Integer> prevBalOpt = accRepository.getBalance(OwnerID, AccID);
	        
	        if (prevBalOpt.isEmpty()) {
	            log.warn("Account not found or balance is null");
	            return new ResponseEntity<>("Account not found", HttpStatus.BAD_REQUEST);
	        }
	        Integer prevBal = prevBalOpt.get();
	        
	        accRepository.updateBalance((prevBal + amount), OwnerID, AccID);
	        log.info("Balance updated for User " + OwnerID + " for Account " + AccID);
	        return new ResponseEntity<>("Deposit Success", HttpStatus.OK);
	    } catch (Exception e) {
	        log.error("Error during deposit", e);
	        return new ResponseEntity<>("Error", HttpStatus.UNAUTHORIZED);
	    }
	}
}
