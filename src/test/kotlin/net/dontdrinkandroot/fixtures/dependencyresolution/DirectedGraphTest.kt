package net.dontdrinkandroot.fixtures.dependencyresolution

import org.junit.Assert
import org.junit.Test

class DirectedGraphTest {

    @Test
    fun testAddAndRemove() {
        var vertices: Set<String?>
        val graph = DirectedGraph<String>()
        graph.addEdge("one", "two")
        vertices = graph.getVertices()
        Assert.assertEquals(2, vertices.size.toLong())
        Assert.assertTrue(vertices.contains("one"))
        Assert.assertTrue(vertices.contains("two"))
        Assert.assertFalse(graph.hasIncomingEdges("one"))
        Assert.assertTrue(graph.hasIncomingEdges("two"))
        vertices = graph.getOutgoingVertices("one")
        Assert.assertEquals(1, vertices.size.toLong())
        Assert.assertTrue(vertices.contains("two"))
        Assert.assertEquals(0, graph.getOutgoingVertices("two").size.toLong())
        graph.removeEdge("one", "two")
        Assert.assertFalse(graph.hasEdges())
        Assert.assertFalse(graph.hasIncomingEdges("one"))
        Assert.assertFalse(graph.hasIncomingEdges("two"))
        Assert.assertEquals(2, graph.getVertices().size.toLong())
        Assert.assertEquals(0, graph.getOutgoingVertices("one").size.toLong())
        Assert.assertEquals(0, graph.getOutgoingVertices("two").size.toLong())
    }
}