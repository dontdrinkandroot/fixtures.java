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
import net.dontdrinkandroot.fixtures.referencerepository.ReferenceRepository;

import java.util.Collection;

/**
 * A {@link FixtureLoader} loads a given set of fixtures into the database.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface FixtureLoader
{
    /**
     * Loads the given fixtures.
     *
     * @param fixtureClasses The fixtures to load.
     * @return The populated {@link ReferenceRepository}
     */
    ReferenceRepository load(Collection<Class<? extends Fixture>> fixtureClasses);
}
