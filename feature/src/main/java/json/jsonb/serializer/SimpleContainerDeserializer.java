package json.jsonb.serializer;

import json.jsonb.model.SimpleContainer;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

/**
 * reference com.sun.ts.tests.jsonb.customizedmapping.serializers.model.serializer.AnimalDeserializer
 */
public class SimpleContainerDeserializer implements JsonbDeserializer<SimpleContainer> {

    public SimpleContainer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {
        SimpleContainer simpleContainer = null;

        while (jsonParser.hasNext()) {
            JsonParser.Event event = jsonParser.next();
            System.out.println(this.getClass().getSimpleName() + ":" + event);

            if (event == JsonParser.Event.START_OBJECT) {
                continue;
            }

            if (event == JsonParser.Event.END_OBJECT) {
                break;
            }

            if (event == JsonParser.Event.KEY_NAME) {
                switch (jsonParser.getString()) {
                    case "name":
                        //indicate name must be first field
                        simpleContainer = new SimpleContainer();

                        //get value associated with key
                        jsonParser.next();
                        simpleContainer.setName(jsonParser.getString());
                        break;
                    case "address":
                        jsonParser.next();
                        simpleContainer.setAddress(jsonParser.getString());
                        break;
                }
            }
        }

        return simpleContainer;
    }
}
