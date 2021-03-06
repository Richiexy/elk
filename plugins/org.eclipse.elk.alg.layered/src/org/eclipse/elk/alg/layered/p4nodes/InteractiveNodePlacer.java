/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.layered.p4nodes;

import org.eclipse.elk.alg.layered.ILayoutPhase;
import org.eclipse.elk.alg.layered.IntermediateProcessingConfiguration;
import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.alg.layered.graph.LNode;
import org.eclipse.elk.alg.layered.graph.Layer;
import org.eclipse.elk.alg.layered.graph.LNode.NodeType;
import org.eclipse.elk.alg.layered.intermediate.IntermediateProcessorStrategy;
import org.eclipse.elk.alg.layered.properties.GraphProperties;
import org.eclipse.elk.alg.layered.properties.InternalProperties;
import org.eclipse.elk.alg.layered.properties.Spacings;
import org.eclipse.elk.core.util.IElkProgressMonitor;

/**
 * A node placer that keeps the pre-existing y coordinates of nodes. As far as dummy nodes are
 * concerned, the interactive node placer tries to compute sensible coordinates for them based
 * on the pre-existing routing of the edges they represent. If nodes overlap, they are moved as
 * far down as necessary to remove the overlaps.
 * 
 * <p>Using this node placer really only makes sense if the interactive implementations of all
 * previous phases are used as well.</p>
 * 
 * <dl>
 *   <dt>Preconditions:</dt>
 *     <dd>The graph layering was produced by the interactive layering algorithm</dd>
 *     <dd>The node ordering was produced by the interactive crossing minimization algorithm</dd>
 *   <dt>Postconditions:</dt>
 *     <dd>Each node is assigned a vertical coordinate such that no two nodes overlap</dd>
 *     <dd>The size of each layer is set according to the area occupied by its nodes</dd>
 *     <dd>The height of the graph is set to the maximal layer height</dd>
 * </dl>
 * 
 * @author cds
 */
public final class InteractiveNodePlacer implements ILayoutPhase {

    /** additional processor dependencies for graphs with hierarchical ports. */
    private static final IntermediateProcessingConfiguration HIERARCHY_PROCESSING_ADDITIONS =
        IntermediateProcessingConfiguration.createEmpty()
            .addBeforePhase5(IntermediateProcessorStrategy.HIERARCHICAL_PORT_POSITION_PROCESSOR);

    /**
     * {@inheritDoc}
     */
    public IntermediateProcessingConfiguration getIntermediateProcessingConfiguration(
            final LGraph graph) {
        
        if (graph.getProperty(InternalProperties.GRAPH_PROPERTIES).contains(
                GraphProperties.EXTERNAL_PORTS)) {
            return HIERARCHY_PROCESSING_ADDITIONS;
        } else {
            return null;
        }
    }
    
    /** Spacing values. */
    private Spacings spacings;

    /**
     * {@inheritDoc}
     */
    public void process(final LGraph layeredGraph, final IElkProgressMonitor monitor) {
        monitor.begin("Interactive node placement", 1);

        spacings = layeredGraph.getProperty(InternalProperties.SPACINGS);
        
        // Place the nodes in each layer
        for (Layer layer : layeredGraph) {
            placeNodes(layer);
        }
        
        // TODO Compute a graph offset?
        
        monitor.done();
    }
    
    /**
     * Places the nodes in the given layer.
     * 
     * @param layer the layer whose nodes to place.
     */
    private void placeNodes(final Layer layer) {
        // The minimum value for the next valid y coordinate
        double minValidY = Double.NEGATIVE_INFINITY;
        
        // The node type of the last node
        NodeType prevNodeType = NodeType.NORMAL;
        
        for (LNode node : layer) {
            // Check which kind of node it is
            NodeType nodeType = node.getType();
            if (nodeType != NodeType.NORMAL) {
                // While normal nodes have their original position already in them, with dummy nodes
                // it's more complicated. Check if the interactive crossing minimizer has calculated
                // an original position for the dummy node. If not, we compute one.
                Double originalYCoordinate = node.getProperty(
                        InternalProperties.ORIGINAL_DUMMY_NODE_POSITION);
                
                if (originalYCoordinate == null) {
                    // Make sure that the minimum valid Y position is usable
                    minValidY = Math.max(minValidY, 0.0);
                    
                    node.getPosition().y =
                            minValidY + spacings.getVerticalSpacing(nodeType, prevNodeType);
                } else {
                    node.getPosition().y = originalYCoordinate;
                }
            }
            
            // If the node extends into nodes we already placed above, we need to move it down
            float spacing = spacings.getVerticalSpacing(nodeType, prevNodeType);
            if (node.getPosition().y < minValidY + spacing + node.getMargin().top) {
                node.getPosition().y = minValidY + spacing + node.getMargin().top;
            }
            
            // Update minimum valid y coordinate and remember node type
            minValidY = node.getPosition().y + node.getSize().y + node.getMargin().bottom;
            prevNodeType = nodeType;
        }
    }

}
