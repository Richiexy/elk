/*******************************************************************************
 * Copyright (c) 2008, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.KNode;

/**
 * A graph layout engine is able to perform automatic layout on a graph or parts of it.
 * 
 * @author swe
 * @kieler.rating yellow 2012-08-10 review KI-23 by cds, sgu
 * @kieler.design proposed by msp
 */
public interface IGraphLayoutEngine {

    /**
     * Performs layout on the given layout graph.
     * 
     * @param layoutGraph
     *            the top-level node of the graph to be laid out
     * @param progressMonitor
     *            monitor to which progress of the layout algorithms is reported
     * @throws UnsupportedGraphException
     *             if the given graph is not supported by this algorithm
     * @throws UnsupportedConfigurationException
     *             if the layout configuration included in the graph is inconsistent or incompatible
     */
    void layout(KNode layoutGraph, IElkProgressMonitor progressMonitor);

    /**
     * Returns the ID of the layout algorithm used by this layout engine if no algorithm is explicitly set in the graph.
     * 
     * @implSpec The default implementation returns {@code "org.eclipse.elk.layered"}.
     * 
     * @return ID of the default layout algorithm.
     */
    default String getDefaultLayoutAlgorithmID() {
        return "org.eclipse.elk.layered";
    }

}
