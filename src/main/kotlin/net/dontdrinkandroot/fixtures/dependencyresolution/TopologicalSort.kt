package net.dontdrinkandroot.fixtures.dependencyresolution

import java.util.*

/**
 * Kahn's algorithm.
 */
fun <V> DirectedGraph<V>.getTopologicalOrder(): List<V> {
    val graph = this.clone()
    val orderedVertices: MutableList<V> = ArrayList()
    val independentVertices = Stack<V>()

    for (v in graph.vertices) {
        if (!graph.hasIncomingEdges(v)) independentVertices.push(v)
    }

    while (!independentVertices.isEmpty()) {
        val n = independentVertices.pop()
        orderedVertices.add(n)
        val outgoingVertices = graph.getOutgoingVertices(n)
        for (m in outgoingVertices) {
            graph.removeEdge(n, m)
            if (!graph.hasIncomingEdges(m)) independentVertices.push(m)
        }
    }

    if (graph.hasEdges()) handleCycles(graph)

    return orderedVertices
}

private fun <V> handleCycles(graph: DirectedGraph<V>) {
    val stringBuilder = StringBuilder()
    for ((key, value) in graph.outgoingEdges) {
        for (v in value) stringBuilder.append("$key -> $v; ")
    }
    throw RuntimeException("Graph has at least one cycle: $stringBuilder")
}