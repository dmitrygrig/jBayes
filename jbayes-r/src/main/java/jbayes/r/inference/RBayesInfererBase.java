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
import jbayes.util.Ensure;
import jbayes.core.BayesNet;
import jbayes.core.Node;
import jbayes.core.NodeLinkType;
import jbayes.r.R;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public abstract class RBayesInfererBase {

    private final R r;
    private final RBayesNetAdapter netAdapter;
    private boolean initialized = false;

    protected RBayesInfererBase(R r, RBayesNetAdapter netAdapter) {
        Ensure.NotNull(r, "R Engine");
        Ensure.NotNull(netAdapter, "queryableNetwork");

        this.r = r;
        this.netAdapter = netAdapter;
    }

    public boolean isInitialized() {
        return initialized;
    }

    protected void initializeIfNecessary() {
        if (!isInitialized()) {
            initialize();
        }
    }

    protected void initialize() {
        importLibs();
        initNetwork();
        this.initialized = true;
    }

    private void importLibs() {
        getR().lib("gRbase");
        getR().lib("gRain");
//        getR().lib("graph");
//        getR().lib("grid");
    }

    private void initNetwork() {
        // create each node in R Env
        netAdapter.getNetwork().getNodes().stream()
                .map(node -> new RNodeAdapter(node))
                .map(qnode -> qnode.getRCreateCmd())
                .forEach(rCmd -> r.eval(rCmd));

        // compute CPT
        String computeCmd = String.format("%s <- grain(compileCPT(list(%s)))",
                netAdapter.getAlias(),
                netAdapter.getNodeAliases());
        getR().eval(computeCmd);
    }

    /**
     * Gets R engine, used by the network.
     *
     * @return R engine
     */
    protected R getR() {
        return r;
    }

    /**
     * Returns underlying {@link RBayesNetAdapter} instance.
     *
     * @return Underlying {@link RBayesNetAdapter} instance
     */
    protected RBayesNetAdapter getQueryableNetwork() {
        return netAdapter;
    }

    /**
     * Returns underlying {@link BayesNet} instance.
     *
     * @return Underlying {@link BayesNet} instance
     */
    public BayesNet getNetwork() {
        return this.getQueryableNetwork().getNetwork();
    }

    public static class RBayesNetAdapter {

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
         * Returns network alias. If it doesn't specified yet, combines the name
         * and prefix "bn.".
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
                    getNetwork().getNodes().parallelStream()
                    .map(x -> new RNodeAdapter(x))
                    .map(x -> x.getAlias())
                    .collect(Collectors.toList()));
        }

        /**
         * Returns comma-separated list of names for the specified nodes
         *
         * @param nodes
         * @return Comma-separated list of names
         */
        protected String getFormattedNodeNamesByNodes(Collection<Node> nodes) {
            return '"' + Joiner.on("\",\"").join(
                    nodes.parallelStream().
                    map(x -> x.getName()).collect(Collectors.toList())) + '"';
        }

        /**
         * Returns comma-separated list of names for the specified nodes
         *
         * @param nodeNames
         * @return Comma-separated list of names
         */
        protected String getFormattedNodeNamesByNodeNames(Collection<String> nodeNames) {
            return '"' + Joiner.on("\",\"").join(nodeNames) + '"';
        }

        /**
         * Returns comma-separated list of evidences for the specified nodes
         *
         * @param nodes
         * @return Comma-separated list of evidences
         */
        protected String getFormattedNodeEvidences(Collection<Node> nodes) {
            return '"' + Joiner.on("\",\"").join(
                    nodes.parallelStream().
                    map(x -> x.getEvidence()).collect(Collectors.toList())) + '"';
        }
    }

    public static class RNodeAdapter {

        private final Node node;
        private String alias;

        public RNodeAdapter(Node node) {
            this.node = node;
        }

        public RNodeAdapter(Node node, String alias) {
            this.node = node;
            this.alias = alias;
        }

        public Node getNode() {
            return node;
        }

        /**
         * Returns node alias.
         *
         * If alias is already exists, return it. If alias doesn't exist and
         * there are no incoming links then returns first letter of the name,
         * otherwise adds a point and first letter of each incoming node.
         *
         * Example: ~ asia -> a ~ tub + asia -> t.a
         *
         * @return Node alias
         */
        public String getAlias() {
            if (alias != null && !"".equals(alias)) {
                return alias;
            } else {
                alias = node.getInLinks().isEmpty()
                        ? String.valueOf(node.getName().toCharArray()[0])
                        : String.format("%s.%s",
                                String.valueOf(node.getName().toCharArray()[0]),
                                Joiner.on("").join(node.getInLinks()
                                        .stream()
                                        .map(x -> x.getParent())
                                        .map(x -> String.valueOf(x.getName().toCharArray()[0]))
                                        .collect(Collectors.toList())));
                return alias;
            }
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        /**
         * Returns R command that creates representation of this object in R.
         *
         * @return Command in R for representation of this object.
         */
        public String getRCreateCmd() {
            // cptable
            String table = node.getLinkType() == NodeLinkType.NONE
                    ? "cptable"
                    : node.getLinkType() == NodeLinkType.AND
                            ? "andtable" : "ortable";

            // dysp + bronc + either
            String vpar = node.getInLinks().isEmpty() ? node.getName()
                    : String.format("%s + %s", node.getName(), Joiner.on(" + ")
                            .join(node.getInLinks()
                                    .stream()
                                    .map(x -> x.getParent().getName())
                                    .collect(Collectors.toList())));

            // 5.0, 5.0, 9, 1
            String distr = node.getLinkType() == NodeLinkType.NONE
                    ? node.getDistribution().convertToString() : null;

            // "yes", "no"
            String stringLevels = Joiner.on(",")
                    .join(node
                            .getLevels()
                            .stream()
                            .map(x -> String.format("\"%s\"", x))
                            .collect(Collectors.toList()));

            return node.getLinkType() == NodeLinkType.NONE
                    ? String.format("%s <- %s(~ %s, values = c(%s), levels = c(%s))", this.getAlias(), table, vpar, distr, stringLevels)
                    : String.format("%s <- %s(~ %s, levels = c(%s))", this.getAlias(), table, vpar, stringLevels);
        }

    }
}
