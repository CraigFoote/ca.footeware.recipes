package ca.footeware.recipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point to application.
 *
 * @author Footeware.ca
 */
@SpringBootApplication
public class RecipesApplication {

	/**
	 * Start application.
	 *
	 * @param args {@link String}[]
	 */
	public static void main(String[] args) {
		SpringApplication.run(RecipesApplication.class, args);
	}
}
