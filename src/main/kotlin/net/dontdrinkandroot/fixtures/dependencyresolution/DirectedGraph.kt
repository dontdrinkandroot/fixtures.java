package net.dontdrinkandroot.fixtures.dependencyresolution

import java.util.*

/**
 * Simple implementation of a Directed Graph.
 */
class DirectedGraph<V> {

    internal val vertices: MutableSet<V> = HashSet()

    internal val outgoingEdges: MutableMap<V, MutableSet<V>> = HashMap()

    private val incomingEdges: MutableMap<V, MutableSet<V>> = HashMap()

    fun addEdge(start: V, end: V) {
        vertices.add(start)
        vertices.add(end)
        outgoingEdges.computeIfAbsent(start, { HashSet() }).add(end)
        incomingEdges.computeIfAbsent(end, { HashSet() }).add(start)
    }

    fun addVertex(v: V) {
        vertices.add(v)
    }

    fun getVertices(): Set<V> = vertices

    fun hasIncomingEdges(v: V): Boolean = incomingEdges.getOrDefault(v, emptySet()).isNotEmpty()

    fun getOutgoingVertices(v: V): Set<V> = HashSet(outgoingEdges.getOrDefault(v, emptySet()))

    fun removeEdge(start: V, end: V) {
        outgoingEdges[start]?.let {
            it.remove(end)
            if (it.isEmpty()) outgoingEdges.remove(start)
        }
        incomingEdges[end]?.let {
            it.remove(start)
            if (it.isEmpty()) incomingEdges.remove(end)
        }
    }

    fun hasEdges(): Boolean = outgoingEdges.isNotEmpty()

    fun clone(): DirectedGraph<V> {
        val clonedGraph = DirectedGraph<V>()
        clonedGraph.vertices.addAll(vertices)
        for ((key, value) in outgoingEdges) clonedGraph.outgoingEdges[key] = HashSet(value)
        for ((key, value) in incomingEdges) clonedGraph.incomingEdges[key] = HashSet(value)
        return clonedGraph
    }
}