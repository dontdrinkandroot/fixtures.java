package net.dontdrinkandroot.fixtures.dependencyresolution

import net.dontdrinkandroot.fixtures.dependencyresolution.TopologicalSort.getTopologialOrder
import org.junit.Assert
import org.junit.Test

class TopologicalSortTest {

    @Test
    fun testEmpty() {
        val graph = DirectedGraph<String>()
        val orderedVertices = getTopologialOrder(graph)
        Assert.assertTrue(orderedVertices.isEmpty())
    }

    @Test
    fun testSimple() {
        val graph = DirectedGraph<String>()
        graph.addEdge("one", "two")
        val orderedVertices = getTopologialOrder(graph)
        Assert.assertEquals("one", orderedVertices[0])
        Assert.assertEquals("two", orderedVertices[1])
    }

    @Test
    fun testComplex() {
        val graph = DirectedGraph<String>()
        graph.addEdge("one", "two")
        graph.addEdge("two", "three")
        graph.addEdge("two", "four")
        val orderedVertices = getTopologialOrder(graph)
        Assert.assertEquals("one", orderedVertices[0])
        Assert.assertEquals("two", orderedVertices[1])
        Assert.assertEquals("three", orderedVertices[2])
        Assert.assertEquals("four", orderedVertices[3])
    }

    @Test
    fun testCycle() {
        val graph = DirectedGraph<String>()
        graph.addEdge("one", "two")
        graph.addEdge("two", "three")
        graph.addEdge("three", "one")
        try {
            val orderedVertices = getTopologialOrder(graph)
            Assert.fail("Exception expected")
        } catch (e: RuntimeException) {
            Assert.assertEquals(
                "Graph has at least one cycle: one -> two; three -> one; two -> three; ",
                e.message
            )
        }
    }
}