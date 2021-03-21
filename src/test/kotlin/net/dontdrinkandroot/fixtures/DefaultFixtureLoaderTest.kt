package net.dontdrinkandroot.fixtures

import net.dontdrinkandroot.fixtures.example.*
import net.dontdrinkandroot.fixtures.loader.DefaultFixtureLoader
import net.dontdrinkandroot.fixtures.purger.NoopDatabasePurger
import org.junit.Assert
import org.junit.Test

class DefaultFixtureLoaderTest {

    @Test
    fun testTransitiveLoading() {
        val defaultFixtureLoader = DefaultFixtureLoader(NoopDatabasePurger())
        defaultFixtureLoader.entityManager = NoopEntityManager()
        val referenceRepository = defaultFixtureLoader.load(setOf(ExampleFixtureOne::class))
        Assert.assertNotNull(referenceRepository.retrieve(ExampleFixtureOne::class))
        Assert.assertNotNull(referenceRepository.retrieve(ExampleFixtureTwo::class))
        Assert.assertNotNull(referenceRepository.retrieve(ExampleFixtureThree::class))
        try {
            Assert.assertNotNull(referenceRepository.retrieve(ExampleFixtureFour::class))
            Assert.fail("Exception expected")
        } catch (e: RuntimeException) {
            /* Expected */
        }
        try {
            Assert.assertNotNull(referenceRepository.retrieve(ExampleFixtureFive::class))
            Assert.fail("Exception expected")
        } catch (e: RuntimeException) {
            /* Expected */
        }
    }

    @Test
    fun testCycle() {
        try {
            val defaultFixtureLoader = DefaultFixtureLoader(NoopDatabasePurger())
            defaultFixtureLoader.entityManager = NoopEntityManager()
            defaultFixtureLoader.load(setOf(ExampleFixtureFour::class))
            Assert.fail("Exception expected")
        } catch (e: RuntimeException) {
            Assert.assertTrue(e.message!!.startsWith("Graph has at least one cycle:"))
        }
    }
}