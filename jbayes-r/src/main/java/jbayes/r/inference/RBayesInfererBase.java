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
package jbayes.r.inference;

import jbayes.util.Ensure;
import jbayes.core.BayesNet;
import jbayes.r.R;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public abstract class RBayesInfererBase  {

    private final R r;
    private final RBayesNetAdapter netAdapter;
    private boolean initialized = false;

    protected RBayesInfererBase(R r, RBayesNetAdapter netAdapter) {
        Ensure.NotNull(r, "R Engine");
        Ensure.NotNull(netAdapter, "queryableNetwork");

        this.r = r;
        this.netAdapter = netAdapter;
    }

    public boolean isInitialized() {
        return initialized;
    }

    protected void initializeIfNecessary() {
        if (!isInitialized()) {
            initialize();
        }
    }

    protected void initialize() {
        importLibs();
        initNetwork();
        this.initialized = true;
    }

    private void importLibs() {
        getR().lib("gRbase");
        getR().lib("gRain");
        getR().lib("graph");
        getR().lib("grid");
    }

    private void initNetwork() {
        // create each node in R Env
        netAdapter.getNetwork().getNodes().stream()
                .map(node -> new RNodeAdapter(node))
                .map(qnode -> qnode.getRCreateCmd())
                .forEach(rCmd -> r.eval(rCmd));

        // compute CPT
        String computeCmd = String.format("%s <- grain(compileCPT(list(%s)))",
                netAdapter.getAlias(),
                netAdapter.getNodeAliases());
        getR().eval(computeCmd);
    }

    /**
     * Gets R engine, used by the network.
     *
     * @return R engine
     */
    protected R getR() {
        return r;
    }

    /**
     * Returns underlying {@link RBayesNetAdapter} instance.
     *
     * @return Underlying {@link RBayesNetAdapter} instance
     */
    protected RBayesNetAdapter getQueryableNetwork() {
        return netAdapter;
    }

    /**
     * Returns underlying {@link BayesNet} instance.
     *
     * @return Underlying {@link BayesNet} instance
     */
    public BayesNet getNetwork() {
        return this.getQueryableNetwork().getNetwork();
    }
}
