package spring.json.requestbody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

// 对于序列化时，不包含对 null 域的序列化, class 级别
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestBodyObjectBean {

    private String fieldWithNull;

    private String fieldWithBlank = "";

    // defaultValue 当前只是元数据，并不会设置为默认值。
    // 需要反序列化的默认值时可以简单的设置为域的初始化值就可以了。
    @JsonProperty(defaultValue = "default-value-from-json-property")
    private String fieldJsonPropertyWithDefault = "default-value-from-field";

    // 对于序列化时，不包含对 null 域的序列化, field 级别
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fieldJsonIncludeWithNull;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fieldJsonIncludeWithDefault = "default-value-from-field";

    // 对于反序列化是，对于 json null 值不进行 set 调用，这可以保留该域原始的默认值。
    @JsonSetter(nulls = Nulls.SKIP)
    private String fieldJsonSetterWithDefault = "default-value-from-field";

    public String getFieldWithNull() {
        return fieldWithNull;
    }

    public void setFieldWithNull(String fieldWithNull) {
        this.fieldWithNull = fieldWithNull;
    }

    public String getFieldWithBlank() {
        return fieldWithBlank;
    }

    public void setFieldWithBlank(String fieldWithBlank) {
        this.fieldWithBlank = fieldWithBlank;
    }

    public String getFieldJsonPropertyWithDefault() {
        return fieldJsonPropertyWithDefault;
    }

    public void setFieldJsonPropertyWithDefault(String fieldJsonPropertyWithDefault) {
        this.fieldJsonPropertyWithDefault = fieldJsonPropertyWithDefault;
    }

    public String getFieldJsonIncludeWithNull() {
        return fieldJsonIncludeWithNull;
    }

    public void setFieldJsonIncludeWithNull(String fieldJsonIncludeWithNull) {
        this.fieldJsonIncludeWithNull = fieldJsonIncludeWithNull;
    }

    public String getFieldJsonIncludeWithDefault() {
        return fieldJsonIncludeWithDefault;
    }

    public void setFieldJsonIncludeWithDefault(String fieldJsonIncludeWithDefault) {
        this.fieldJsonIncludeWithDefault = fieldJsonIncludeWithDefault;
    }

    public String getFieldJsonSetterWithDefault() {
        return fieldJsonSetterWithDefault;
    }

    public void setFieldJsonSetterWithDefault(String fieldJsonSetterWithDefault) {
        this.fieldJsonSetterWithDefault = fieldJsonSetterWithDefault;
    }

    @Override
    public String toString() {
        return "RequestBodyObjectBean{" +
                "fieldWithNull='" + fieldWithNull + '\'' +
                ", fieldWithBlank='" + fieldWithBlank + '\'' +
                ", fieldJsonPropertyWithDefault='" + fieldJsonPropertyWithDefault + '\'' +
                ", fieldJsonIncludeWithNull='" + fieldJsonIncludeWithNull + '\'' +
                ", fieldJsonIncludeWithDefault='" + fieldJsonIncludeWithDefault + '\'' +
                ", fieldJsonSetterWithDefault='" + fieldJsonSetterWithDefault + '\'' +
                '}';
    }
}
