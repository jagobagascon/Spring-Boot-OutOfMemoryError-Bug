package com.example.demo;

import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.apache.catalina.core.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public ServletContextInitializer preCompileJspsAtStartup() {
		return servletContext -> {
			getDeepResourcePaths(servletContext, "/WEB-INF/").parallel().forEach(jspPath -> {
				ServletRegistration.Dynamic reg = servletContext.addServlet(jspPath, Constants.JSP_SERVLET_CLASS);
				reg.addMapping(jspPath);
				//reg.setLoadOnStartup(99); // uncomment this to avoid the problem
			});
		};
	}

	private static Stream<String> getDeepResourcePaths(ServletContext servletContext, String path) {
		if (!path.endsWith("/")) {
			return Stream.of(path);
		}

		Set<String> resourcePaths = servletContext.getResourcePaths(path);
		if (resourcePaths != null) {
			return resourcePaths.stream().flatMap(p -> getDeepResourcePaths(servletContext, p))
					.filter(s -> s.endsWith(".jsp"));
		} else {
			return Stream.empty();
		}
	}
}
