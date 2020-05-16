package bmworks.dogediary.crudframework;

import bmworks.dogediary.model.Model;
import bmworks.dogediary.persistence.AbstractEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@RestController
public class MetadataController {

    private ApplicationContext applicationContext;

    public MetadataController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(path = "/{modelName}", method = RequestMethod.POST)
    public Object createModel(@PathVariable("modelName") String modelName,
                              @RequestBody FormData formData) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<Model<? extends AbstractEntity>> modelClass = (Class<Model<? extends AbstractEntity>>) Class.forName("bmworks.dogediary.model." + modelName);
        List<Field> allFields = getAllFieldsFromModel(modelClass);
        Model<? extends AbstractEntity> model = modelClass.getConstructor().newInstance();
        for (var field : allFields) {
            String formFieldValue = formData.formField.stream()
                    .filter(formField -> formField.id.equals(field.getName()))
                    .findAny()
                    .map(formField -> formField.value)
                    .orElse("");

            Object convertedValue = null;
            if (field.getType().equals(String.class))
                convertedValue = formFieldValue;
            else if (field.getType().equals(UUID.class))
                convertedValue = UUID.fromString(formFieldValue);

            field.set(model, convertedValue);
        }

        AbstractEntity databaseObject = model.toEntity();
        String simpleName = databaseObject.getClass().getSimpleName();
        simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        CrudRepository<AbstractEntity, UUID> repository = (CrudRepository<AbstractEntity, UUID>) applicationContext.getBean(simpleName + "Repository");
        repository.save(databaseObject);

        return StreamSupport.stream(repository.findAll().spliterator(), false).collect(toList());
    }

    @RequestMapping(path = "/{modelName}/metadata", method = RequestMethod.GET)
    public ModelMetadata getModelMetadata(@PathVariable("modelName") String modelName) throws ClassNotFoundException {

        List<Field> allFields = getAllFieldsFromModel(Class.forName("bmworks.dogediary.model." + modelName));

        var metadata = new ModelMetadata();
        metadata.name = modelName;
        metadata.fields = buildFieldMetadata(allFields);
        return metadata;
    }

    private List<Field> getAllFieldsFromModel(Class<?> modelClass) {
        List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = modelClass;
        while (!currentClass.equals(Object.class)) {
            allFields.addAll(List.of(modelClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    private List<FieldMetadata> buildFieldMetadata(List<Field> fields) {
        var metadata = new ArrayList<FieldMetadata>();

        for (var field : fields) {
            FieldMetadata fieldMetadata = new FieldMetadata();

            fieldMetadata.id = field.getName();
            fieldMetadata.labelText = transformToText(field.getName());
            if (field.getType().equals(String.class))
                fieldMetadata.dataType = new StringMetadata();
            else if (field.getType().equals(UUID.class))
                fieldMetadata.dataType = new StringMetadata();

            metadata.add(fieldMetadata);
        }


        return metadata;
    }

    private String transformToText(String name) {
        return StringUtils.capitalize(name);
    }

}
