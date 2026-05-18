package co.edu.univalle.NiceLook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NiceLookApplication {

	public static void main(String[] args) {
		SpringApplication.run(NiceLookApplication.class, args);
	}

}
