package bmworks.dogediary;

import bmworks.dogediary.persistence.Pet;
import bmworks.dogediary.persistence.PetRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import java.util.UUID;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted(ApplicationStartedEvent applicationStartedEvent) {
        PetRepository bean = applicationStartedEvent.getApplicationContext().getBean(PetRepository.class);
        bean.save(new Pet(UUID.randomUUID(), "Pacc"));
        bean.findAll().forEach(pet -> System.out.println(pet.name));
    }
}
