package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import behaviours.ontologybehaviours.FoodOntology;
import utils.DFUtils;

import java.util.Iterator;

public class OntologyAgent extends Agent {

    private final OntologyProcessor processor = new OntologyProcessor();

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " iniciado.");
        DFUtils.registerService(this, "ontology-service");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchConversationId("TEXT_MINING_RESULT");
                ACLMessage msg = receive(mt);

                if (msg != null) {
                    System.out.println("OntologyAgent <- recibe de TextMiningAgent:");
                    System.out.println(msg.getContent());

                    String output = processor.process(msg.getContent());

                    ACLMessage forward = new ACLMessage(ACLMessage.INFORM);
                    forward.addReceiver(new AID("GraphAgent", AID.ISLOCALNAME));
                    forward.setConversationId("ONTOLOGY_RESULT");
                    forward.setContent(output);
                    send(forward);

                    System.out.println("OntologyAgent -> envía a GraphAgent:");
                    System.out.println(output);

                    Iterator<AID> replyToIt = msg.getAllReplyTo();
                    if (replyToIt.hasNext()) {
                        AID replyTo = replyToIt.next();
                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                        reply.addReceiver(replyTo);
                        reply.setConversationId("ONTOLOGY_RESULT");
                        reply.setContent(output);
                        send(reply);
                        System.out.println("OntologyAgent -> reply-to: " + replyTo.getLocalName());
                    }

                } else {
                    block();
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        DFUtils.deregister(this);
        System.out.println(getLocalName() + " finalizado.");
    }
}