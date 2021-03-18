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
 * Kahn's algorithm.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class TopologicalSort
{
    public static <V> List<V> getTopologialOrder(DirectedGraph<V> originalGraph)
    {
        DirectedGraph<V> graph = originalGraph.clone();

        List<V> orderedVertices = new ArrayList<>();

        Stack<V> independentVertices = new Stack<>();
        for (V v : graph.getVertices()) {
            if (!graph.hasIncomingEdges(v)) {
                independentVertices.push(v);
            }
        }

        while (!independentVertices.isEmpty()) {
            V n = independentVertices.pop();
            orderedVertices.add(n);
            Set<V> outgoingVertices = graph.getOutgoingVertices(n);
            for (V m : outgoingVertices) {
                graph.removeEdge(n, m);
                if (!graph.hasIncomingEdges(m)) {
                    independentVertices.push(m);
                }
            }
        }

        if (graph.hasEdges()) {
            StringBuilder stringBuilder = new StringBuilder();
            Map<V, Set<V>> outgoingEdges = graph.getOutgoingEdges();
            for (Map.Entry<V, Set<V>> entry : outgoingEdges.entrySet()) {
                for (V v : entry.getValue()) {
                    stringBuilder.append(entry.getKey());
                    stringBuilder.append(" -> ");
                    stringBuilder.append(v);
                    stringBuilder.append("; ");
                }
            }
            throw new RuntimeException("Graph has at least one cycle: " + stringBuilder);
        }

        return orderedVertices;
    }
}
