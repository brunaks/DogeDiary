package bmworks.dogediary.model;

import bmworks.dogediary.persistence.Pet;

import java.util.UUID;

public class PetModel extends Model<Pet> {
    public UUID id;
    public String name;

    public Pet toEntity() {
        return new Pet(id, name);
    }
}
