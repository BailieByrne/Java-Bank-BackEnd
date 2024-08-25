package Security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class CorrectLogin extends RuntimeException {
    public CorrectLogin(String message) {
        super(message);
    }
    
    
}