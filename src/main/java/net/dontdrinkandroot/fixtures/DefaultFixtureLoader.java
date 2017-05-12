package net.dontdrinkandroot.fixtures;

import net.dontdrinkandroot.fixtures.dependencyresolution.DirectedGraph;
import net.dontdrinkandroot.fixtures.dependencyresolution.TopologicalSort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class DefaultFixtureLoader implements FixtureLoader
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    protected EntityManager entityManager;

    private DatabasePurger databasePurger = null;

    public DefaultFixtureLoader()
    {
        /* Default constructor */
    }

    /**
     * Construct a new {@link FixtureLoader} with the given {@link DatabasePurger} that will be executed before the fixtures are loaded.
     *
     * @param databasePurger The {@link DatabasePurger} to use.
     */
    public DefaultFixtureLoader(DatabasePurger databasePurger)
    {
        this.databasePurger = databasePurger;
    }

    @Override
    public ReferenceRepository load(Collection<Class<? extends Fixture>> fixtureClasses)
    {
        ReferenceRepository referenceRepository = new ReferenceRepository();

        if (null != this.databasePurger) {
            this.databasePurger.purge();
        }

        List<Fixture> orderedFixtures;
        try {
            orderedFixtures = this.getOrderedFixtures(fixtureClasses);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        for (Fixture fixture : orderedFixtures) {
            this.logger.info("Loading fixture " + fixture.getClass().getCanonicalName());
            fixture.load(this.entityManager, referenceRepository);
            this.entityManager.flush();
        }

        return referenceRepository;
    }

    private List<Fixture> getOrderedFixtures(Collection<Class<? extends Fixture>> fixtureClasses) throws IllegalAccessException, InstantiationException
    {
        Map<Class<? extends Fixture>, Fixture> instantiatedFixtures = new HashMap<>();
        DirectedGraph<Fixture> fixtureGraph = new DirectedGraph<>();
        for (Class<? extends Fixture> fixtureClass : fixtureClasses) {
            this.addFixtureClass(fixtureClass, fixtureGraph, instantiatedFixtures);
        }

        return TopologicalSort.getTopologialOrder(fixtureGraph);
    }

    private Fixture addFixtureClass(
            Class<? extends Fixture> fixtureClass,
            DirectedGraph<Fixture> fixtureGraph,
            Map<Class<? extends Fixture>, Fixture> instantiatedFixtures
    ) throws IllegalAccessException, InstantiationException
    {
        Fixture fixture = instantiatedFixtures.get(fixtureClass);
        if (null == fixture) {
            fixture = this.instantiateFixtureClass(fixtureClass);
            fixtureGraph.addVertex(fixture);
            instantiatedFixtures.put(fixtureClass, fixture);
            for (Class<? extends Fixture> dependingFixtureClass : fixture.getDependencies()) {
                Fixture dependingFixture =
                        this.addFixtureClass(dependingFixtureClass, fixtureGraph, instantiatedFixtures);
                fixtureGraph.addEdge(dependingFixture, fixture);
            }
        }

        return fixture;
    }

    protected Fixture instantiateFixtureClass(Class<? extends Fixture> fixtureClass) throws InstantiationException, IllegalAccessException
    {
        return fixtureClass.newInstance();
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
}
