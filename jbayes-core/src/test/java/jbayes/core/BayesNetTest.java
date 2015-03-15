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

import jbayes.core.BayesNet;
import jbayes.core.Link;
import jbayes.core.Node;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class BayesNetTest {

    public BayesNetTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addNode method, of class BayesNet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddNode_Node_NetworkIsSet() throws Exception {
        System.out.println("addNode");
        Node node = mock(Node.class);
        BayesNet instance = new BayesNet();
        instance.addNode(node);

        verify(node).setNetwork(instance);
    }

    /**
     * Test of addNode method, of class BayesNet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddNode_Node_Added() throws Exception {
        System.out.println("addNode");
        Node node = mock(Node.class);
        BayesNet instance = new BayesNet();
        instance.addNode(node);

        assertTrue(instance.getNodes().contains(node));
    }

    /**
     * Test of addNode method, of class BayesNet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddNode_Node_AddedJustOne() throws Exception {
        System.out.println("addNode");
        Node node = mock(Node.class);
        BayesNet instance = new BayesNet();
        int before = instance.getNodes().size();
        instance.addNode(node);
        int after = instance.getNodes().size();
        assertEquals(1, after - before);
    }

    /**
     * Test of addLink method, of class BayesNet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddLink_Link_NetworkIsSet() throws Exception {
        System.out.println("addLink");
        Link node = mock(Link.class);
        BayesNet instance = new BayesNet();
        instance.addLink(node);

        verify(node).setNetwork(instance);
    }

    /**
     * Test of addLink method, of class BayesNet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddLink_Link_Added() throws Exception {
        System.out.println("addLink");
        Link node = mock(Link.class);
        BayesNet instance = new BayesNet();
        instance.addLink(node);

        assertTrue(instance.getLinks().contains(node));
    }

    /**
     * Test of addLink method, of class BayesNet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddLink_Link_AddedJustOne() throws Exception {
        System.out.println("addLink");
        Link node = mock(Link.class);
        BayesNet instance = new BayesNet();
        int before = instance.getLinks().size();
        instance.addLink(node);
        int after = instance.getLinks().size();
        assertEquals(1, after - before);
    }

    /**
     * Test of addLink method, of class BayesNet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddLink_2Nodes_NetworkIsSetForEachNode() throws Exception {
        System.out.println("addLink");
        Node a = new Node("a");
        Node b = new Node("b");
        BayesNet instance = new BayesNet();
        instance.addLink(a, b);

        assertEquals(instance, a.getNetwork());
        assertEquals(instance, b.getNetwork());
    }

    /**
     * Test of addLink method, of class BayesNet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddLink_2Nodes_2NodesAdded() throws Exception {
        System.out.println("addLink");
        Node a = new Node("a");
        Node b = new Node("b");
        BayesNet instance = new BayesNet();

        int before = instance.getNodes().size();
        instance.addLink(a, b);
        int after = instance.getNodes().size();

        assertEquals(2, after - before);
    }

    /**
     * Test of addLink method, of class BayesNet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddLink_2Nodes_1LinksAdded() throws Exception {
        System.out.println("addLink");
        Node a = new Node("a");
        Node b = new Node("b");
        BayesNet instance = new BayesNet();

        int before = instance.getLinks().size();
        instance.addLink(a, b);
        int after = instance.getLinks().size();

        assertEquals(1, after - before);
    }

    /**
     * Test of addLink method, of class BayesNet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddLink_2Nodes_LinkAddToOutLinksOfA() throws Exception {
        System.out.println("addLink");
        Node a = new Node("a");
        Node b = new Node("b");
        BayesNet instance = new BayesNet();

        int before = a.getOutLinks().size();
        instance.addLink(a, b);
        int after = a.getOutLinks().size();

        assertEquals(1, after - before);
    }

    /**
     * Test of addLink method, of class BayesNet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddLink_2Nodes_LinkAddToInLinksOfB() throws Exception {
        System.out.println("addLink");
        Node a = new Node("a");
        Node b = new Node("b");
        BayesNet instance = new BayesNet();

        int before = b.getInLinks().size();
        instance.addLink(a, b);
        int after = b.getInLinks().size();

        assertEquals(1, after - before);
    }

}
