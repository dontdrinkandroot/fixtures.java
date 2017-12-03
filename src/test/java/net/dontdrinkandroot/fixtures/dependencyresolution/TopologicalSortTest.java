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
package net.dontdrinkandroot.fixtures.dependencyresolution;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class TopologicalSortTest
{
    @Test
    public void testEmpty()
    {
        DirectedGraph<String> graph = new DirectedGraph<>();
        List<String> orderedVertices = TopologicalSort.getTopologialOrder(graph);
        Assert.assertTrue(orderedVertices.isEmpty());
    }

    @Test
    public void testSimple()
    {
        DirectedGraph<String> graph = new DirectedGraph<>();
        graph.addEdge("one", "two");
        List<String> orderedVertices = TopologicalSort.getTopologialOrder(graph);
        Assert.assertEquals("one", orderedVertices.get(0));
        Assert.assertEquals("two", orderedVertices.get(1));
    }

    @Test
    public void testComplex()
    {
        DirectedGraph<String> graph = new DirectedGraph<>();
        graph.addEdge("one", "two");
        graph.addEdge("two", "three");
        graph.addEdge("two", "four");
        List<String> orderedVertices = TopologicalSort.getTopologialOrder(graph);
        Assert.assertEquals("one", orderedVertices.get(0));
        Assert.assertEquals("two", orderedVertices.get(1));
        Assert.assertEquals("three", orderedVertices.get(2));
        Assert.assertEquals("four", orderedVertices.get(3));
    }

    @Test
    public void testCycle()
    {
        DirectedGraph<String> graph = new DirectedGraph<>();
        graph.addEdge("one", "two");
        graph.addEdge("two", "three");
        graph.addEdge("three", "one");

        try {
            List<String> orderedVertices = TopologicalSort.getTopologialOrder(graph);
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            Assert.assertEquals(
                    "Graph has at least one cycle: one -> two; three -> one; two -> three; ",
                    e.getMessage()
            );
        }
    }
}
