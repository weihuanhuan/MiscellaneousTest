package json.jsonb.serializer;

import json.jsonb.model.SimpleContainer;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SimpleContainerArrayDeserializer implements JsonbDeserializer<List<SimpleContainer>> {

    private final JsonbDeserializer<SimpleContainer> simpleContainerDeserializer = new SimpleContainerDeserializer();

    public List<SimpleContainer> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {
        List<SimpleContainer> containers = new ArrayList<>();

        while (jsonParser.hasNext()) {
            JsonParser.Event event = jsonParser.next();
            System.out.println(this.getClass().getSimpleName() + ":" + event);

            if (event == JsonParser.Event.START_OBJECT) {
                if (!SimpleContainerSerializerUtil.isUsingJsonbDeserializer()) {
                    containers.add(deserializationContext.deserialize(SimpleContainer.class, jsonParser));
                } else {
                    containers.add(simpleContainerDeserializer.deserialize(jsonParser, deserializationContext, SimpleContainer.class));
                }
            }

            if (event == JsonParser.Event.END_OBJECT) {
                break;
            }
        }

        return containers;
    }

}
