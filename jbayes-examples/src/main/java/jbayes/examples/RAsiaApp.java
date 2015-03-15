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

import jbayes.r.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Represents modelling example of the network "Asia" using SingleR (gRain
 * package) and JRI.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class RAsiaApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(RAsiaApp.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        final String rHomeDir = System.getenv("R_HOME");
        R r = new R(rHomeDir);
        if (!r.init(args)) {
            return;
        }

        try {

            // import libs
            r.lib("gRbase");
            r.lib("gRain");
            r.lib("graph");
            r.lib("grid");
            r.lib("Rgraphviz");

            // create nodes
            r.eval("yn <- c(\"yes\", \"no\")");
            r.eval("a <- cptable(~ asia, values = c(1, 99), levels = yn)");
            r.eval("t.a <- cptable(~ tub + asia, values = c(5, 95, 1, 99), levels = yn)");
            r.eval("s <- cptable(~ smoke, values = c(5,5), levels = yn)");
            r.eval("l.s <- cptable(~ lung + smoke, values = c(1, 9, 1, 99), levels = yn)");
            r.eval("b.s <- cptable(~ bronc + smoke, values = c(6, 4, 3, 7), levels = yn)");
            r.eval("x.e <- cptable(~ xray + either, values = c(98, 2, 5, 95), levels = yn)");
            r.eval("d.be <- cptable(~ dysp + bronc + either, values = c(9, 1, 7, 3, 8, 2, 1, 9), levels = yn)");
            r.eval("e.lt <- ortable(~ either + lung + tub, levels = yn)");

            // compute cpt and init network
            r.eval("bn.asia.plist <- compileCPT(list(a, t.a, s, l.s, b.s, e.lt, x.e, d.be))");
            r.eval("bn.asia <- grain(bn.asia.plist)");

            // set evidences and query the network
            r.eval("setEvidence(bn.asia, c(\"asia\",\"xray\"), c(\"yes\", \"yes\"))");
            r.eval("querygrain(bn.asia, nodes=c(\"tub\"), type=\"marginal\")");
            
        } catch (Exception e) {
            LOGGER.error("Asia", e);
        }

    }
}
