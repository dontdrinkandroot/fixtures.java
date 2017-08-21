package net.dontdrinkandroot.fixtures.example;

import net.dontdrinkandroot.fixtures.Fixture;
import net.dontdrinkandroot.fixtures.referencerepository.ReferenceRepository;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class ExampleFixtureOne implements Fixture
{
    @Override
    public Collection<Class<? extends Fixture>> getDependencies()
    {
        return Collections.singleton(ExampleFixtureTwo.class);
    }

    @Override
    public void load(EntityManager entityManager, ReferenceRepository referenceRepository)
    {
        referenceRepository.store(this.getClass().getCanonicalName(), true);
    }
}
