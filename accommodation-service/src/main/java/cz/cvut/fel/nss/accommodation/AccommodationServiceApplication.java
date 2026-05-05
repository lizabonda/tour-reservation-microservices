package cz.cvut.fel.nss.accommodation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"cz.cvut.fel.nss.entity", "cz.cvut.fel.nss.accommodation"})
public class AccommodationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccommodationServiceApplication.class, args);
    }
}