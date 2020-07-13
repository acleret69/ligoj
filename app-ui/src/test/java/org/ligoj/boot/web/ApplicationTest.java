/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.boot.web;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.util.StringUtils;

/**
 * Test class of {@link Application}
 */
class ApplicationTest {

	private static String oldCp = System.getProperty("java.class.path");

	@AfterAll
	static void resetTestEnv() {
		System.clearProperty("app-env");
		System.clearProperty("security.pre-auth-principal");
		System.clearProperty("security.pre-auth-credentials");
		System.clearProperty("security.pre-auth-logout");
		if (oldCp == null) {
			System.clearProperty("java.class.path");
		} else {
			System.setProperty("java.class.path", oldCp);
		}
	}

	@Test
	void configClassContext() throws Exception {
		Application.main(getArgs(getClass().getName()));
		SpringApplication.exit(Application.lastContext);
	}

	@Test
	void configure() throws Exception {
		new Application().configure(Mockito.mock(SpringApplicationBuilder.class));
		new Application().containerCustomizer().registerErrorPages(Mockito.mock(ErrorPageRegistry.class));
	}

	@Test
	void configurePreAuthHeader() throws Exception {
		System.setProperty("security.pre-auth-principal", "HEADER");
		System.setProperty("security.pre-auth-credentials", "CREDS");
		Application.main(getArgs(getClass().getName()));
		SpringApplication.exit(Application.lastContext);
	}

	@Test
	void configurePreAuthLogout() throws Exception {
		System.setProperty("security.pre-auth-principal", "HEADER");
		System.setProperty("security.pre-auth-credentials", "CREDS");
		System.setProperty("security.pre-auth-logout", "https://signin.sample.com");
		Application.main(getArgs(getClass().getName()));
		SpringApplication.exit(Application.lastContext);
	}

	@Test
	void getEnvironment() {
		final var application = new Application();
		application.environmentCode = "auto";
		Assertions.assertEquals("", application.getEnvironment());
		application.environmentCode = "any";
		Assertions.assertEquals("any", application.getEnvironment());
		application.environmentCode = "auto";
		System.setProperty("java.class.path", "any.war");
		Assertions.assertEquals("-prod", application.getEnvironment());
	}

	protected String[] getArgs(String... args) {
		return new String[] { "--spring.main.webEnvironment=false", "--spring.main.showBanner=OFF", "--spring.main.registerShutdownHook=false",
				"--spring.main.sources=" + StringUtils.arrayToCommaDelimitedString(args) };
	}

}
