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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import jbayes.util.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class represents a node of a {@link BayesNet}.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class Node {

    private static Logger LOGGER = LoggerFactory.getLogger(Node.class);

    private final String name;
    private Distribution distribution;
    private NodeLinkType linkType = NodeLinkType.NONE;
    private List<String> levels;
    private String evidence;
    private BayesNet network;
    private List<Link> inLinks;
    private List<Link> outLinks;

    /**
     * Creates new {@link Node} instance.
     *
     * @param name Node name
     */
    public Node(String name) {
        Ensure.NotNull(name, "name");

        this.name = name;
    }

    /**
     * Creates new {@link Node} instance.
     *
     * @param name Node name
     * @param levels Levels
     */
    public Node(String name, String[] levels) {
        Ensure.NotNull(name, "name");
        Ensure.NotNull(levels, "levels");
        Ensure.GreaterThan(levels.length, 0, "levels should not be empty");

        this.name = name;
        this.setLevels(levels);
    }

    /**
     * Creates new {@link Node} instance.
     *
     * @param name Node name
     * @param levels Levels
     * @param distribution Discrete distribution
     */
    public Node(String name, String[] levels, Integer[] distribution) {
        Ensure.NotNull(name, "name");
        Ensure.NotNull(levels, "levels");
        Ensure.GreaterThan(levels.length, 0, "levels should not be empty");
        Ensure.NotNull(distribution, "distribution");
        Ensure.GreaterThan(distribution.length, 0, "distribution should not be empty");

        this.name = name;
        this.setDistribution(distribution);
        this.setLevels(levels);
    }

    /**
     * Creates new {@link Node} instance.
     *
     * @param name Node name
     * @param levels Levels
     * @param linkType Node link type
     */
    public Node(String name, String[] levels, NodeLinkType linkType) {
        Ensure.NotNull(name, "name");
        Ensure.NotNull(levels, "levels");
        Ensure.GreaterThan(levels.length, 0, "levels should not be empty");

        this.name = name;
        this.setLevels(levels);
        this.linkType = linkType;
    }

    /**
     * Creates new {@link Node} instance.
     *
     * @param name Node name
     * @param levels Levels
     * @param distribution Discrete distribution
     */
    public Node(String name, String[] levels, Double[] distribution) {
        Ensure.NotNull(name, "name");
        Ensure.NotNull(levels, "levels");
        Ensure.GreaterThan(levels.length, 0, "levels should not be empty");
        Ensure.NotNull(distribution, "distribution");
        Ensure.GreaterThan(distribution.length, 0, "distribution should not be empty");

        this.name = name;
        this.setDistribution(distribution);
        this.setLevels(levels);
    }

    /**
     * Returns the name of the node.
     *
     * @return Name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * Returns distribution of the node.
     *
     * <p>
     * If distribution has not been created yet, average distribution is
     * generated for all node levels. For instance, if node has discrete
     * probability and there are four levels, marginal probability of each level
     * will equal 0.25.
     * </p>
     *
     * @return Distribution of the node
     */
    public Distribution getDistribution() {
        if (distribution == null) {
            distribution = generateAverageDistributionFromLevels();
        }
        return distribution;
    }

    /**
     * Returns the level of the node that is set as evidence.
     *
     * @return Evidence of the node
     */
    public String getEvidence() {
        return evidence;
    }

    /**
     * Sets the specified level as new evidence to the node.
     *
     * <p>
     * After the evidence changed, the probability of evidence level and other
     * nodes is set respectively to 1.0 and 0.0.
     * </p>
     *
     * @param level Level as evidence
     * @throws IllegalArgumentException Level equals to null.
     */
    public void setEvidence(String level) {
        Ensure.NotNull(level, "evidence");

        if (level.equals(getEvidence())) {
            return;
        }

        LOGGER.trace("Evidence {} is set to Node {}", level, this.getName());

        int index = getLevelIndex(level);
        this.evidence = level;

        // set inference
        List<Double> inference = new ArrayList<>();
        for (int i = 0; i < getLevels().size(); i++) {
            inference.add(i == index ? 1.0 : 0.0);
        }
        setInference(inference);
    }

    /**
     * Clears evidence of the node.
     */
    public void clearEvidence() {
        this.evidence = null;
        this.setInference(null);
    }

    /**
     * Sets new distribution for the node.
     *
     * @param distribution New distribution.
     */
    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    /**
     * Sets new discrete distribution for the node.
     *
     * @param args
     */
    public final void setDistribution(Double... args) {
        this.distribution = DiscreteDistribution.FromArray(args);
    }

    /**
     * Sets new discrete distribution for the node.
     *
     * @param args
     */
    public final void setDistribution(Integer... args) {
        this.distribution = DiscreteDistribution.FromArray(args);
    }

    /**
     * Returns node link type.
     *
     * @return Node link type
     */
    public NodeLinkType getLinkType() {
        return linkType;
    }

    /**
     * Sets new node link type.
     *
     * @param linkType Node link type
     */
    public void setLinkType(NodeLinkType linkType) {
        this.linkType = linkType;
    }

    /**
     * Returns node levels.
     *
     * @return Node levels
     */
    public List<String> getLevels() {
        return levels != null ? levels : (levels = new ArrayList<>());
    }

    /**
     * Sets new node levels.
     *
     * @param levels Node levels
     */
    public void setLevels(List<String> levels) {
        this.levels = levels;
    }

    /**
     * Sets new node levels.
     *
     * @param levels Node levels
     */
    public final void setLevels(String... levels) {
        setLevels(Arrays.asList(levels));
    }

    /**
     * Returns the network that contains the node.
     *
     * @return {@link BayesNet}
     */
    public BayesNet getNetwork() {
        return network;
    }

    /**
     * Internal method for settings the network to the node.
     *
     * @param network {@link BayesNet}
     */
    void setNetwork(BayesNet network) {
        this.network = network;
    }

    /**
     * Returns all ingoing links.
     *
     * @return Ingoing links
     */
    public List<Link> getInLinks() {
        return inLinks != null ? inLinks : (inLinks = new ArrayList<>());
    }

    /**
     * Returns all outgoing links.
     *
     * @return Outgoing links
     */
    public List<Link> getOutLinks() {
        return outLinks != null ? outLinks : (outLinks = new ArrayList<>());
    }

    /**
     * Returns node inference.
     *
     * @return Node inference
     */
    public List<Double> getInference() {
        return getDistribution().getInference();
    }

    /**
     * Sets new node inference
     *
     * @param inference Node inference
     */
    public void setInference(List<Double> inference) {
        getDistribution().setInference(inference);
    }

    /**
     * Returns inference for the specified level.
     *
     * @param level Level
     * @return Inference for the specified level.
     */
    public Double getInference(String level) {
        return getInference().get(getLevelIndex(level));
    }

    /**
     * Returns the most probable level by inference possibility.
     *
     * @return The most probable level
     */
    public String getMostProbableLevel() {
        Ensure.NotNull(getInference(), "inference");

        int indexOfMax = 0;
        Double max = 0.0;
        for (int i = 0; i < getInference().size(); i++) {
            if (getInference().get(i) > max) {
                max = getInference().get(i);
                indexOfMax = i;
            }
        }
        return getLevels().get(indexOfMax);
    }

    /**
     * Returns index of the specified level
     *
     * @return index of the specified level
     * @throws IllegalArgumentException
     */
    private int getLevelIndex(String level) throws IllegalArgumentException {
        int index = getLevels().indexOf(level);
        if (index == -1) {
            throw new IllegalArgumentException(String.format("Node %s doesn't contain level with name %s", this.getName(), level));
        }
        return index;
    }

    private Distribution generateAverageDistributionFromLevels() {
        double value = 1.0 / (double) getLevels().size();
        List<Double> distr = new ArrayList<>();
        final int levelsCount = getLevels().size();
        for (int i = 0; i < levelsCount; i++) {
            distr.add(value);
        }
        return new DiscreteDistribution(distr);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + Objects.hashCode(this.network);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.network, other.network)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Node{" + "name=" + name + ", linkType=" + linkType + ", levels=" + levels + '}';
    }

}
