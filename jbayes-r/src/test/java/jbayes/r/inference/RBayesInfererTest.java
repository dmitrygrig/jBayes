/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbayes.r.inference;

import java.util.List;
import jbayes.core.BayesNet;
import jbayes.core.Node;
import jbayes.inference.IBayesInferer;
import jbayes.r.R;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Dmytro
 */
public class RBayesInfererTest {

    private final static Double DELTA = 1e-15;
    private static R r;

    @Before
    public void setUp() throws Exception {
        r = R.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        r.clearAll();
        r = null;
    }

    @Test
    public void test_infer_node_calculates_correctly_inference() {
        BayesNet bn = BNFactoryUtil.createWeatherBN();
        bn.getNodeByName("grasswet").setEvidence("T");
        IBayesInferer inferer = new RBayesInferer(r, bn);

        inferer.inferNode("rain");
        final double expected = 0.3577;
        final double result = bn.getNodeByName("rain").getInference("T");

        assertEquals(expected, result, 1e-4);
    }

    @Test
    public void test_infer_node_does_not_change_inference_for_other_nodes() {
        BayesNet bn = BNFactoryUtil.createWeatherBN();
        bn.getNodeByName("grasswet").setEvidence("T");
        IBayesInferer inferer = new RBayesInferer(r, bn);

        inferer.inferNode("rain");
        final List<Double> result = bn.getNodeByName("sprinkler").getInference();

        assertNull(result);
    }

    @Test
    public void test_infer_all_nodes_calculates_correctly_inference_for_all_nodes() {
        BayesNet bn = BNFactoryUtil.createWeatherBN();
        bn.getNodeByName("grasswet").setEvidence("T");
        IBayesInferer inferer = new RBayesInferer(r, bn);

        inferer.inferAllNodes();
        final double expectedRain = 0.3577;
        final double expectedSprinkler = 0.6467;
        final double resultRain = bn.getNodeByName("rain").getInference("T");
        final double resultSprinkler = bn.getNodeByName("sprinkler").getInference("T");

        assertEquals(expectedRain, resultRain, 1e-4);
        assertEquals(expectedSprinkler, resultSprinkler, 1e-4);
    }

}
