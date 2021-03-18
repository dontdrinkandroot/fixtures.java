package net.dontdrinkandroot.fixtures.dependencyresolution

import java.util.*

/**
 * Simple implementation of a Directed Graph.
 */
class DirectedGraph<V> {

    private val vertices: MutableSet<V> = HashSet()

    private val outgoingEdges: MutableMap<V, MutableSet<V>> = HashMap()

    private val incomingEdges: MutableMap<V, MutableSet<V>> = HashMap()

    fun addEdge(start: V, end: V) {
        vertices.add(start)
        vertices.add(end)
        var oppositeVertices: MutableSet<V> = outgoingEdges.computeIfAbsent(start, { HashSet() })
        oppositeVertices.add(end)
        oppositeVertices = incomingEdges.computeIfAbsent(end, { HashSet() })
        oppositeVertices.add(start)
    }

    fun addVertex(v: V) {
        vertices.add(v)
    }

    fun getVertices(): Set<V> {
        return vertices
    }

    fun hasIncomingEdges(v: V): Boolean {
        return !incomingEdges.getOrDefault(v, emptySet()).isEmpty()
    }

    fun getOutgoingVertices(v: V): Set<V> {
        return HashSet(outgoingEdges.getOrDefault(v, emptySet()))
    }

    fun removeEdge(start: V, end: V) {
        if (outgoingEdges.containsKey(start)) {
            outgoingEdges[start]!!.remove(end)
            if (outgoingEdges[start]!!.isEmpty()) {
                outgoingEdges.remove(start)
            }
        }
        if (incomingEdges.containsKey(end)) {
            incomingEdges[end]!!.remove(start)
            if (incomingEdges[end]!!.isEmpty()) {
                incomingEdges.remove(end)
            }
        }
    }

    fun hasEdges(): Boolean {
        return outgoingEdges.size > 0
    }

    fun getOutgoingEdges(): Map<V, MutableSet<V>> {
        return outgoingEdges
    }

    fun clone(): DirectedGraph<V> {
        val clonedGraph = DirectedGraph<V>()
        clonedGraph.vertices.addAll(vertices)
        for ((key, value) in outgoingEdges) {
            val oppositeVertices: MutableSet<V> = HashSet()
            oppositeVertices.addAll(value)
            clonedGraph.outgoingEdges[key] = oppositeVertices
        }
        for ((key, value) in incomingEdges) {
            val oppositeVertices: MutableSet<V> = HashSet()
            oppositeVertices.addAll(value)
            clonedGraph.incomingEdges[key] = oppositeVertices
        }
        return clonedGraph
    }
}