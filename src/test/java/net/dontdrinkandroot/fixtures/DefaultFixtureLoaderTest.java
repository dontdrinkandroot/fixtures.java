package net.dontdrinkandroot.fixtures;

import net.dontdrinkandroot.fixtures.example.*;
import net.dontdrinkandroot.fixtures.loader.DefaultFixtureLoader;
import net.dontdrinkandroot.fixtures.purger.NoopDatabasePurger;
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
        defaultFixtureLoader.setEntityManager(new NoopEntityManager());
        ReferenceRepository referenceRepository =
                defaultFixtureLoader.load(Collections.singleton(ExampleFixtureOne.class));

        Assert.assertNotNull(referenceRepository.retrieve(
                ExampleFixtureOne.class.getCanonicalName()
        ));
        Assert.assertNotNull(referenceRepository.retrieve(
                ExampleFixtureTwo.class.getCanonicalName()
        ));
        Assert.assertNotNull(referenceRepository.retrieve(
                ExampleFixtureThree.class.getCanonicalName()
        ));

        try {
            Assert.assertNotNull(referenceRepository.retrieve(
                    ExampleFixtureFour.class.getCanonicalName()
            ));
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            /* Expected */
        }

        try {
            Assert.assertNotNull(referenceRepository.retrieve(
                    ExampleFixtureFive.class.getCanonicalName()
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
            defaultFixtureLoader.setEntityManager(new NoopEntityManager());
            defaultFixtureLoader.load(Collections.singleton(ExampleFixtureFour.class));
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().startsWith("Graph has at least one cycle:"));
        }
    }
}
