package net.dontdrinkandroot.fixtures;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.Map.Entry;

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

        List<Fixture> orderedFixtures = this.getOrderedFixtures(fixtureClasses);

        for (Fixture fixture : orderedFixtures) {
            this.logger.info("Loading fixture " + fixture.getClass().getCanonicalName());
            fixture.load(this.entityManager, this.referenceRepository);
            this.entityManager.flush();
        }
    }

    private List<Fixture> getOrderedFixtures(Collection<Class<? extends Fixture>> fixtureClasses)
    {
        List<Fixture> orderedFixtures = new ArrayList<Fixture>();
        Map<Class<? extends Fixture>, Fixture> fixtureClassMap = new HashMap<Class<? extends Fixture>, Fixture>();
        Set<Class<? extends Fixture>> nonDependingFixtureClasses = new HashSet<Class<? extends Fixture>>();
        Map<Class<? extends Fixture>, Set<Class<? extends Fixture>>> fixtureClassDependencies = new HashMap<>();

        this.resolveDependencies(fixtureClasses, fixtureClassMap, nonDependingFixtureClasses, fixtureClassDependencies);

        while (!nonDependingFixtureClasses.isEmpty()) {
            Iterator<Class<? extends Fixture>> nonDependingFixtureClassIterator = nonDependingFixtureClasses.iterator();
            Class<? extends Fixture> nonDependingFixtureClass = nonDependingFixtureClassIterator.next();
            // System.out.println("Popping from S: " + nonDependingFixtureClass.getCanonicalName());
            nonDependingFixtureClassIterator.remove();
            // System.out.println("Adding to L: " + nonDependingFixtureClass.getCanonicalName());
            orderedFixtures.add(fixtureClassMap.get(nonDependingFixtureClass));
            Iterator<Entry<Class<? extends Fixture>, Set<Class<? extends Fixture>>>> iterator =
                    fixtureClassDependencies.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<Class<? extends Fixture>, Set<Class<? extends Fixture>>> entry = iterator.next();
                Iterator<Class<? extends Fixture>> iterator2 = entry.getValue().iterator();
                while (iterator2.hasNext()) {
                    Class<? extends Fixture> next = iterator2.next();
                    // System.out.println(
                    // "Checking edge : " + entry.getKey().getCanonicalName() + " -> " + next.getCanonicalName());
                    if (next.equals(nonDependingFixtureClass)) {
                        // System.out.println(
                        // "Removing edge : "
                        // + entry.getKey().getCanonicalName()
                        // + " -> "
                        // + next.getCanonicalName());
                        iterator2.remove();
                    }
                }
                if (entry.getValue().isEmpty()) {
                    // System.out.println("Adding to S: " + entry.getKey().getCanonicalName());
                    nonDependingFixtureClasses.add(entry.getKey());
                }
                if (entry.getValue().isEmpty()) {
                    iterator.remove();
                }
            }
        }

        // System.out.println("Fixture order:");
        // for (Fixture fixture : orderedFixtures) {
        // System.out.println("\t" + fixture.getClass().getCanonicalName());
        // }

        if (!fixtureClassDependencies.isEmpty()) {
            StringBuffer remainingBuffer =
                    new StringBuffer("Could not find fixture ordering. Remaining dependencies: ");
            for (Entry<Class<? extends Fixture>, Set<Class<? extends Fixture>>> entry : fixtureClassDependencies.entrySet()) {
                remainingBuffer.append(entry.getKey().getSimpleName()).append(": ");
                for (Class<? extends Fixture> dependantClass : entry.getValue()) {
                    remainingBuffer.append(dependantClass).append(", ");
                }
            }
            throw new RuntimeException(remainingBuffer.toString());
        }

        return orderedFixtures;
    }

    private void resolveDependencies(
            Collection<Class<? extends Fixture>> fixtureClasses,
            Map<Class<? extends Fixture>, Fixture> fixtureClassMap,
            Set<Class<? extends Fixture>> nonDependingFixtureClasses,
            Map<Class<? extends Fixture>, Set<Class<? extends Fixture>>> fixtureClassDependencies
    )
    {
        for (Class<? extends Fixture> fixtureClass : fixtureClasses) {
            if (!fixtureClassMap.containsKey(fixtureClass)) {
                try {
                    Fixture fixture = this.instantiateFixtureClass(fixtureClass);
                    fixtureClassMap.put(fixtureClass, fixture);
                    Collection<Class<? extends Fixture>> dependencies = fixture.getDependencies();
                    if (dependencies.isEmpty()) {
                        nonDependingFixtureClasses.add(fixtureClass);
                    } else {
                        fixtureClassDependencies.put(fixtureClass, new HashSet<Class<? extends Fixture>>(dependencies));
                        this.resolveDependencies(
                                dependencies,
                                fixtureClassMap,
                                nonDependingFixtureClasses,
                                fixtureClassDependencies
                        );
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected Fixture instantiateFixtureClass(Class<? extends Fixture> fixtureClass) throws InstantiationException, IllegalAccessException
    {
        return fixtureClass.newInstance();
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
