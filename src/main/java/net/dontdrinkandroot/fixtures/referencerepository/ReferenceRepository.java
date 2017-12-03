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

/**
 * Allows you to store references to already created objects and retrieve them in other fixtures.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface ReferenceRepository
{
    /**
     * Stores an object.
     *
     * @param name   The lookup name.
     * @param object The object.
     * @param <T>    Type of the object.
     */
    <T> void store(String name, T object);

    /**
     * Retrieves an already stored object.
     *
     * @param name The lookup name.
     * @param <T>  Type of the object.
     * @return The object.
     * @throws RuntimeException Thrown if no object can be found under the given name.
     */
    <T> T retrieve(String name);
}
