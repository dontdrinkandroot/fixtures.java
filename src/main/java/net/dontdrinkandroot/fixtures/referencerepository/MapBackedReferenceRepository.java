package net.dontdrinkandroot.fixtures.referencerepository;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ReferenceRepository} that uses a {@link HashMap} as storage.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class MapBackedReferenceRepository implements ReferenceRepository
{
    private final Map<String, Object> objects = new HashMap<>();

    @Override
    public <T> void store(String name, T object)
    {
        this.objects.put(name, object);
    }

    @Override
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
