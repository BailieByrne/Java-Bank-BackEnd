package Stripe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import Account.WithdrawOrDeposit;

import java.net.http.HttpRequest;
import java.util.Map;

@RestController
@RequestMapping("")
public class PaymentController {
	
	@Value("${stripe.apiKey}")
	private String stripeSecretKey;
	

    @Autowired
    private StripePaymentHandler stripeService;

    @PostMapping("/api/payment/deposit/{userId}")
    public ResponseEntity<Map<String, Object>> deposit(@RequestBody StripeRequest request) {
        int amount = (int) request.getAmount(); // Amount in cents
        return new ResponseEntity<>((stripeService.createPaymentIntent(amount)),HttpStatus.ACCEPTED);
    }
    
    @PostMapping("/verify-payment")
    public ResponseEntity<String> verifyPayment(@RequestBody VerifyRequest paymentRequest) {
        try {

            // Retrieve the PaymentIntent object from Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentRequest.getStripeID());

            // Check the payment status
            if ("succeeded".equals(paymentIntent.getStatus())) {
                // Payment is successful, you can perform actions like updating database
            	WithdrawOrDeposit.deposit(paymentRequest.getOwnerID(), paymentRequest.getAccountID(), (paymentIntent.getAmount() / 100));
                
            } else {
                // Payment failed
                return ResponseEntity.status(400).body("Payment failed: " + paymentIntent.getStatus());
            }
        } catch (StripeException e) {
            // Handle Stripe API errors
            return ResponseEntity.status(500).body("Error verifying payment: " + e.getMessage());
    }
        return ResponseEntity.status(200).body("Complete");
    }
}

