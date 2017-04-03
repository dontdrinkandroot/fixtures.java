package net.dontdrinkandroot.fixtures;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class ReferenceRepositoryTest
{
    @Test(expected = RuntimeException.class)
    public void nullReferenceThrowsException()
    {
        ReferenceRepository referenceRepository = new ReferenceRepository();
        referenceRepository.getReference("asdf", String.class);
    }

    @Test
    public void canStoreAndRetrieve()
    {
        ReferenceRepository referenceRepository = new ReferenceRepository();
        referenceRepository.addReference("test", "asdf");
        Assert.assertEquals("asdf", referenceRepository.getReference("test", String.class));
    }
}
