package agents;

import behaviours.graphbehaviours.GraphBehaviour;
import jade.core.Agent;
import utils.DFUtils;

public class GraphAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " iniciado");

        DFUtils.registerService(this, "graph-service");

        addBehaviour(new GraphBehaviour(this));
    }

    @Override
    protected void takeDown() {
        DFUtils.deregister(this);
        System.out.println(getLocalName() + " finalizado");
    }
}