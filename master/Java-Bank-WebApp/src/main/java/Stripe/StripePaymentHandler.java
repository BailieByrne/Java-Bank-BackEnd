package Stripe;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripePaymentHandler {

	@Value("${stripe.apiKey}")
    private String apiKey;

    public Map<String, Object> createPaymentIntent(int amount) {
        Stripe.apiKey = apiKey;

        try {
            PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                    .setAmount((long) amount) // Amount in cents
                    .setCurrency("gbp") // Change currency as needed
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create payment intent", e);
        }
    }


    public void CheckRefunds() {
    	
    }
}

	
