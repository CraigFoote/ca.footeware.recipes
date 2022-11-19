/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
package ca.footeware.recipes;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * Specify the URLs to which the current user can access and those that are
 * forwarded to login.
 *
 * @author Footeware.ca
 */
@Configuration
public class WebSecurityConfig {

	/**
	 * A bean that configures HTTP security.
	 *
	 * @param http {@link HttpSecurity}
	 * @return {@link SecurityFilterChain}
	 * @throws Exception when the internet falls over.
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authz -> authz
				.antMatchers("/cookbook", "/webcam", "/gallery/Camping at Drumheller", "/gallery/Cookbook",
						"/gallery/Family", "/gallery/Karla Camping", "/gallery/Soccer", "/gallery/Youmans Camping")
				.authenticated()).httpBasic(withDefaults()).formLogin().loginPage("/login").permitAll();
		http.headers().frameOptions().disable();
		http.csrf().disable();
		return http.build();
	}

	/**
	 * Use BCrypt for password encoding.
	 *
	 * @return {@link PasswordEncoder}
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Username and password.
	 *
	 * @return {@link InMemoryUserDetailsManager}
	 */
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		UserDetails user = User.withUsername("foote").password(passwordEncoder().encode("bogie97")).roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user);
	}

	/**
	 * Specify pages to allow without credentials.
	 *
	 * @return {@link WebSecurityCustomizer}
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedPercent(true);
		return web -> web.httpFirewall(firewall).ignoring().antMatchers("/", "/gear/**", "/styles/**", "/js/**",
				"/images/**", "/fonts/**", "/gallery", "/gallery/Artsy-Fartsy/", "/gallery/Artsy-Fartsy/**",
				"/gallery/thumbnails/Artsy-Fartsy/**");
	}

}