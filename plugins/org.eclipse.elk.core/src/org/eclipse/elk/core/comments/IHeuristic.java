/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.comments;

import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KNode;

/**
 * An attachment heuristic that indicates how closely a comment is related to another graph element.
 * Attachment heuristics provide a raw heuristic result and a normalized result. The latter is
 * constrained to be in {@code [0, 1]} (including the boundaries), while the former is
 * unconstrained, but may choose to follow the same constraints.
 * 
 * <p>
 * Note that if an implementation holds state, all resources should be released once
 * {@link #cleanup()} is called.
 * </p>
 */
public interface IHeuristic {
    
    /**
     * Computes the raw heuristic value for the given comment and the given graph element. The
     * heuristic value is expressed as an arbitrary number.
     * 
     * <p>
     * Usually, the overall comment attachment will be based on the normalized value given by
     * {@link #normalized(KNode, KGraphElement)} instead of on the raw value, but the raw value may
     * be interesting for evaluation purposes. However, the raw value may well be equal to the
     * normalized value. An example are heuristics that conceptually return boolean values: a pair
     * of graph elements either is considered to be related ({@code 1}) or not ({@code 0}.
     * </p>
     * 
     * @param comment
     *            the comment node.
     * @param element
     *            the graph element.
     * @return the raw heuristic result for the given comment and graph element.
     */
    double raw(KNode comment, KGraphElement element);
    
    /**
     * Computes the normalized heuristic value in {@code [0, 1]} (including the boundaries) for the
     * given comment and the given graph element.
     * 
     * @param comment
     *            the comment node.
     * @param element
     *            the graph element.
     * @return the normalized heuristic result for the given comment and graph element.
     */
    double normalized(KNode comment, KGraphElement element);
    
    /**
     * Does any preprocessing necessary. This method is called before the first invocation of
     * the non-default interface methods for a given graph.
     * 
     * @implSpec
     * The default implementation does nothing.
     * 
     * @param graph
     *            the graph.
     * @param includeHierarchy
     *            if {@code true}, the preprocessing is done not only on the current hierarchy
     *            level, but also on all sub levels. Implementations may choose to behave
     *            differently depending on this value.
     */
    default void preprocess(KNode graph, boolean includeHierarchy) {
    }
    
    /**
     * Does any cleaning necessary to get the implementation ready for the next comment attachment
     * run. This method is called after the last invocation of the non-default interface methods for
     * a given graph.
     * 
     * @implSpec The default implementation does nothing.
     */
    default void cleanup() {
    }
    
}
