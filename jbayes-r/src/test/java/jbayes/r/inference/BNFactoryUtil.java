/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbayes.r.inference;

import jbayes.core.BayesNet;
import jbayes.core.Node;

/**
 *
 * @author Dmytro
 */
public class BNFactoryUtil {
    public static BayesNet createWeatherBN() {
        final String[] levels = new String[]{"T", "F"};
        Node rain = new Node("rain", levels, new Integer[]{20, 80});
        Node sprinkler = new Node("sprinkler", levels, new Integer[]{1, 99, 40, 60});
        Node grassWet = new Node("grasswet", levels, new Integer[]{99, 1, 80, 20, 90, 10, 0, 100});
        BayesNet bn = new BayesNet("weather");
        bn.addLink(rain, sprinkler);
        bn.addLink(sprinkler, grassWet);
        bn.addLink(rain, grassWet);

        return bn;
    }
}
