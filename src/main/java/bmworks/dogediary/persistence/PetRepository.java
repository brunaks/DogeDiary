package bmworks.dogediary.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PetRepository extends CrudRepository<Pet, UUID> {

}
