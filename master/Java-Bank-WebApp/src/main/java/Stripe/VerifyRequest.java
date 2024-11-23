package Stripe;

import lombok.Getter;

@Getter
public class VerifyRequest {
	
	private int ownerID;
	
	private int accountID;
	
	private String stripeID;

}
