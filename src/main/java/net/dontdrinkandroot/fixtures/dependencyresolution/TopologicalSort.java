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
                    stringBuilder.append(";");
                }
            }
            throw new RuntimeException("Graph has at least one cycle: " + stringBuilder);
        }

        return orderedVertices;
    }
}
