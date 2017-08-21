package net.dontdrinkandroot.fixtures;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class ReferenceRepositoryTest
{
    @Test
    public void canStoreAndRetrieve()
    {
        ReferenceRepository referenceRepository = new ReferenceRepository();
        referenceRepository.store("test", "asdf");
        Assert.assertEquals("asdf", referenceRepository.retrieve("test"));
    }

    @Test(expected = RuntimeException.class)
    public void nullReferenceThrowsException()
    {
        ReferenceRepository referenceRepository = new ReferenceRepository();
        referenceRepository.retrieve("asdf");
    }

    @Test(expected = ClassCastException.class)
    public void wrongClassCastThrowsException()
    {
        ReferenceRepository referenceRepository = new ReferenceRepository();
        referenceRepository.store("test", "asdf");
        Integer foo = referenceRepository.retrieve("test");
    }
}
