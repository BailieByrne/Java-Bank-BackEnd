package Stripe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;



@Configuration
public class StripeConfig {
	
	@Value("${stripe.apiKey}")
	private String stripeSecretKey;
	
	public void initialize() {
		Stripe.apiKey = stripeSecretKey;
	}
	
	

}
