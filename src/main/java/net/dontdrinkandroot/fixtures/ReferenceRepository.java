package net.dontdrinkandroot.fixtures;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows you to store references to already created objects and retrieve them in other fixtures.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class ReferenceRepository
{
    private final Map<String, Object> objects = new HashMap<>();

    /**
     * Stores an object.
     *
     * @param name   The lookup name.
     * @param object The object.
     * @param <T>    Type of the object.
     */
    public <T> void store(String name, T object)
    {
        this.objects.put(name, object);
    }

    /**
     * Retrieves an already stored object.
     *
     * @param name The lookup name.
     * @param <T>  Type of the object.
     * @return The object.
     * @throws RuntimeException Thrown if no object can be found under the given name.
     */
    public <T> T retrieve(String name)
    {
        @SuppressWarnings("unchecked")
        T reference = (T) this.objects.get(name);
        if (null == reference) {
            throw new RuntimeException(String.format("No reference found with name '%s'", name));
        }

        return reference;
    }
}
