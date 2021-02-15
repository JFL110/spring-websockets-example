package dev.jamesleach.socketcanvas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application entry point
 * 
 * @author jim
 *
 */
@SpringBootApplication
@EnableScheduling
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}