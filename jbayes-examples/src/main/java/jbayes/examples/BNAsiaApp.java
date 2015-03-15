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
package jbayes.examples;

import jbayes.core.BayesNet;
import jbayes.core.Node;
import jbayes.core.NodeLinkType;
import jbayes.inference.IBayesInferer;
import jbayes.r.R;
import jbayes.r.inference.RBayesInferer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Represents example of the bayes net "Asia".
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class BNAsiaApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(BNAsiaApp.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        final String rHomeDir = System.getenv("R_HOME");
        R rEngine = R.getInstance(rHomeDir);

        String[] levels = new String[]{"yes", "no"};

        Node asia = new Node("asia", levels, new Integer[]{1, 99});
        Node tub = new Node("tub", levels, new Integer[]{5, 95, 1, 99});
        Node smoke = new Node("smoke", levels, new Integer[]{5, 5});
        Node lung = new Node("lung", levels, new Integer[]{1, 9, 1, 99});
        Node bronc = new Node("bronc", levels, new Integer[]{6, 4, 3, 7});
        Node xray = new Node("xray", levels, new Integer[]{98, 2, 5, 95});
        Node dysp = new Node("dysp", levels, new Integer[]{9, 1, 7, 3, 8, 2, 1, 9});
        Node either = new Node("either", levels, NodeLinkType.OR);
        
        BayesNet net = new BayesNet("asia");
        net.addLink(asia, tub);
        net.addLink(smoke, bronc);
        net.addLink(smoke, lung);
        net.addLink(tub, either);
        net.addLink(lung, either);
        net.addLink(either, xray);
        net.addLink(either, dysp);
        net.addLink(bronc, dysp);

        net.setEvidence(asia, "yes");
        net.setEvidence(either, "yes");

        IBayesInferer inferer = new RBayesInferer(rEngine, net);
        inferer.inferAllNodes();

        LOGGER.info("xray[yes] = %f", xray.getInference("yes"));
        LOGGER.info("dysp[no] = %f", dysp.getInference("no"));
    }

}
