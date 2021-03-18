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

import net.dontdrinkandroot.fixtures.example.*;
import net.dontdrinkandroot.fixtures.loader.DefaultFixtureLoader;
import net.dontdrinkandroot.fixtures.purger.NoopDatabasePurger;
import net.dontdrinkandroot.fixtures.referencerepository.ReferenceRepository;
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
