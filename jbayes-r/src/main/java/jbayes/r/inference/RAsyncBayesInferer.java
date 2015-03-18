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

import jbayes.r.R;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import jbayes.core.BayesNet;
import jbayes.core.Node;
import jbayes.util.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class RAsyncBayesInferer extends RBayesInfererBase implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RAsyncBayesInferer.class);

    private ExecutorService executorService;
    private static AtomicLong executionCount = new AtomicLong(0);
    
     public RAsyncBayesInferer(R r, BayesNet network) {
        this(r, new RBayesNetAdapter(network));
    }

    public RAsyncBayesInferer(R r, RBayesNetAdapter queryableNetwork) {
        super(r, queryableNetwork);
    }

    public RAsyncBayesInferer(ExecutorService executorService, R r, RBayesNetAdapter queryableNetwork) {
        super(r, queryableNetwork);
        this.executorService = executorService;
    }

    public ExecutorService getExecutorService() {
        return executorService != null ? executorService : (executorService = Executors.newCachedThreadPool());
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Returns the most probable level for each of the specified nodes.
     *
     * @param nodes
     * @return The most probable level for each of the specified nodes
     */
    public Future<List<Integer>> inferMostProbableLevelsAsync(List<String> nodes) {
        Ensure.NotNull(nodes, "nodes");

        initializeIfNecessary();

        final List<String> finalNodes = nodes;
        final String bntemp = getQueryableNetwork().getAlias() + ".temp." + executionCount.get();
        final String bnres = getQueryableNetwork().getAlias() + ".res." + executionCount.get();
        executionCount.addAndGet(1);

        List<Node> nodeWithEvidences = getQueryableNetwork().getNetwork().getNodes()
                .parallelStream()
                .filter(x -> x.getEvidence() != null)
                .collect(Collectors.toList());

        // set all evidences: setEvidence(bn.asia, c("asia","either"), c("yes", "yes"))
        final String evidenceCmd = String.format("%s <- setEvidence(%s, c(%s), c(%s))",
                bntemp,
                getQueryableNetwork().getAlias(),
                getQueryableNetwork().getFormattedNodeNamesByNode(nodeWithEvidences),
                getQueryableNetwork().getFormattedNodeEvidences(nodeWithEvidences));

        Callable<List<Integer>> callable = () -> {
            try {
                List<Integer> resultList = new ArrayList<>();

                getR().eval(evidenceCmd);

                // execute query: querygrain(bn1, nodes=c("smoke"), type="marginal")$smoke
                String queryCmd = String.format("%s <- querygrain(%s, nodes=c(%s), type=\"marginal\")",
                        bnres,
                        bntemp,
                        getQueryableNetwork().getFormattedNodeNamesByName(finalNodes));
                getR().eval(queryCmd);

                for (String node : finalNodes) {
                    String resCmd = String.format("%s$%s", bnres, node);
                    double[] result = getR().eval(resCmd).asDoubleArray();
                    int maxIndex;
                    if (result == null) {
                        // if result is null, it means that the evidence 
                        // was already set for this node
                        // therefore level should be obtained from joint probability
                        String jointProbCmd = String.format("querygrain(%s, nodes=c(\"%s\"), type=\"joint\")",
                                bntemp,
                                node);
                        double[] jointProbResult = getR().eval(jointProbCmd).asDoubleArray();
                        maxIndex = getIndexOfMaxElem(jointProbResult);
                    } else {
                        maxIndex = getIndexOfMaxElem(result);
                    }
                    resultList.add(maxIndex);
                }

                return resultList;
            } catch (Exception e) {
                LOGGER.error("queryMostProbableAsync", e);
                return null;
            } finally {
                // free resources
                getR().eval(String.format("rm(%s)", bntemp));
                getR().eval(String.format("rm(%s)", bnres));
            }

        };

        Future<List<Integer>> future = getExecutorService().submit(callable);
        return future;
    }

    /**
     * Returns the most probable level for each of the specified nodes.
     *
     * @param nodes
     * @return The most probable level for each of the specified nodes
     */
    public Future<List<Integer>> inferMostProbableLevelsAsync(String... nodes) {
        return this.inferMostProbableLevelsAsync(Arrays.asList(nodes));
    }

    @Override
    public void close() throws IOException {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    private int getIndexOfMaxElem(double[] arr) {
        Ensure.GreaterThan(arr.length, 0, "array should not be empty");
        int maxIndex = 0;
        double maxResult = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (maxResult < arr[i]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
