package xml.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Map;

public class JaxbMarshal {

    public static void main(String[] args) throws JAXBException {
        // 实例化Java对象
        Person person = new Person();
        person.setName("Tom");
        person.setAge(25);
        Map<String, String> properties = person.getProperties();
        properties.put("address", "china");
        properties.put("career", "programmer");

        // 将Java对象转换为XML文档
        JAXBContext jaxbContext = JAXBContext.newInstance(Person.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(person, stringWriter);
        String xml = stringWriter.toString();

        // 输出XML文档
        System.out.println(xml);
    }
}
