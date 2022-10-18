package json.jsonb.model;

import json.jsonb.serializer.SimpleContainerArrayDeserializer;
import json.jsonb.serializer.SimpleContainerArraySerializer;

import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;
import java.util.List;

/**
 * reference https://github.com/eclipse-ee4j/yasson/blob/master/src/test/java/org/eclipse/yasson/serializers/model/SimpleAnnotatedSerializedArrayContainer.java)
 */
public class SimpleAnnotatedSerializedArrayContainer {

    @JsonbTypeSerializer(SimpleContainerArraySerializer.class)
    @JsonbTypeDeserializer(SimpleContainerArrayDeserializer.class)
    private List<SimpleContainer> listInstance;

    public List<SimpleContainer> getListInstance() {
        return listInstance;
    }

    public void setListInstance(List<SimpleContainer> listInstance) {
        this.listInstance = listInstance;
    }

}
