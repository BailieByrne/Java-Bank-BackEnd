package Security;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;

import Account.AccountException;
import Account.CrudAccRepository;
import Users.CrudUserRepository;
import Users.UserMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RegisterFormHandlerService {
    private final CrudUserRepository crudUserRepository;
    private final CrudAccRepository crudAccRepository;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public static final Logger log = LogManager.getLogger(RegisterFormHandlerService.class);

    public void createUser(@Validated RegisterRequest request) {
        var user = crudUserRepository.findByUsername(request.getKeeper());
        if (user != null ) {
            log.info("Username Already Exists");
            throw new AccountException("Username Already Exists");
        }
        
        if (request.getKeeper().isBlank() || request.getPassword().isBlank()) {
            log.info("Invalid Credentials");
            throw new AccountException("Username Already Exists");
        } 
        
        if(request.getKeeper().chars().anyMatch(c -> !Character.isLetter(c))) {
    		log.info("Invalid Credentials");
            throw new AccountException("Username Must Contain Only Letters");
        }
        
        
        crudUserRepository.save(UserMapper.mapToUser(request));
        crudAccRepository.save(UserMapper.mapToAccount(request));
        crudAccRepository.setOwnerID();
        log.info("Created User "+request.getKeeper());
    }
}