package net.dontdrinkandroot.fixtures.loader

import net.dontdrinkandroot.fixtures.Fixture
import net.dontdrinkandroot.fixtures.referencerepository.ReferenceRepository

/**
 * A [FixtureLoader] loads a given set of fixtures into the database.
 */
interface FixtureLoader {

    /**
     * Loads the given fixtures.
     *
     * @param fixtureClasses The fixtures to load.
     * @return The populated [ReferenceRepository]
     */
    fun load(fixtureClasses: Collection<Class<out Fixture>>): ReferenceRepository
}