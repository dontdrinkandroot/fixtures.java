package net.dontdrinkandroot.fixtures.referencerepository

/**
 * Allows you to store references to already created objects and retrieve them in other fixtures.
 */
interface ReferenceRepository {

    /**
     * Stores an object.
     *
     * @param name   The lookup name.
     * @param obj The object.
     * @param <T>    Type of the object.
     */
    fun <T : Any> store(name: String, obj: T)

    /**
     * Retrieves an already stored object.
     *
     * @param name The lookup name.
     * @param <T>  Type of the object.
     * @return The object.
     * @throws RuntimeException Thrown if no object can be found under the given name.
     */
    fun <T : Any> retrieve(name: String): T
}