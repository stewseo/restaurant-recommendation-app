package org.example.elasticsearch.client.util;

public class MissingRequiredPropertyException extends RuntimeException {
    private final Class<?> clazz;
    private final String property;

    public MissingRequiredPropertyException(Object obj, String property) {
        super("Missing required property '" + obj.getClass().getSimpleName() + "." + property + "'");
        this.clazz = obj.getClass();
        this.property = property;
    }

    /**
     * The class where the missing property was found
     */
    public Class<?> getObjectClass() {
        return clazz;
    }

    public String getPropertyName() {
        return property;
    }
}
