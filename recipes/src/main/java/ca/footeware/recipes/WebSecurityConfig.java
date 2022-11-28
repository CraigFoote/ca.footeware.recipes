/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
package ca.footeware.recipes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * Specify the URLs to which the current user can access.
 *
 * @author Footeware.ca
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

	/**
	 * Configures HTTP security.
	 *
	 * @param http {@link HttpSecurity}
	 * @return {@link SecurityFilterChain}
	 * @throws Exception when the internet falls over.
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf()
			.disable()
			.authorizeHttpRequests()
			.requestMatchers("/styles/**", "/js/**", "/images/**", "/fonts/**")
			.permitAll()
			.requestMatchers("/", "/login*", "error", "/search", "/browse", "/recipes/**", "/tags/**")
			.hasRole("USER")
			.requestMatchers("/", "/login*", "error", "/search", "/browse", "/recipes/**", "/tags/**", 
					"/add", "/create", "/uploadImage", "/edit/**", "/delete/**")
			.hasRole("ADMIN")
			.anyRequest()
			.authenticated()
			.and()
			.formLogin()
			.loginPage("/login")
			.permitAll()
			.defaultSuccessUrl("/", true)
			.failureUrl("/login?error=true");
		return http.build();
	}

	/**
	 * Use password encoding.
	 *
	 * @return {@link PasswordEncoder}
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	/**
	 * Username and password and roles.
	 *
	 * @return {@link InMemoryUserDetailsManager}
	 */
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		UserDetails user = User.withUsername("foote").password(passwordEncoder().encode("Bogart1997")).roles("USER")
				.build();
		UserDetails admin = User.withUsername("admin").password(passwordEncoder().encode("admin234"))
				.roles("USER", "ADMIN").build();
		return new InMemoryUserDetailsManager(user, admin);
	}

	/**
	 * Specify resources to allow without credentials.
	 *
	 * @return {@link WebSecurityCustomizer}
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedPercent(true);
		return web -> web.httpFirewall(firewall);
	}
}