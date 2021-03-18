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

import java.util.*;

/**
 * Simple implementation of a Directed Graph.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class DirectedGraph<V>
{
    private final Set<V> vertices = new HashSet<>();

    private final Map<V, Set<V>> outgoingEdges = new HashMap<>();

    private final Map<V, Set<V>> incomingEdges = new HashMap<>();

    public void addEdge(V start, V end)
    {
        this.vertices.add(start);
        this.vertices.add(end);

        Set<V> oppositeVertices;

        oppositeVertices = this.outgoingEdges.computeIfAbsent(start, k -> new HashSet<>());
        oppositeVertices.add(end);

        oppositeVertices = this.incomingEdges.computeIfAbsent(end, k -> new HashSet<>());
        oppositeVertices.add(start);
    }

    public void addVertex(V v)
    {
        this.vertices.add(v);
    }

    public Set<V> getVertices()
    {
        return this.vertices;
    }

    public boolean hasIncomingEdges(V v)
    {
        return !this.incomingEdges.getOrDefault(v, Collections.emptySet()).isEmpty();
    }

    public Set<V> getOutgoingVertices(V v)
    {
        return new HashSet<>(this.outgoingEdges.getOrDefault(v, Collections.emptySet()));
    }

    public void removeEdge(V start, V end)
    {
        if (this.outgoingEdges.containsKey(start)) {
            this.outgoingEdges.get(start).remove(end);
            if (this.outgoingEdges.get(start).isEmpty()) {
                this.outgoingEdges.remove(start);
            }
        }
        if (this.incomingEdges.containsKey(end)) {
            this.incomingEdges.get(end).remove(start);
            if (this.incomingEdges.get(end).isEmpty()) {
                this.incomingEdges.remove(end);
            }
        }
    }

    public boolean hasEdges()
    {
        return this.outgoingEdges.size() > 0;
    }

    public Map<V, Set<V>> getOutgoingEdges()
    {
        return this.outgoingEdges;
    }

    public DirectedGraph<V> clone()
    {
        DirectedGraph<V> clonedGraph = new DirectedGraph<>();
        clonedGraph.vertices.addAll(this.vertices);

        for (Map.Entry<V, Set<V>> entry : this.outgoingEdges.entrySet()) {
            Set<V> oppositeVertices = new HashSet<>();
            oppositeVertices.addAll(entry.getValue());
            clonedGraph.outgoingEdges.put(entry.getKey(), oppositeVertices);
        }

        for (Map.Entry<V, Set<V>> entry : this.incomingEdges.entrySet()) {
            Set<V> oppositeVertices = new HashSet<>();
            oppositeVertices.addAll(entry.getValue());
            clonedGraph.incomingEdges.put(entry.getKey(), oppositeVertices);
        }

        return clonedGraph;
    }
}
