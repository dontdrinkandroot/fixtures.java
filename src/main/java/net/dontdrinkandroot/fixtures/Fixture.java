package net.dontdrinkandroot.fixtures;

import javax.persistence.EntityManager;
import java.util.Collection;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface Fixture
{
    Collection<Class<? extends Fixture>> getDependencies();

    void load(EntityManager entityManager, ReferenceRepository referenceRepository);
}
