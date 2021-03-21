package net.dontdrinkandroot.fixtures.referencerepository

import kotlin.reflect.KClass

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
     * Shortcut for using a kClass as name.
     */
    fun <T : Any> store(kClass: KClass<*>, obj: T) = store(kClass.qualifiedName!!, obj)

    /**
     * Retrieves an already stored object.
     *
     * @param name The lookup name.
     * @param <T>  Type of the object.
     * @return The object.
     * @throws RuntimeException Thrown if no object can be found under the given name.
     */
    fun <T : Any> retrieve(name: String): T

    /**
     * Shortcut for using a kClass as name.
     */
    fun <T : Any> retrieve(kClass: KClass<*>): T = this.retrieve(kClass.qualifiedName!!)
}