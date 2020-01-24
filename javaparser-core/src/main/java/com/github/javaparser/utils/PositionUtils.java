/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2020 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */

package com.github.javaparser.utils;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.type.Type;

import java.util.List;
import java.util.Objects;

import static java.lang.Integer.signum;

public final class PositionUtils {

    private PositionUtils() {
        // prevent instantiation
    }

    public static <T extends Node> void sortByBeginPosition(List<T> nodes) {
        sortByBeginPosition(nodes, false);
    }

    public static <T extends Node> void sortByBeginPosition(NodeList<T> nodes) {
        sortByBeginPosition(nodes, false);
    }

    public static <T extends Node> void sortByBeginPosition(List<T> nodes, final boolean ignoringAnnotations) {
        nodes.sort((o1, o2) -> PositionUtils.compare(o1, o2, ignoringAnnotations));
    }

    public static boolean areInOrder(Node a, Node b) {
        return areInOrder(a, b, false);
    }

    public static boolean areInOrder(Node a, Node b, boolean ignoringAnnotations) {
        return compare(a, b, ignoringAnnotations) <= 0;
    }

    private static int compare(Node a, Node b, boolean ignoringAnnotations) {
        if (a.getRange().isPresent() && !b.getRange().isPresent()) {
            return -1;
        }
        if (!a.getRange().isPresent() && b.getRange().isPresent()) {
            return 1;
        }
        if (!a.getRange().isPresent() && !b.getRange().isPresent()) {
            return 0;
        }
        if (ignoringAnnotations) {
            int signLine = signum(beginLineWithoutConsideringAnnotation(a) - beginLineWithoutConsideringAnnotation(b));
            if (signLine == 0) {
                return signum(beginColumnWithoutConsideringAnnotation(a) - beginColumnWithoutConsideringAnnotation(b));
            } else {
                return signLine;
            }
        }

        Position aBegin = a.getBegin().get();
        Position bBegin = b.getBegin().get();

        int signLine = signum(aBegin.line - bBegin.line);
        if (signLine == 0) {
            return signum(aBegin.column - bBegin.column);
        } else {
            return signLine;
        }
    }

    public static AnnotationExpr getLastAnnotation(Node node) {
        if (node instanceof NodeWithAnnotations) {
            NodeList<AnnotationExpr> annotations = NodeList.nodeList(((NodeWithAnnotations<?>) node).getAnnotations());
            if (annotations.isEmpty()) {
                return null;
            }
            sortByBeginPosition(annotations);
            return annotations.get(annotations.size() - 1);
        } else {
            return null;
        }
    }

    private static int beginLineWithoutConsideringAnnotation(Node node) {
        return firstNonAnnotationNode(node).getRange().get().begin.line;
    }


    private static int beginColumnWithoutConsideringAnnotation(Node node) {
        return firstNonAnnotationNode(node).getRange().get().begin.column;
    }

    private static Node firstNonAnnotationNode(Node node) {
        if (node instanceof MethodDeclaration || node instanceof FieldDeclaration) {
            NodeWithType<?, Type> casted = (NodeWithType<?, Type>) node;
            return casted.getType();
        } else if (node instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration casted = (ClassOrInterfaceDeclaration) node;
            return casted.getName();
        } else {
            return node;
        }
    }

    /**
     * Compare the position of two nodes. Optionally include annotations within the range checks.
     * This method takes into account whether the nodes are within the same compilation unit.
     * <p>
     * Note that this performs a "strict contains", where the container must extend beyond the other node in both
     * directions (otherwise it would count as an overlap, rather than "contain").
     */
    public static boolean nodeContains(Node container, Node other, boolean ignoringAnnotations) {
        if (!container.getRange().isPresent()) {
            throw new IllegalArgumentException("Cannot compare the positions of nodes if container node does not have a range.");
        }
        if (!other.getRange().isPresent()) {
            throw new IllegalArgumentException("Cannot compare the positions of nodes if contained node does not have a range.");
        }

        if (!Objects.equals(container.findCompilationUnit(), other.findCompilationUnit())) {
            // Allow the check to complete if they are both within a known CU (i.e. the CUs are the same),
            // ... or both not within a CU (i.e. both are Optional.empty())
            return false;
        }

        final boolean nodeCanHaveAnnotations = container instanceof NodeWithAnnotations;
        final boolean hasAnnotations = PositionUtils.getLastAnnotation(container) != null;
        if ((!ignoringAnnotations || !nodeCanHaveAnnotations || !hasAnnotations)) {
            // No special consideration required - perform simple range check.
            return container.containsWithinRange(other);
        }

        // If the node is contained, but it comes immediately after the annotations,
        // let's not consider it contained (i.e. it must be "strictly contained").
        return nodeWithoutConsideringAnnotations(container).getRange().get()
                .strictlyContains(other.getRange().get());
    }

}
