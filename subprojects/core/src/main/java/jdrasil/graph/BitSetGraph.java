/*
 * Copyright (c) 2016-present, Max Bannach, Sebastian Berndt, Thorsten Ehlers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
 * OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package jdrasil.graph;

import java.util.*;

/**
 * A BitSetGraph stores a graph (with arbitrary vertices) as bitwise adjacency matrix, i.e., as array of BitSets where
 * the i'th BitSet corresponds to the i'th row of the adjacency matrix. This comes with all the advantages and disadvantages
 * of an adjacency matrix.
 *
 * The crucial advantage is that the graph is compact and that many operations can be performed quickly on the bit level.
 * In particular, dynamic programming over subgraphs can be implemented efficiently, as subgraphs are also just BitSets
 * that can efficiently be hashed.
 *
 * @param <T> the type of vertices the original graph has
 * @author Max Bannach
 */
public class BitSetGraph<T extends Comparable<T>> {

    /** The underling "real" graph. */
    private final Graph<T> graph;

    /** The number of vertices in the graph. */
    private final int n;

    /** A bijection from V to {0,...,n-1} */
    private final Map<T, Integer> vToInt;
    private final Map<Integer, T> intToV;

    /** The graph as array of BitSets (aka. bit adjacency matrix). */
    private final BitSet[] bitSetGraph;

    /**
     * Creates the BitSetGraph from the given graph by computing a bijection from the vertices to {0,...,n-1}
     * and storing edges in an adjacency matrix represented by an array of BitSets.
     * @param graph
     */
    public BitSetGraph(Graph<T> graph) {
        // parse given graph
        this.graph = graph;
        this.n = graph.getCopyOfVertices().size();

        // compute the bijection
        this.vToInt = new HashMap<>();
        this.intToV = new HashMap<>();
        int i = 0;
        for (T v : graph) {
            vToInt.put(v, i);
            intToV.put(i, v);
            i++;
        }

        // create the bitgraph
        bitSetGraph = new BitSet[n];
        for (int v = 0; v < n; v++) bitSetGraph[v] = new BitSet();
        for (T v : graph) {
            int x = vToInt.get(v);
            for (T w : graph.getNeighborhood(v)) {
                int y = vToInt.get(w);
                bitSetGraph[x].set(y);
            }
        }
    }

    /**
     * Getter for the original graph used to create this BitSet graph.
     * @return
     */
    public Graph<T> getGraph() {
        return this.graph;
    }

    /**
     * Getter for the number of vertices in the graph.
     * @return
     */
    public int getN() {
        return this.n;
    }

    /**
     * Getter for the BitSet graph: an array of n BitSets where the i'th BitSet corresponds to the i'th row
     * of the adjacency matrix of the graph.
     * @return
     */
    public BitSet[] getBitSetGraph() {
        return this.bitSetGraph;
    }

    /**
     * Get the used mapping that maps the vertices to {0,...,n-1}.
     * @return
     */
    public Map<T, Integer> getVToInt() {
        return this.vToInt;
    }

    /**
     * Get the reverse mapping, i.e., from {0,...,n-1} to the vertices of the original graph.
     * @return
     */
    public Map<Integer, T> getIntToV() {
        return this.intToV;
    }

    /**
     * Computes a set of "real" vertices to the corresponding BitSet.
     * @param S
     * @return
     */
    public Set<T> getVertexSet(BitSet S) {
        Set<T> vertexSet = new HashSet<T>();
        for (int v = S.nextSetBit(0); v >= 0; v = S.nextSetBit(v+1)) {
            vertexSet.add(intToV.get(v));
        }
        return vertexSet;
    }

    /**
     * Computes a corresponding BitSet to a "real" vertex set.
     * @param vertexSet
     * @return
     */
    public BitSet getBitSet(Set<T> vertexSet) {
        BitSet S = new BitSet();
        for (T v : vertexSet) S.set(vToInt.get(v));
        return S;
    }

    /**
     * Gets the interior border of S, i.e., all vertices v in S that have at least one neighbor outside of S.
     * @param S
     * @return
     */
    public BitSet interiorBorder(BitSet S) {
        BitSet border = new BitSet();
        BitSet outside = (BitSet) S.clone();
        outside.flip(0,n);
        for (int v = S.nextSetBit(0); v >= 0; v = S.nextSetBit(v+1)) {
            if (bitSetGraph[v].intersects(outside)) border.set(v);
        }
        return border;
    }

    /**
     * Gets the exterior border of S, i.e., all vertices v in G\S that have at least one neighbor in S.
     * @param S
     * @return
     */
    public BitSet exteriorBorder(BitSet S) {
        BitSet border = new BitSet();
        BitSet outside = (BitSet) S.clone();
        outside.flip(0,n);
        for (int v = S.nextSetBit(0); v >= 0; v = S.nextSetBit(v+1)) {
            if (bitSetGraph[v].intersects(outside)) border.or(bitSetGraph[v]);
        }
        border.andNot(S);
        return border;
    }

    /**
     * Saturates the subgraph \(S\) by adding all vertices \(v\in N(S)\) of \(V\setminus S\) that have all neighbors in \(S\) or \(N(S)\).
     * @param S
     */
    public void saturate(BitSet S) {
        BitSet neighbors = exteriorBorder(S);
        BitSet Sprime = (BitSet) S.clone();
        Sprime.or(neighbors);
        for (int v = neighbors.nextSetBit(0); v >= 0; v = neighbors.nextSetBit(v+1)) {
            BitSet tmp = (BitSet) bitSetGraph[v].clone();
            tmp.andNot(Sprime);
            if (tmp.cardinality() == 0) S.set(v);
        }
    }

    /**
     * Finds and returns an arbitrary vertex \(v\in N(S)\) that has only neighbors in \(S\) or \(N(S)\). Returns -1 if no such
     * vertex exists.
     * @param S
     * @return
     */
    public int absorbable(BitSet S) {
        BitSet neighbors = exteriorBorder(S);
        BitSet outside = (BitSet) S.clone();
        outside.or(neighbors);
        outside.flip(0,n);
        for (int v = neighbors.nextSetBit(0); v >= 0; v = neighbors.nextSetBit(v+1)) {
            if (!bitSetGraph[v].intersects(outside)) return v;
        }
        return -1;
    }

    /**
     * Compute the connected components of G[V\S], S will not be included in any of these components.
     * @param S
     * @return
     */
    public List<BitSet> separate(BitSet S) {
        List<BitSet> components = new ArrayList<>(5);
        BitSet visited = new BitSet();
        visited.or(S);

        // start dfs on each vertex
        Stack<Integer> stack = new Stack<>();
        for (int s = 0; s < n; s++) {
            if (visited.get(s)) continue; // old component
            BitSet component = new BitSet();
            component.set(s);
            visited.set(s);
            stack.add(s);
            while (!stack.isEmpty()) {
                int v = stack.pop();
                for (int w = 0; w < n; w++) {
                    if (!bitSetGraph[v].get(w)) continue;
                    if (visited.get(w)) continue;
                    component.set(w);
                    visited.set(w);
                    stack.push(w);
                }
            }
            components.add(component);
        }

        // done
        return components;
    }

    /**
     * Test if the given set of vertices is a potential maximal clique, i.e., a maximal clique in some minimal triangulation
     * of the graph.
     * This method uses the local characterisation of potential maximal cliques found by Bouchitte and Todinca "Treewidth
     * and Minimum Fill-In: Grouping th Minimal Separators".
     * @param S
     * @return
     */
    public boolean isPotentialMaximalClique(BitSet S) {
        List<BitSet> components = separate(S);

        // Test 1: S can not be maximal potential clique, if there is a component C with N(C)=S
        for (BitSet C : components) {
            if (exteriorBorder(C).cardinality() == S.cardinality()) return false; // |N(C)|=|S| vs N(C)=S
        }

        // Test 2: for every non-edge {u,v} in S, there must be a component C adjacent to both, u and v
        for (int v = S.nextSetBit(0); v >= 0; v = S.nextSetBit(v+1)) {
            for (int w = S.nextSetBit(v+1); w >= 0; w = S.nextSetBit(w+1)) {
                if (bitSetGraph[v].get(w)) continue;
                boolean completeable = false;
                for (BitSet C : components) {
                    if (C.intersects(bitSetGraph[v]) && C.intersects(bitSetGraph[w])) {
                        completeable = true;
                        break;
                    }
                }
                if (!completeable) return false;
            }
        }

        return true;
    }

}
