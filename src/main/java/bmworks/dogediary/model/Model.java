package bmworks.dogediary.model;

import bmworks.dogediary.persistence.AbstractEntity;

public abstract class Model<T extends AbstractEntity> {

    public abstract T toEntity();
}
