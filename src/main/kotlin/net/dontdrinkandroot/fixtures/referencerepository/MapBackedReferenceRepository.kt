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
package net.dontdrinkandroot.fixtures.referencerepository;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ReferenceRepository} that uses a {@link HashMap} as storage.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class MapBackedReferenceRepository implements ReferenceRepository
{
    private final Map<String, Object> objects = new HashMap<>();

    @Override
    public <T> void store(String name, T object)
    {
        this.objects.put(name, object);
    }

    @Override
    public <T> T retrieve(String name)
    {
        @SuppressWarnings("unchecked")
        T reference = (T) this.objects.get(name);
        if (null == reference) {
            throw new RuntimeException(String.format("No reference found with name '%s'", name));
        }

        return reference;
    }
}
