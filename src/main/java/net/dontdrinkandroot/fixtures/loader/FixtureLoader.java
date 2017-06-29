package net.dontdrinkandroot.fixtures.loader;

import net.dontdrinkandroot.fixtures.Fixture;
import net.dontdrinkandroot.fixtures.ReferenceRepository;

import java.util.Collection;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface FixtureLoader
{
    ReferenceRepository load(Collection<Class<? extends Fixture>> fixtureClasses);
}
