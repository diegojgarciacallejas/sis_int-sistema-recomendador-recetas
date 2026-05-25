package agents;

import behaviours.miningbehaviours.TextMiningBehaviour;
import jade.core.Agent;
import utils.DFUtils;

public class TextMiningAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " iniciado.");

        DFUtils.registerService(this, "text-mining-service");

        addBehaviour(new TextMiningBehaviour(this));
    }

    @Override
    protected void takeDown() {
        DFUtils.deregister(this);
        System.out.println(getLocalName() + " finalizado.");
    }
}