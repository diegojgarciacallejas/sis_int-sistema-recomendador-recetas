package agents;

import behaviours.recommendationbehaviours.RecommendationBehaviour;
import jade.core.Agent;
import utils.DFUtils;

public class RecommendationAgent extends Agent {

    @Override
    protected void setup() {

        System.out.println(getLocalName() + " iniciado");

        DFUtils.registerService(this, "recommendation-service");

        addBehaviour(new RecommendationBehaviour(this));
    }

    @Override
    protected void takeDown() {

        DFUtils.deregister(this);

        System.out.println(getLocalName() + " finalizado");
    }
}