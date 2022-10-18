package json.jsonb;

import json.jsonb.model.SimpleAnnotatedSerializedArrayContainer;
import json.jsonb.model.SimpleContainer;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.spi.JsonbProvider;
import java.util.ArrayList;

public class JsonBMain {

    public static void main(String[] args) throws Exception {

        testJsonbTypeDeserializer();

    }

    private static void testJsonbTypeDeserializer() throws Exception {
        ArrayList<SimpleContainer> simpleContainers = createSimpleContainers();

        SimpleAnnotatedSerializedArrayContainer arrayContainer = new SimpleAnnotatedSerializedArrayContainer();
        arrayContainer.setListInstance(simpleContainers);

        try (Jsonb jsonb = createJsonb();) {
            String toJson = jsonb.toJson(arrayContainer);

            SimpleAnnotatedSerializedArrayContainer fromJson = jsonb.fromJson(toJson, SimpleAnnotatedSerializedArrayContainer.class);
            System.out.println(fromJson.getListInstance());
        }
    }

    private static ArrayList<SimpleContainer> createSimpleContainers() {
        ArrayList<SimpleContainer> simpleContainers = new ArrayList<>();

        SimpleContainer simpleContainer1 = new SimpleContainer();
        simpleContainer1.setName("name-1");
        simpleContainer1.setAddress("address-1");
        simpleContainers.add(simpleContainer1);

        SimpleContainer simpleContainer2 = new SimpleContainer();
        simpleContainer2.setName("name-2");
        simpleContainer2.setAddress("address-2");
        simpleContainers.add(simpleContainer2);

        return simpleContainers;
    }

    private static Jsonb createJsonb() {
        JsonbProvider provider = JsonbProvider.provider();

        JsonbBuilder jsonbBuilder = provider.create();
        Jsonb jsonb = jsonbBuilder.build();
        return jsonb;
    }

}
