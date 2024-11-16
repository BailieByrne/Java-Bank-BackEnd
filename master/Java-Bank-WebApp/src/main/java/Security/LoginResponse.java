package Security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@ResponseStatus(HttpStatus.ACCEPTED)
public class LoginResponse {

    private final String accessToken;
   
}