package Security;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
@Configuration
@ConfigurationProperties("security.jwt")
public class JWTProperties {
	/*
	 * Private Secret Key Used For JWT issuing
	 */
	private String secretKey;

}
