package net.dontdrinkandroot.fixtures.loader

import net.dontdrinkandroot.fixtures.Fixture
import net.dontdrinkandroot.fixtures.dependencyresolution.DirectedGraph
import net.dontdrinkandroot.fixtures.dependencyresolution.getTopologicalOrder
import net.dontdrinkandroot.fixtures.purger.DatabasePurger
import net.dontdrinkandroot.fixtures.purger.NoopDatabasePurger
import net.dontdrinkandroot.fixtures.referencerepository.MapBackedReferenceRepository
import net.dontdrinkandroot.fixtures.referencerepository.ReferenceRepository
import org.slf4j.LoggerFactory
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.reflect.KClass

/**
 * A [FixtureLoader] that is based on an [EntityManager] and executes a [DatabasePurger] before
 * loading the fixtures.
 */
open class DefaultFixtureLoader(private val databasePurger: DatabasePurger = NoopDatabasePurger()) : FixtureLoader {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PersistenceContext
    lateinit var entityManager: EntityManager

    override fun load(fixtureClasses: Collection<KClass<out Fixture>>): ReferenceRepository {
        val referenceRepository: ReferenceRepository = MapBackedReferenceRepository()
        databasePurger.purge()
        val orderedFixtures: List<Fixture> = try {
            getOrderedFixtures(fixtureClasses)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        }
        for (fixture in orderedFixtures) {
            logger.info("Loading fixture " + fixture.javaClass.canonicalName)
            fixture.load(entityManager, referenceRepository)
            entityManager.flush()
        }
        return referenceRepository
    }

    @Throws(IllegalAccessException::class, InstantiationException::class)
    private fun getOrderedFixtures(fixtureClasses: Collection<KClass<out Fixture>>): List<Fixture> {
        val instantiatedFixtures: MutableMap<KClass<out Fixture>, Fixture> = HashMap()
        val fixtureGraph = DirectedGraph<Fixture>()
        for (fixtureClass in fixtureClasses) {
            addFixtureClass(fixtureClass, fixtureGraph, instantiatedFixtures)
        }
        return fixtureGraph.getTopologicalOrder()
    }

    @Throws(IllegalAccessException::class, InstantiationException::class)
    private fun addFixtureClass(
        fixtureClass: KClass<out Fixture>,
        fixtureGraph: DirectedGraph<Fixture>,
        instantiatedFixtures: MutableMap<KClass<out Fixture>, Fixture>
    ): Fixture {
        var fixture = instantiatedFixtures[fixtureClass]
        if (null == fixture) {
            fixture = instantiateFixtureClass(fixtureClass)
            fixtureGraph.addVertex(fixture)
            instantiatedFixtures[fixtureClass] = fixture
            for (dependingFixtureClass in fixture.dependencies) {
                val dependingFixture = addFixtureClass(dependingFixtureClass, fixtureGraph, instantiatedFixtures)
                fixtureGraph.addEdge(dependingFixture, fixture)
            }
        }
        return fixture
    }

    @Throws(InstantiationException::class, IllegalAccessException::class)
    protected open fun instantiateFixtureClass(fixtureClass: KClass<out Fixture>): Fixture {
        return fixtureClass.java.newInstance()
    }
}