package bmworks.dogediary.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Pet extends AbstractEntity {

    public String name;

    public Pet() {

    }

    public Pet(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
