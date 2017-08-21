package net.dontdrinkandroot.fixtures;

import net.dontdrinkandroot.fixtures.referencerepository.MapBackedReferenceRepository;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class MapBackedReferenceRepositoryTest
{
    @Test
    public void canStoreAndRetrieve()
    {
        MapBackedReferenceRepository referenceRepository = new MapBackedReferenceRepository();
        referenceRepository.store("test", "asdf");
        Assert.assertEquals("asdf", referenceRepository.retrieve("test"));
    }

    @Test(expected = RuntimeException.class)
    public void nullReferenceThrowsException()
    {
        MapBackedReferenceRepository referenceRepository = new MapBackedReferenceRepository();
        referenceRepository.retrieve("asdf");
    }

    @Test(expected = ClassCastException.class)
    public void wrongClassCastThrowsException()
    {
        MapBackedReferenceRepository referenceRepository = new MapBackedReferenceRepository();
        referenceRepository.store("test", "asdf");
        Integer foo = referenceRepository.retrieve("test");
    }
}
