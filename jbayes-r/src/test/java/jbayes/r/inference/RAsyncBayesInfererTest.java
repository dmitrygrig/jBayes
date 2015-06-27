/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbayes.r.inference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbayes.core.BayesNet;
import jbayes.r.R;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Dmytro
 */
public class RAsyncBayesInfererTest {

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
    public void test_inferMostProbableLevelsAsync_calculates_correctly_inference() throws Exception {
        BayesNet bn = BNFactoryUtil.createWeatherBN();
        bn.getNodeByName("grasswet").setEvidence("T");
        try (RAsyncBayesInferer inferer = new RAsyncBayesInferer(r, bn)) {

            List<Integer> levels = inferer.inferMostProbableLevelsAsync("rain", "sprinkler").get();
            final int expectedRain = 1;
            final int expectedSprinkler = 0;
            final int resultRain = levels.get(0);
            final int resultSprinkler = levels.get(1);

            assertEquals(expectedRain, resultRain);
            assertEquals(expectedSprinkler, resultSprinkler);
        }
    }

    @Test
    public void test_inferMostProbableLevelsAsync_calculates_correctly_inference_for_multiple_bayes_nets() throws Exception {

        BayesNet bn = BNFactoryUtil.createWeatherBN();
        bn.getNodeByName("grasswet").setEvidence("T");
        try (RAsyncBayesInferer inferer = new RAsyncBayesInferer(r, bn)) {
            List<Future<List<Integer>>> results = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                results.add(inferer.inferMostProbableLevelsAsync("rain", "sprinkler"));
            }

            for (Future<List<Integer>> result : results) {
                List<Integer> levels = result.get();
                final int expectedRain = 1;
                final int expectedSprinkler = 0;
                final int resultRain = levels.get(0);
                final int resultSprinkler = levels.get(1);

                assertEquals(expectedRain, resultRain);
                assertEquals(expectedSprinkler, resultSprinkler);

            }
        }
    }

}
