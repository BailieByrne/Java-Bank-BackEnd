package Web;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import Security.LoginRequest;



@Controller
@RequestMapping("")
public class WebController {
	
	
	public static final Logger log = LogManager.getLogger(WebController.class);
	
	@GetMapping("/login")
	public String loginPage(Model model) {
		LoginRequest loginRequest = new LoginRequest(); // Example default value
	    model.addAttribute("loginRequest", loginRequest);
		log.info("Redirecting User To Front End");
		return "redirect:https://82.41.19.127:5173/login";
		}
	
	@GetMapping("")
	public String startpage(Model model) {
		LoginRequest loginRequest = new LoginRequest(); // Example default value
	    model.addAttribute("loginRequest", loginRequest);
	    log.info("Redirecting User To Front End");
		return "redirect:https://82.41.19.127:5173/login";
		}
	
	@GetMapping("/info")
	public String info() {
		log.info("Redirecting User To Front End");
		return "redirect:https://82.41.19.127:5173/login";
		}
	
	@GetMapping("/api/home")
	public String test() {
		log.info("Testing Secured EndPoint");
		return "test";
		}
}
