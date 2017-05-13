package net.dontdrinkandroot.fixtures.example;

import net.dontdrinkandroot.fixtures.AbstractFixture;
import net.dontdrinkandroot.fixtures.Fixture;
import net.dontdrinkandroot.fixtures.ReferenceRepository;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class ExampleFixtureOne extends AbstractFixture
{
    @Override
    public Collection<Class<? extends Fixture>> getDependencies()
    {
        return Collections.singleton(ExampleFixtureTwo.class);
    }

    @Override
    public void load(EntityManager entityManager, ReferenceRepository referenceRepository)
    {
        referenceRepository.addReference(this.getClass().getCanonicalName(), true);
    }
}
