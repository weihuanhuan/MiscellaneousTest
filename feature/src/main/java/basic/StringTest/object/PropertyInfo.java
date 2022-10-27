package basic.StringTest.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyInfo extends Properties {
    PropertyInfo parent;

    List<PropertyInfo> children = new ArrayList<PropertyInfo>();

    String v_name = "name";

    String v_type = "type";

    String v_isBinary = "isbinary";

    String v_isArray = "isarray";

    String v_isPrimitive = "isprimitive";

    String v_isGeneric = "clazz/generic";

    String v_modifier = "modifier";

    public PropertyInfo getParent() {
        return this.parent;
    }

    public void set(String typeName, String variableName, boolean isBinary, boolean isArray, boolean isPrimitive, int modifier) {
        setType(typeName);
        setBinary(isBinary);
        setArray(isArray);
        setPrimitive(isPrimitive);
        setName(variableName);
        setModifier(modifier);
    }

    public PropertyInfo addChild() {
        if (this.children == null)
            this.children = new ArrayList<PropertyInfo>();
        PropertyInfo info = new PropertyInfo();
        this.children.add(info);
        return info;
    }

    public void setModifier(int modifier) {
        if (modifier > 0)
            put(this.v_modifier, Integer.valueOf(modifier));
    }

    public int getModifier() {
        return (get(this.v_modifier) == null) ? 0 : ((Integer) get(this.v_modifier)).intValue();
    }

    public void setParent(PropertyInfo parent) {
        this.parent = parent;
    }

    public List<PropertyInfo> getChildren() {
        return this.children;
    }

    public void setChildren(List<PropertyInfo> children) {
        this.children = children;
    }

    public boolean isGeneric() {
        return (get(this.v_isGeneric) == null) ? false : ((Boolean) get(this.v_isGeneric)).booleanValue();
    }

    public void setGeneric(boolean generic) {
        if (generic)
            put(this.v_isGeneric, Boolean.valueOf(generic));
    }

    public String getName() {
        return (String) get(this.v_name);
    }

    public void setName(String name) {
        put(this.v_name, name);
    }

    public String getType() {
        return (String) get(this.v_type);
    }

    public void setType(String typeName) {
        put(this.v_type, typeName);
    }

    public boolean isBinary() {
        return (get(this.v_isBinary) == null) ? false : ((Boolean) get(this.v_isBinary)).booleanValue();
    }

    public void setBinary(boolean binary) {
        if (binary)
            put(this.v_isBinary, Boolean.valueOf(binary));
    }

    public boolean isArray() {
        return (get(this.v_isArray) == null) ? false : ((Boolean) get(this.v_isArray)).booleanValue();
    }

    public void setArray(boolean array) {
        if (array)
            put(this.v_isArray, Boolean.valueOf(array));
    }

    public boolean isPrimitive() {
        return (get(this.v_isPrimitive) == null) ? false : ((Boolean) get(this.v_isPrimitive)).booleanValue();
    }

    public void setPrimitive(boolean primitive) {
        if (primitive)
            put(this.v_isPrimitive, Boolean.valueOf(primitive));
    }
}
