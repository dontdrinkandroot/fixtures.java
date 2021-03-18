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
package net.dontdrinkandroot.fixtures;

import net.dontdrinkandroot.fixtures.referencerepository.ReferenceRepository;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;

/**
 * A {@link Fixture} is responsible for storing one or many objects in the database.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface Fixture
{
    /**
     * Specifies fixture classes that need to be loaded before this fixture.
     *
     * @return The collection of fixture classes to load before this fixture.
     */
    default Collection<Class<? extends Fixture>> getDependencies()
    {
        return Collections.emptyList();
    }

    /**
     * Loads this fixture into the database via the {@link EntityManager}. Can use the {@link ReferenceRepository} to
     * retrieve objects needed and to store the objects created.
     *
     * @param entityManager       The EntityManager to interact with the database.
     * @param referenceRepository The Repository to store or retrieve other fixture objects.
     */
    void load(EntityManager entityManager, ReferenceRepository referenceRepository);
}
