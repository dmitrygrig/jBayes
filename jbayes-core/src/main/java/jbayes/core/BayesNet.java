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
package jbayes.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jbayes.util.Ensure;

/**
 * The class represents a Bayesian Network.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class BayesNet {

    private String name;
    private List<Node> nodes;
    private List<Link> links;

    /**
     * Creates new {@link BayesNet} instance.
     */
    public BayesNet() {
    }

    /**
     * Creates new {@link BayesNet} instance.
     *
     * @param name Name of the network
     */
    public BayesNet(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the network.
     *
     * @return Name of the network
     */
    public String getName() {
        return name;
    }

    /**
     * Sets new name for the network.
     *
     * @param name Name of the network
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns list of network nodes.
     *
     * @return List of nodes.
     */
    public List<Node> getNodes() {
        return nodes != null ? nodes : (nodes = new ArrayList<>());
    }

    /**
     * Sets the list of network nodes.
     *
     * @param nodes List of network nodes
     */
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * Returns the list of network links between nodes.
     *
     * @return List of network links between nodes.
     */
    public List<Link> getLinks() {
        return links != null ? links : (links = new ArrayList<>());
    }

    /**
     * Sets the list of network links between nodes
     *
     * @param links List of network links between nodes
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    /**
     * Adds specified node to the network.
     *
     * @param node
     * @throws IllegalStateException If node exists already in the network
     */
    public void addNode(Node node) {
        Ensure.IsFalse(getNodes().contains(node), String.format("Node %s already exists in the network", node));
        ensureNodeNameNotExists(node.getName());

        node.setNetwork(this);
        getNodes().add(node);
    }

    /**
     * Adds range of nodes to the network.
     *
     * @param nodes List of nodes
     * @throws IllegalStateException If one of the nodes exists already in the
     * network
     */
    public void addNodes(Node... nodes) {
        for (Node node : nodes) {
            addNode(node);
        }
    }

    /**
     * Adds specified link if it doesn't exist in the network yet.
     *
     * @param link
     * @throws IllegalStateException Link exists already in the network
     * @throws IllegalStateException If one of the nodes doesn't exist in the
     * network yet.
     */
    public void addLink(Link link) {
        Ensure.IsFalse(getLinks().contains(link), String.format("Link %s already exists in the network", link));
        Ensure.IsTrue(getNodes().contains(link.getParent()), "Parent node doesn't exist in the network yet.");
        Ensure.IsTrue(getNodes().contains(link.getChild()), "Child node doesn't exist in the network yet.");

        link.setNetwork(this);
        getLinks().add(link);
    }

    private void addLinkWithoutCheck(Link link) {
        link.setNetwork(this);
        getLinks().add(link);
    }

    /**
     * Adds two nodes if they don't belong to the network and adds link between
     * them.
     *
     * @param a Node with outgoing link
     * @param b Node with incoming link
     * @throws IllegalStateException If one of the two nodes exists already in
     * the network
     */
    public void addLink(Node a, Node b) {
        addNodeIfNecessary(a);
        addNodeIfNecessary(b);

        Link link = new Link(a, b);
        addLinkWithoutCheck(link);

        a.getOutLinks().add(link);
        b.getInLinks().add(link);
    }

    private void addNodeIfNecessary(Node node) {
        if (!getNodes().contains(node)) {
            addNode(node);
        }
    }

    /**
     * Sets evidence for the specified node
     *
     * @param node Node
     * @param evidence Evidence (should be a value from node levels)
     */
    public void setEvidence(Node node, String evidence) {
        node.setEvidence(evidence);
    }

    /**
     * Sets evidence for the specified node
     *
     * @param nodeName Node name
     * @param evidence Evidence (should be a value from node levels)
     */
    public void setEvidence(String nodeName, String evidence) {
        this.setEvidence(getNodeByName(nodeName), evidence);
    }

    /**
     * Clears evidences from all nodes.
     */
    public void clearEvidences() {
        getNodes()
                .parallelStream()
                .forEach(x -> x.clearEvidence());
    }

    /**
     * Returns node by its name.
     *
     * @param name
     * @return Node that has specified name
     */
    public Node getNodeByName(String name) {
        return getNodes()
                .parallelStream()
                .filter(x -> (name == null ? x.getName() == null : name.equals(x.getName())))
                .findFirst()
                .get();
    }

    protected void ensureNodeNameNotExists(final String nodeName) {
        List<Node> sameNameOrAlias = getNodes()
                .stream()
                .filter(x -> x.getName().equals(nodeName))
                .collect(Collectors.toList());
        Ensure.AreEquals(sameNameOrAlias.size(), 0, "Node with the same name or alias is already presented in the network.");
    }

    @Override
    public String toString() {
        return "BayesNet{" + "name=" + name + '}';
    }

}
