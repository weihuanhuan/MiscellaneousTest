package json.jsonb.serializer;

import json.jsonb.model.SimpleContainer;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.util.List;

public class SimpleContainerArraySerializer implements JsonbSerializer<List<SimpleContainer>> {

    private final JsonbSerializer<SimpleContainer> simpleContainerSerializer = new SimpleContainerSerializer();

    @Override
    public void serialize(List<SimpleContainer> containers, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
        System.out.println("jsonGenerator.getClass():" + jsonGenerator.getClass());
        System.out.println("serializationContext.getClass():" + serializationContext.getClass());

        jsonGenerator.writeStartArray();

        for (SimpleContainer container : containers) {
            if (!SimpleContainerSerializerUtil.isUsingJsonbDeserializer()) {
                serializationContext.serialize(container, jsonGenerator);
            } else {
                simpleContainerSerializer.serialize(container, jsonGenerator, serializationContext);
            }
        }

        jsonGenerator.writeEnd();
    }

}
