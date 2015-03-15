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

import jbayes.core.Node;
import jbayes.core.DiscreteDistribution;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class NodeTest {

    public NodeTest() {
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
     * Test of clearEvidence method, of class Node.
     */
    @Test
    public void testClearEvidence_EvidenceIsNull() {
        System.out.println("clearEvidence");
        Node instance = new Node("test");
        instance.setLevels("yes", "no");
        instance.setDistribution(DiscreteDistribution.FromArray(1.0, 0.0, 1.0, 0.0));
        instance.setEvidence("yes");
        instance.clearEvidence();
        assertNull(instance.getEvidence());
    }

    /**
     * Test of setEvidence method, of class Node.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetEvidence_IncorrectLevel_IllegalArgumentException() {
        System.out.println("setEvidence");
        Node instance = new Node("test");
        instance.setLevels("yes", "no");
        instance.setDistribution(DiscreteDistribution.FromArray(1.0, 0.0, 1.0, 0.0));
        instance.setEvidence("error");
    }

    /**
     * Test of setEvidence method, of class Node.
     */
    @Test()
    public void testSetEvidence_CorrectInference() {
        System.out.println("setEvidence");
        Node instance = new Node("test");
        instance.setLevels("yes", "no");
        instance.setDistribution(DiscreteDistribution.FromArray(1.0, 0.0, 1.0, 0.0));
        instance.setEvidence("no");
        assertEquals(0.0, instance.getInference().get(0), TestConstants.DELTA);
        assertEquals(1.0, instance.getInference().get(1), TestConstants.DELTA);
    }

    /**
     * Test of getInference method, of class Node.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInferenceByLevel_IncorrectLevel_IllegalArgumentException() {
        System.out.println("getInference");
        Node instance = new Node("test");
        instance.setLevels("yes", "no");
        instance.setDistribution(DiscreteDistribution.FromArray(1.0, 0.0, 1.0, 0.0));
        instance.setEvidence("no");
        instance.getInference("error");
    }

    /**
     * Test of getInference method, of class Node.
     */
    @Test()
    public void testGetInferenceByLevel_CorrectValue() {
        System.out.println("getInference");
        Node instance = new Node("test");
        instance.setLevels("yes", "no");
        instance.setDistribution(DiscreteDistribution.FromArray(1.0, 0.0, 1.0, 0.0));
        instance.setEvidence("no");
        assertEquals(0.0, instance.getInference("yes"), TestConstants.DELTA);
        assertEquals(1.0, instance.getInference("no"), TestConstants.DELTA);
    }

}
