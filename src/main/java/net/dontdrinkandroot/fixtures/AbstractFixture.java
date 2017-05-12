package net.dontdrinkandroot.fixtures;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
abstract public class AbstractFixture implements Fixture
{
    @Override
    public Collection<Class<? extends Fixture>> getDependencies()
    {
        return new ArrayList<>();
    }
}
