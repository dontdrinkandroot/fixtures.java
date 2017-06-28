package net.dontdrinkandroot.fixtures;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class ReferenceRepository
{
    private Map<String, Object> objects = new HashMap<>();

    public <T> void add(String name, T object)
    {
        this.objects.put(name, object);
    }

    public <T> T resolve(String name)
    {
        @SuppressWarnings("unchecked")
        T reference = (T) this.objects.get(name);
        if (null == reference) {
            throw new RuntimeException(String.format("No reference found with name '%s'", name));
        }

        return reference;
    }
}
