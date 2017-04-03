package net.dontdrinkandroot.fixtures;

import java.util.HashMap;
import java.util.Map;

public class ReferenceRepository
{
    private Map<String, Object> references = new HashMap<>();

    public void addReference(String name, Object reference)
    {
        this.references.put(name, reference);
    }

    public <T> T getReference(String name, Class<T> clazz)
    {
        @SuppressWarnings("unchecked")
        T reference = (T) this.references.get(name);
        if (null == reference) {
            throw new RuntimeException(String.format("No reference found with name '%s'", name));
        }

        return reference;
    }
}
