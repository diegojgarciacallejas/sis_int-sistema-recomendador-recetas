package utils;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DFUtils {

    public static void registerService(Agent agent, String serviceType) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceType);
        sd.setName(agent.getLocalName() + "-" + serviceType);

        dfd.addServices(sd);

        try {
            DFService.register(agent, dfd);
            System.out.println(agent.getLocalName() + " registrado en DF como " + serviceType);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    public static void deregister(Agent agent) {

        try {

            DFService.deregister(agent);

        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}