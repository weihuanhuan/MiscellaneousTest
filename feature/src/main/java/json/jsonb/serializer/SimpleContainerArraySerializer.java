package json.jsonb.serializer;

import json.jsonb.model.SimpleContainer;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.util.List;

public class SimpleContainerArraySerializer implements JsonbSerializer<List<SimpleContainer>> {

    @Override
    public void serialize(List<SimpleContainer> containers, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
        jsonGenerator.writeStartArray();
        for (SimpleContainer container : containers) {
            serializationContext.serialize(container, jsonGenerator);
        }
        jsonGenerator.writeEnd();
    }

}
