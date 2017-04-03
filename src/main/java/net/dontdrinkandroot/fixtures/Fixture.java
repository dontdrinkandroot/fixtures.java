package net.dontdrinkandroot.fixtures;

import javax.persistence.EntityManager;
import java.util.Collection;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface Fixture
{
    public Collection<Class<? extends Fixture>> getDependencies();

    public void load(EntityManager entityManager, ReferenceRepository referenceRepository);
}
