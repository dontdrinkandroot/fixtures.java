package net.dontdrinkandroot.fixtures;

import net.dontdrinkandroot.fixtures.example.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class DefaultFixtureLoaderTest
{
    @Test
    public void testTransitiveLoading()
    {
        DefaultFixtureLoader defaultFixtureLoader = new DefaultFixtureLoader(new NoopDatabasePurger());
        defaultFixtureLoader.setEntityManager(new MockEntityManager());
        ReferenceRepository referenceRepository =
                defaultFixtureLoader.load(Collections.singleton(ExampleFixtureOne.class));

        Assert.assertNotNull(referenceRepository.getReference(
                ExampleFixtureOne.class.getCanonicalName(),
                Boolean.class
        ));
        Assert.assertNotNull(referenceRepository.getReference(
                ExampleFixtureTwo.class.getCanonicalName(),
                Boolean.class
        ));
        Assert.assertNotNull(referenceRepository.getReference(
                ExampleFixtureThree.class.getCanonicalName(),
                Boolean.class
        ));

        try {
            Assert.assertNotNull(referenceRepository.getReference(
                    ExampleFixtureFour.class.getCanonicalName(),
                    Boolean.class
            ));
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            /* Expected */
        }

        try {
            Assert.assertNotNull(referenceRepository.getReference(
                    ExampleFixtureFive.class.getCanonicalName(),
                    Boolean.class
            ));
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            /* Expected */
        }
    }

    @Test
    public void testCycle()
    {
        try {
            DefaultFixtureLoader defaultFixtureLoader = new DefaultFixtureLoader(new NoopDatabasePurger());
            defaultFixtureLoader.setEntityManager(new MockEntityManager());
            defaultFixtureLoader.load(Collections.singleton(ExampleFixtureFour.class));
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().startsWith("Graph has at least one cycle:"));
        }
    }
}
