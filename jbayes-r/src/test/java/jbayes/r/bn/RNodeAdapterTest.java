/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbayes.r.bn;

import jbayes.r.inference.RNodeAdapter;
import jbayes.core.Link;
import jbayes.core.Node;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class RNodeAdapterTest {

    public RNodeAdapterTest() {
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

    @Test
    public void testGetAlias_SetInCtor_SameAlias() {
        RNodeAdapter instance = new RNodeAdapter(new Node("ab"), "testAlias");

        final String expResult = "testAlias";
        String result = instance.getAlias();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetAlias_NoIncomingLinks_FirstLetterOfTheName() {
        RNodeAdapter instance = new RNodeAdapter(new Node("ab"));

        final String expResult = "a";
        String result = instance.getAlias();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetAlias_OneIncomingLinkToNodeWithoutIncomingLinks_ConcatOfTwoAliases() {
        RNodeAdapter instance = new RNodeAdapter(new Node("ab"));
        instance.getNode().getInLinks().add(new Link(new Node("bc"), instance.getNode()));

        final String expResult = "a.b";
        String result = instance.getAlias();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetAlias_OneIncomingLinkToNodeWithIncomingLink_ConcatOfTwoAliases() {
        RNodeAdapter instance = new RNodeAdapter(new Node("ab"));
        Node b = new Node("bc");
        b.getInLinks().add(new Link(new Node("cd"), b));
        instance.getNode().getInLinks().add(new Link(b, instance.getNode()));

        final String expResult = "a.b";
        String result = instance.getAlias();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetAlias_TwoIncomingLinks_ConcatOfThreeAliases() {
        RNodeAdapter instance = new RNodeAdapter(new Node("ab"));
        instance.getNode().getInLinks().add(new Link(new Node("bc"), instance.getNode()));
        instance.getNode().getInLinks().add(new Link(new Node("cd"), instance.getNode()));

        final String expResult = "a.bc";
        String result = instance.getAlias();

        assertEquals(expResult, result);
    }

}
