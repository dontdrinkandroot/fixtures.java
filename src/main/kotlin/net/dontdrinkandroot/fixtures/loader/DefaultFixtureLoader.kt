/*
 * Copyright (C) 2017 Philip Washington Sorst <philip@sorst.net>
 * and individual contributors as indicated
 * by the @authors tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dontdrinkandroot.fixtures.loader;

import net.dontdrinkandroot.fixtures.Fixture;
import net.dontdrinkandroot.fixtures.dependencyresolution.DirectedGraph;
import net.dontdrinkandroot.fixtures.dependencyresolution.TopologicalSort;
import net.dontdrinkandroot.fixtures.purger.DatabasePurger;
import net.dontdrinkandroot.fixtures.referencerepository.MapBackedReferenceRepository;
import net.dontdrinkandroot.fixtures.referencerepository.ReferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link FixtureLoader} that is based on an {@link EntityManager} and executes a {@link DatabasePurger} before
 * loading the fixtures.
 *
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
     * Construct a new {@link FixtureLoader} with the given {@link DatabasePurger} that will be executed before the
     * fixtures are loaded.
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
        ReferenceRepository referenceRepository = new MapBackedReferenceRepository();

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
