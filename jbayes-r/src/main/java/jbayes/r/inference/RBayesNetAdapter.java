/*
 *
 * Copyright (C) 2015 Dmytro Grygorenko <dmitrygrig@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbayes.r.inference;

import com.google.common.base.Joiner;
import java.util.Collection;
import java.util.stream.Collectors;
import jbayes.core.BayesNet;
import jbayes.core.Node;
import jbayes.util.Ensure;

/**
 * The class represents adapter for {@link BayesNet} that allows its usage
 * in R environment.
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class RBayesNetAdapter {

    private final BayesNet network;
    private String alias;

    public RBayesNetAdapter(BayesNet network) {
        Ensure.NotNull(network, "network");
        this.network = network;
    }

    public RBayesNetAdapter(BayesNet network, String alias) {
        Ensure.NotNull(network, "network");
        this.network = network;
        this.alias = alias;
    }

    public BayesNet getNetwork() {
        return network;
    }

    /**
     * Returns network alias. If it doesn't specified yet, combines the name and
     * prefix "bn.".
     *
     * @return NetworkImpl alias
     */
    public String getAlias() {
        if (alias != null && !"".equals(alias)) {
            return alias;
        } else {
            alias = String.format("bn.%s", network.getName());
            return alias;
        }
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Returns comma-separated list of all node aliases
     *
     * @return
     */
    protected String getNodeAliases() {
        return Joiner.on(", ").join(
                network.getNodes().parallelStream()
                .map(x -> new RNodeAdapter(x))
                .map(x -> x.getAlias())
                .collect(Collectors.toList()));
    }

    /**
     * Returns comma-separated list of all node names
     *
     * @return
     */
    protected String getFormattedNodeNames() {
        return '"' + Joiner.on("\",\"").join(
                network.getNodes().parallelStream().
                map(x -> x.getName()).collect(Collectors.toList())) + '"';
    }

    /**
     * Returns comma-separated list of names for the specified nodes
     *
     * @param nodes
     * @return Comma-separated list of names
     */
    protected String getFormattedNodeNamesByNode(Collection<Node> nodes) {
        return '"' + Joiner.on("\",\"").join(
                network.getNodes().parallelStream().
                map(x -> x.getName()).collect(Collectors.toList())) + '"';
    }

    /**
     * Returns comma-separated list of names for the specified nodes
     *
     * @param nodeNames
     * @return Comma-separated list of names
     */
    protected String getFormattedNodeNamesByName(Collection<String> nodeNames) {
        return '"' + Joiner.on("\",\"").join(nodeNames) + '"';
    }

    /**
     * Returns comma-separated list of all node evidences
     *
     * @return
     */
    protected String getFormattedNodeEvidences() {
        return '"' + Joiner.on("\",\"").join(
                network.getNodes().parallelStream().
                map(x -> x.getEvidence()).collect(Collectors.toList())) + '"';
    }

    /**
     * Returns comma-separated list of evidences for the specified nodes
     *
     * @param nodes
     * @return Comma-separated list of evidences
     */
    protected String getFormattedNodeEvidences(Collection<Node> nodes) {
        return '"' + Joiner.on("\",\"").join(
                network.getNodes().parallelStream().
                map(x -> x.getEvidence()).collect(Collectors.toList())) + '"';
    }
}
