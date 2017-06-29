package net.dontdrinkandroot.fixtures;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface Fixture
{
    default Collection<Class<? extends Fixture>> getDependencies()
    {
        return Collections.emptyList();
    }

    void load(EntityManager entityManager, ReferenceRepository referenceRepository);
}
