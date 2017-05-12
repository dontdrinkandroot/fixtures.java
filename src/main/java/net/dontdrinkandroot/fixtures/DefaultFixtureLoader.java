package net.dontdrinkandroot.fixtures;

import net.dontdrinkandroot.fixtures.dependencyresolution.DirectedGraph;
import net.dontdrinkandroot.fixtures.dependencyresolution.TopologicalSort;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

public class DefaultFixtureLoader implements FixtureLoader
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    protected EntityManager entityManager;

    protected ReferenceRepository referenceRepository = new ReferenceRepository();

    protected Set<Pair<Class<?>, Class<?>>> purgeIgnoreAssociations;

    @Override
    public void load(Collection<Class<? extends Fixture>> fixtureClasses)
    {
        DatabasePurger purger = new DatabasePurger(this.entityManager, this.purgeIgnoreAssociations);
        purger.purge();

        List<Fixture> orderedFixtures;
        try {
            orderedFixtures = this.getOrderedFixtures(fixtureClasses);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        for (Fixture fixture : orderedFixtures) {
            this.logger.info("Loading fixture " + fixture.getClass().getCanonicalName());
            fixture.load(this.entityManager, this.referenceRepository);
            this.entityManager.flush();
        }
    }

    private List<Fixture> getOrderedFixtures(Collection<Class<? extends Fixture>> fixtureClasses) throws IllegalAccessException, InstantiationException
    {
        Map<Class<? extends Fixture>, Fixture> instantiatedFixtures = new HashMap<>();
        DirectedGraph<Fixture> fixtureGraph = new DirectedGraph<>();
        for (Class<? extends Fixture> fixtureClass : fixtureClasses) {
            Fixture fixture = this.instantiateFixtureClass(fixtureClass, instantiatedFixtures);
            for (Class<? extends Fixture> dependingFixtureClass : fixture.getDependencies()) {
                Fixture dependingFixture = this.instantiateFixtureClass(dependingFixtureClass, instantiatedFixtures);
                fixtureGraph.addEdge(dependingFixture, fixture);
            }
        }

        return TopologicalSort.getTopologialOrder(fixtureGraph);
    }

    protected Fixture instantiateFixtureClass(
            Class<? extends Fixture> fixtureClass, Map<Class<? extends Fixture>, Fixture> instantiatedFixtures
    ) throws InstantiationException, IllegalAccessException
    {
        Fixture fixture = instantiatedFixtures.get(fixtureClass);
        if (null == fixture) {
            fixture = fixtureClass.newInstance();
            instantiatedFixtures.put(fixtureClass, fixture);
        }

        return fixture;
    }

    @Override
    public ReferenceRepository getReferenceRepository()
    {
        return this.referenceRepository;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    public void setPurgeIgnoreAssociations(Set<Pair<Class<?>, Class<?>>> purgeIgnoreAssociations)
    {
        this.purgeIgnoreAssociations = purgeIgnoreAssociations;
    }
}
