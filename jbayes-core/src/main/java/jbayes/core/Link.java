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

import java.util.Objects;
import jbayes.util.Ensure;

/**
 * The class represents a link between two nodes of a {@link BayesNet}.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class Link {

    private final Node parent;
    private final Node child;
    private BayesNet network;

    /**
     * Creates new instance of {@link Link} using the specified nodes.
     *
     * @param parent Parent node
     * @param child Child node
     */
    public Link(Node parent, Node child) {
        Ensure.NotNull(parent, "parent");
        Ensure.NotNull(child, "child");
        this.parent = parent;
        this.child = child;
    }

    /**
     * Returns parent node (tail).
     *
     * @return Parent Node (tail)
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Returns child node (head).
     *
     * @return Child Node (head)
     */
    public Node getChild() {
        return child;
    }

    /**
     * Returns the network that contains the link.
     *
     * @return {@link BayesNet}
     */
    public BayesNet getNetwork() {
        return network;
    }

    /**
     * Internal method for settings the network to the link.
     *
     * @param network {@link BayesNet}
     */
    void setNetwork(BayesNet network) {
        this.network = network;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.parent);
        hash = 59 * hash + Objects.hashCode(this.child);
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
        final Link other = (Link) obj;
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.child, other.child)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Link{" + "parent=" + parent + ", child=" + child + '}';
    }

}
