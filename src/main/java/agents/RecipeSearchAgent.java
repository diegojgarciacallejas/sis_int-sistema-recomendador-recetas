package agents;

import behaviours.searchbehaviours.SearchBehaviour;
import com.google.gson.Gson;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.net.http.HttpClient;

public class RecipeSearchAgent extends Agent {

    private static final String API_KEY = "74e8728ac10847199e9b7db0f0d97a4e";

    private HttpClient httpClient;
    private Gson gson;

    @Override
    protected void setup() {
        System.out.println("RecipeSearchAgent " + getAID().getName() + " is ready.");
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("recipe-search");
        sd.setName("JADE-recipe-search");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            System.out.println(getLocalName() + " registrado en DF como " + sd.getType());
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
        );

        addBehaviour(new SearchBehaviour(this, template, httpClient, gson, API_KEY));
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
