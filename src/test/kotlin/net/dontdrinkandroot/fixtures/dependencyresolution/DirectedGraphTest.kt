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

import java.util.Set;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class DirectedGraphTest
{
    @Test
    public void testAddAndRemove()
    {
        Set<String> vertices;

        DirectedGraph<String> graph = new DirectedGraph<>();
        graph.addEdge("one", "two");

        vertices = graph.getVertices();
        Assert.assertEquals(2, vertices.size());
        Assert.assertTrue(vertices.contains("one"));
        Assert.assertTrue(vertices.contains("two"));

        Assert.assertFalse(graph.hasIncomingEdges("one"));
        Assert.assertTrue(graph.hasIncomingEdges("two"));

        vertices = graph.getOutgoingVertices("one");
        Assert.assertEquals(1, vertices.size());
        Assert.assertTrue(vertices.contains("two"));
        Assert.assertEquals(0, graph.getOutgoingVertices("two").size());

        graph.removeEdge("one", "two");

        Assert.assertFalse(graph.hasEdges());
        Assert.assertFalse(graph.hasIncomingEdges("one"));
        Assert.assertFalse(graph.hasIncomingEdges("two"));
        Assert.assertEquals(2, graph.getVertices().size());
        Assert.assertEquals(0, graph.getOutgoingVertices("one").size());
        Assert.assertEquals(0, graph.getOutgoingVertices("two").size());
    }
}
