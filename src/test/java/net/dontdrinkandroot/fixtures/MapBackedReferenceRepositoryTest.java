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

import net.dontdrinkandroot.fixtures.referencerepository.MapBackedReferenceRepository;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class MapBackedReferenceRepositoryTest
{
    @Test
    public void canStoreAndRetrieve()
    {
        MapBackedReferenceRepository referenceRepository = new MapBackedReferenceRepository();
        referenceRepository.store("test", "asdf");
        Assert.assertEquals("asdf", referenceRepository.retrieve("test"));
    }

    @Test(expected = RuntimeException.class)
    public void nullReferenceThrowsException()
    {
        MapBackedReferenceRepository referenceRepository = new MapBackedReferenceRepository();
        referenceRepository.retrieve("asdf");
    }

    @Test(expected = ClassCastException.class)
    public void wrongClassCastThrowsException()
    {
        MapBackedReferenceRepository referenceRepository = new MapBackedReferenceRepository();
        referenceRepository.store("test", "asdf");
        Integer foo = referenceRepository.retrieve("test");
    }
}
