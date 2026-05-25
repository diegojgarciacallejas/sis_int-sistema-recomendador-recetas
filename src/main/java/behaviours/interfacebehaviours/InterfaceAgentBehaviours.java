package behaviours.interfacebehaviours;

import agents.InterfaceAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPANames;

import javax.swing.SwingUtilities;
import java.util.logging.Logger;

public class InterfaceAgentBehaviours {

    private static final Logger log = Logger.getLogger(InterfaceAgentBehaviours.class.getName());

    private InterfaceAgentBehaviours() {}

    public static class WaitForRecommendationBehaviour extends CyclicBehaviour {

        private final InterfaceAgent agent;

        private final MessageTemplate MT =
                MessageTemplate.MatchConversationId(InterfaceAgent.CONV_RECOMMENDATION);

        public WaitForRecommendationBehaviour(InterfaceAgent agent) {
            super(agent);
            this.agent = agent;
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(MT);

            if (msg != null) {
                log.info("RECOMMENDATION_RESULT recibido de " + msg.getSender().getName());
                String ranking = msg.getContent();
                SwingUtilities.invokeLater(() -> agent.displayResults(ranking));
            } else {
                block();
            }
        }
    }

    public static class SendRequestBehaviour extends OneShotBehaviour {

        private final InterfaceAgent agent;
        private final String content;

        public SendRequestBehaviour(InterfaceAgent agent, String content) {
            super(agent);
            this.agent   = agent;
            this.content = content;
        }

        @Override
        public void action() {
            AID recipeSearchAgent = agent.findRecipeSearchAgent();

            if (recipeSearchAgent == null) {
                SwingUtilities.invokeLater(() -> {
                    agent.setStatus("❌ RecipeSearchAgent no encontrado en el DF.");
                    agent.displayResults(null);   // limpia el área y reactiva el botón
                    agent.enableSearch();
                });
                log.warning("RecipeSearchAgent no disponible en el DF.");
                return;
            }

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(recipeSearchAgent);
            msg.setConversationId(InterfaceAgent.CONV_USER_REQUEST);
            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            msg.setContent(content);
            msg.setLanguage("UTF-8");
            myAgent.send(msg);

            log.info("USER_REQUEST enviado a " + recipeSearchAgent.getName());
            SwingUtilities.invokeLater(() ->
                    agent.setStatus("✅ Solicitud enviada. Esperando resultados..."));
        }
    }
}
