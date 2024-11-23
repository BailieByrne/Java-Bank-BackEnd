package com.example.demo;



import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import Account.CrudAccRepository;
import Security.JWTProvider;
import Users.CrudUserRepository;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@SpringBootApplication()
@EnableJdbcRepositories(basePackageClasses = {CrudAccRepository.class,CrudUserRepository.class}) //neccessary to scan other packages for components andd dependancy injection
@ComponentScan(basePackages = {"Account", "Web","Security","Users","DevTools","Stripe"})
public class DemoApplication {
	
	public static final Logger log = LogManager.getLogger("MainLogger");
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		log.info("APPLICATION STARTED SUCCESSFULLY!");
	}
	
	@Bean
	CommandLineRunner runner(CrudAccRepository accountRepository) {
		return args -> {
			new Thread(this::handleConsoleInput).start();
		};
	}
	
	private void handleConsoleInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Exiting...");
                break; // Exit the loop and terminate the thread
            }
            // Handle the input
            if(input.toLowerCase().equals("logout-all")) {
            	log.info("LOGGING OUT ALL USERS..");
            	JWTProvider.deauthorizeAll();
            }
            
            if(input.toLowerCase().equals("getusers")) {
            	System.out.println(JWTProvider.getUsers());
            	}
            else {
            	System.out.println("Unknown Command...");
            	}
        }
        scanner.close();
    }
	
}



