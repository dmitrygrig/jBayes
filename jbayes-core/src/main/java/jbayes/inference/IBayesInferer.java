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
package jbayes.inference;

import java.util.List;
import jbayes.core.BayesNet;
import jbayes.core.Node;

/**
 * The interface should be implemented by each inferer for a {@link BayesNet}.
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public interface IBayesInferer {

    /**
     * Returns underlying {@link BayesNet} instance.
     *
     * @return Underlying {@link BayesNet} instance
     */
    public BayesNet getNetwork();

    /**
     * Eliminates marginal probability for all nodes of the network.
     */
    void inferAllNodes();

    /**
     * Eliminates marginal probability for the specified node of the network.
     *
     * @param nodeName Node
     */
    void inferNode(String nodeName);

    /**
     * Eliminates marginal probability for the specified nodes of the network.
     *
     * @param nodes
     */
    void inferNodes(List<Node> nodes);

    /**
     * Eliminates marginal probability for the specified nodes of the network.
     *
     * @param nodes
     */
    void inferNodes(Node... nodes);

}
