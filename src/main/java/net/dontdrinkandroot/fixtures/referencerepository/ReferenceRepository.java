package net.dontdrinkandroot.fixtures.referencerepository;

/**
 * Allows you to store references to already created objects and retrieve them in other fixtures.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface ReferenceRepository
{
    /**
     * Stores an object.
     *
     * @param name   The lookup name.
     * @param object The object.
     * @param <T>    Type of the object.
     */
    <T> void store(String name, T object);

    /**
     * Retrieves an already stored object.
     *
     * @param name The lookup name.
     * @param <T>  Type of the object.
     * @return The object.
     * @throws RuntimeException Thrown if no object can be found under the given name.
     */
    <T> T retrieve(String name);
}
