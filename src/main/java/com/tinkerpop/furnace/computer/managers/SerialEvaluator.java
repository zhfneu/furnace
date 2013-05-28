package com.tinkerpop.furnace.computer.managers;

import com.google.common.base.Preconditions;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.furnace.computer.Evaluator;
import com.tinkerpop.furnace.computer.GlobalMemory;
import com.tinkerpop.furnace.computer.GraphComputer;
import com.tinkerpop.furnace.computer.LocalMemory;
import com.tinkerpop.furnace.computer.VertexProgram;

/**
 * A SerialVertexComputerGraph implements the vertex-centric graph computing model in a serial, single-threaded manner.
 * This implementation is simple and provides a reference implementation for the semantics of the interfaces.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class SerialEvaluator implements Evaluator {

    public void execute(final GraphComputer graphComputer) {
        final Graph graph = graphComputer.getGraph();
        final VertexProgram vertexProgram = graphComputer.getVertexProgram();
        final GlobalMemory globalMemory = graphComputer.getGlobalMemory();
        final LocalMemory localMemory = graphComputer.getLocalMemory();
        final CoreShellVertex coreShellVertex = new CoreShellVertex(localMemory);

        Preconditions.checkArgument(graph.getFeatures().supportsVertexIteration);

        if (graphComputer.doSetup()) {
            for (final Vertex vertex : graph.getVertices()) {
                coreShellVertex.setBaseVertex(vertex);
                vertexProgram.setup(coreShellVertex, globalMemory);
            }
        }

        while (!graphComputer.terminate()) {
            localMemory.completeRound();
            for (final Vertex vertex : graph.getVertices()) {
                coreShellVertex.setBaseVertex(vertex);
                vertexProgram.execute(coreShellVertex, globalMemory);
            }

        }
    }
}