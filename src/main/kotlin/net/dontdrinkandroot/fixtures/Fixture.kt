package net.dontdrinkandroot.fixtures

import net.dontdrinkandroot.fixtures.referencerepository.ReferenceRepository
import javax.persistence.EntityManager

/**
 * A [Fixture] is responsible for storing one or many objects in the database.
 */
interface Fixture {

    /**
     * Specifies fixture classes that need to be loaded before this fixture.
     *
     * @return The collection of fixture classes to load before this fixture.
     */
    val dependencies: Collection<Class<out Fixture>>
        get() = emptyList()

    /**
     * Loads this fixture into the database via the [EntityManager]. Can use the [ReferenceRepository] to
     * retrieve objects needed and to store the objects created.
     *
     * @param entityManager       The EntityManager to interact with the database.
     * @param referenceRepository The Repository to store or retrieve other fixture objects.
     */
    fun load(entityManager: EntityManager, referenceRepository: ReferenceRepository)
}