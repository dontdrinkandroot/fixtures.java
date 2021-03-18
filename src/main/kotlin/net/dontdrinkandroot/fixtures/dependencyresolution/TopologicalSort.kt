package net.dontdrinkandroot.fixtures.dependencyresolution

import java.util.*

/**
 * Kahn's algorithm.
 */
fun <V> DirectedGraph<V>.getTopologialOrder(): List<V> {
    val graph = this.clone()
    val orderedVertices: MutableList<V> = ArrayList()
    val independentVertices = Stack<V>()
    for (v in graph.getVertices()) {
        if (!graph.hasIncomingEdges(v)) {
            independentVertices.push(v)
        }
    }
    while (!independentVertices.isEmpty()) {
        val n = independentVertices.pop()
        orderedVertices.add(n)
        val outgoingVertices = graph.getOutgoingVertices(n)
        for (m in outgoingVertices) {
            graph.removeEdge(n, m)
            if (!graph.hasIncomingEdges(m)) {
                independentVertices.push(m)
            }
        }
    }
    if (graph.hasEdges()) {
        val stringBuilder = StringBuilder()
        val outgoingEdges: Map<V, Set<V>> = graph.getOutgoingEdges()
        for ((key, value) in outgoingEdges) {
            for (v in value) {
                stringBuilder.append(key)
                stringBuilder.append(" -> ")
                stringBuilder.append(v)
                stringBuilder.append("; ")
            }
        }
        throw RuntimeException("Graph has at least one cycle: $stringBuilder")
    }
    return orderedVertices
}