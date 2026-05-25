package agents;

import behaviours.nutritionbehaviours.NutritionBehaviour;
import com.google.gson.Gson;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.net.http.HttpClient;

/**
 * Agente que sirve datos nutricionales al RecommendationAgent.
 * Recibe un prefetch de TextMiningAgent para rellenar la caché y responde
 * peticiones NUTRITION_RESULT_REQUEST con los datos ya almacenados.
 */
public class NutritionAgent extends Agent {

    private static final String API_KEY = "74e8728ac10847199e9b7db0f0d97a4e";

    @Override
    protected void setup() {
        System.out.println("NutritionAgent " + getAID().getName() + " is ready.");

        HttpClient httpClient = HttpClient.newHttpClient();
        Gson       gson       = new Gson();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("nutrition-analysis");
        sd.setName("JADE-nutrition");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            System.out.println(getLocalName() + " registrado en DF como " + sd.getType());
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new NutritionBehaviour(this, httpClient, gson, API_KEY));
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
