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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.core.klayoutdata.KLayoutData;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KNode;

/**
 * A simple attachment target provider that returns all siblings of a comment as possible attachment
 * targets. The provider can be configured to only return non-comment siblings (the default) or also
 * include comments.
 */
public class SiblingAttachmentTargetProvider implements IAttachmentTargetProvider {
    
    /** Whether to return siblings that are comments themselves as well. */
    private boolean includeComments = false;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    /**
     * Configures this attachment target provider to also return siblings which are comments
     * themselves. It doesn't do so by default.
     * 
     * @return this attachment target provider (for configuration method chaining).
     */
    public SiblingAttachmentTargetProvider includeComments() {
        includeComments = true;
        return this;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // IAttachmentTargetProvider

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KGraphElement> provideAttachmentTargetsFor(final KNode comment) {
        if (comment.getParent() == null) {
            return Collections.emptyList();
        } else {
            return comment.getParent().getChildren()
                .stream()
                .filter((sibling) -> sibling != comment)
                .filter((sibling) -> includeComments || !isCommentNode(sibling))
                .collect(Collectors.toList());
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    
    /**
     * Checks if the given graph element is a {@link KNode} and marked as a comment node.
     * 
     * @param graphElement the graph element to check.
     * @return {@code true} if it's a comment node.
     */
    private boolean isCommentNode(final KGraphElement graphElement) {
        KLayoutData layoutData = graphElement.getData(KLayoutData.class);
        return graphElement instanceof KNode && layoutData.getProperty(CoreOptions.COMMENT_BOX);
    }

}
