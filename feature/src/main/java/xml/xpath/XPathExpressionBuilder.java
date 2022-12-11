package xml.xpath;

public class XPathExpressionBuilder {

    private final StringBuilder stringBuilder = new StringBuilder();

    public XPathExpressionBuilder() {
    }

    public XPathExpressionBuilder(String base) {
        stringBuilder.append(base);
    }

    public XPathExpressionBuilder withElementName(String elementName) {
        stringBuilder.append("/")
                .append(elementName);
        return this;
    }

    public XPathExpressionBuilder withElementNameOperationValue(String elementName, String operation, String elementValue) {
        stringBuilder.append("[")
                .append(elementName)
                .append(operation)
                .append("'")
                .append(elementValue)
                .append("'")
                .append("]");
        return this;
    }

    public XPathExpressionBuilder withAttributeName(String attributeName) {
        stringBuilder.append("[@")
                .append(attributeName)
                .append("]");
        return this;
    }

    public XPathExpressionBuilder withAttributeNameOperationValue(String attributeName, String operation, String attributeValue) {
        stringBuilder.append("[@")
                .append(attributeName)
                .append(operation)
                .append("'")
                .append(attributeValue)
                .append("'")
                .append("]");
        return this;
    }

    public XPathExpressionBuilder withFunctionForCurrentExpression(String functionName) {
        stringBuilder.insert(0, "(")
                .insert(0, functionName)
                .append(")");
        return this;
    }

    public String build() {
        return stringBuilder.toString();
    }

}