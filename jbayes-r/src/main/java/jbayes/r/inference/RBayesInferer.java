/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbayes.r.inference;

import jbayes.inference.IBayesInferer;
import com.google.common.primitives.Doubles;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import jbayes.core.BayesNet;
import jbayes.core.Node;
import jbayes.r.R;
import jbayes.util.Ensure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class RBayesInferer extends RBayesInfererBase implements IBayesInferer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RBayesInferer.class);

    public RBayesInferer(R r, BayesNet network) {
        this(r, new RBayesNetAdapter(network));
    }

    public RBayesInferer(R r, RBayesNetAdapter queryableNetwork) {
        super(r, queryableNetwork);
    }
    
    /**
     * Computes marginal probability for the specified nodes.
     *
     * @param nodes
     */
    @Override
    public void inferNodes(Collection<Node> nodes) {
        Ensure.NotNull(nodes, "nodes");

        initializeIfNecessary();

        try {

            List<Node> finalNodes = new LinkedList<>(nodes);

            List<Node> nodeWithEvidences = getQueryableNetwork().getNetwork().getNodes()
                    .stream()
                    .filter(x -> x.getEvidence() != null)
                    .collect(Collectors.toList());

            // set all evidences: setEvidence(bn.asia, c("asia","either"), c("yes", "yes"))
            String evidenceCmd = String.format("bntemp <- setEvidence(%s, c(%s), c(%s))",
                    getQueryableNetwork().getAlias(),
                    getQueryableNetwork().getFormattedNodeNamesByNode(nodeWithEvidences),
                    getQueryableNetwork().getFormattedNodeEvidences(nodeWithEvidences));
            getR().eval(evidenceCmd);

            // get nodes without evidence
            finalNodes.removeIf(x -> x.getEvidence() != null);
            if (finalNodes.isEmpty()) {
                return;
            }

            // execute inferAllNodes: querygrain(bn1, nodes=c("smoke"), type="marginal")$smoke
            String queryCmd = String.format("restemp <- querygrain(bntemp, nodes=c(%s), type=\"marginal\")",
                    getQueryableNetwork().getFormattedNodeNamesByNode(finalNodes));
            getR().eval(queryCmd);

            // collect results
            for (Node node : finalNodes) {
                String resCmd = String.format("restemp$%s", node.getName());
                double[] result = getR().eval(resCmd).asDoubleArray();
                List<Double> values = Doubles.asList(result);
                node.setInference(values);
            }
        } catch (Exception e) {
            LOGGER.error("Erorr during quering the network", e);
            throw e;
        } finally {
            // free resources
            getR().eval("rm(bntemp)");
            getR().eval("rm(restemp)");
        }
    }

    /**
     * Computes marginal probability for the specified nodes.
     *
     * @param nodes
     */
    @Override
    public void inferNodes(Node... nodes) {
        this.inferNodes(Arrays.asList(nodes));
    }

    /**
     * Computes marginal probability for all nodes.
     */
    @Override
    public void inferAllNodes() {
        this.inferNodes(getQueryableNetwork().getNetwork().getNodes());
    }

    /**
     * Computes marginal probability for the specified node.
     *
     * @param nodeName Node
     */
    @Override
    public void inferNode(String nodeName) {
        this.inferNodes(getQueryableNetwork().getNetwork().getNodeByName(nodeName));
    }
}
