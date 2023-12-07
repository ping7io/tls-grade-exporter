package io.ping7.tlsgradeexporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TlsGradeExporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TlsGradeExporterApplication.class, args);
	}

}
