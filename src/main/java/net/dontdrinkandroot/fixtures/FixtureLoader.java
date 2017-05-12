package net.dontdrinkandroot.fixtures;

import java.util.Collection;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface FixtureLoader
{
    ReferenceRepository load(Collection<Class<? extends Fixture>> fixtureClasses);
}
