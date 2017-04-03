package net.dontdrinkandroot.fixtures;

import java.util.Collection;

public interface FixtureLoader
{
    void load(Collection<Class<? extends Fixture>> fixtureClasses);

    ReferenceRepository getReferenceRepository();
}
