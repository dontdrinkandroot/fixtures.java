package net.dontdrinkandroot.fixtures.dependencyresolution;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class TopologicalSortTest
{
    @Test
    public void testEmpty()
    {
        DirectedGraph<String> graph = new DirectedGraph<>();
        List<String> orderedVertices = TopologicalSort.getTopologialOrder(graph);
        Assert.assertTrue(orderedVertices.isEmpty());
    }

    @Test
    public void testSimple()
    {
        DirectedGraph<String> graph = new DirectedGraph<>();
        graph.addEdge("one", "two");
        List<String> orderedVertices = TopologicalSort.getTopologialOrder(graph);
        Assert.assertEquals("one", orderedVertices.get(0));
        Assert.assertEquals("two", orderedVertices.get(1));
    }

    @Test
    public void testComplex()
    {
        DirectedGraph<String> graph = new DirectedGraph<>();
        graph.addEdge("one", "two");
        graph.addEdge("two", "three");
        graph.addEdge("two", "four");
        List<String> orderedVertices = TopologicalSort.getTopologialOrder(graph);
        Assert.assertEquals("one", orderedVertices.get(0));
        Assert.assertEquals("two", orderedVertices.get(1));
        Assert.assertEquals("three", orderedVertices.get(2));
        Assert.assertEquals("four", orderedVertices.get(3));
    }

    @Test
    public void testCycle()
    {
        DirectedGraph<String> graph = new DirectedGraph<>();
        graph.addEdge("one", "two");
        graph.addEdge("two", "three");
        graph.addEdge("three", "one");

        try {
            List<String> orderedVertices = TopologicalSort.getTopologialOrder(graph);
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            Assert.assertEquals(
                    "Graph has at least one cycle: one -> two; three -> one; two -> three; ",
                    e.getMessage()
            );
        }
    }
}
