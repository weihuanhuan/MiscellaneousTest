package json.jsonb.serializer;

import json.jsonb.model.SimpleContainer;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class SimpleContainerSerializer implements JsonbSerializer<SimpleContainer> {

    public void serialize(SimpleContainer simpleContainer, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
        if (simpleContainer != null) {
            jsonGenerator.writeStartObject();

            //write name as first field
            jsonGenerator.write("name", simpleContainer.getName());
            jsonGenerator.write("address", simpleContainer.getAddress());

            jsonGenerator.writeEnd();
        } else {
            serializationContext.serialize(null, jsonGenerator);
        }
    }

}
