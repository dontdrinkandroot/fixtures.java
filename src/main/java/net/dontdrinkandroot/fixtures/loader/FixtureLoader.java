package net.dontdrinkandroot.fixtures.loader;

import net.dontdrinkandroot.fixtures.Fixture;
import net.dontdrinkandroot.fixtures.ReferenceRepository;

import java.util.Collection;

/**
 * A {@link FixtureLoader} loads a given set of fixtures into the database.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface FixtureLoader
{
    /**
     * Loads the given fixtures.
     *
     * @param fixtureClasses The fixtures to load.
     * @return The populated {@link ReferenceRepository}
     */
    ReferenceRepository load(Collection<Class<? extends Fixture>> fixtureClasses);
}
