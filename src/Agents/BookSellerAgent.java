package Agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.HashMap;

public class BookSellerAgent extends Agent {

    // Le catalogue : Titre -> Prix
    private HashMap<String, Integer> catalogue;

    @Override
    protected void setup() {
        // 1. Initialisation du catalogue avec des prix aléatoires
        catalogue = new HashMap<>();
        // On génère un prix aléatoire entre 50 et 300 par exemple
        catalogue.put("Clean Code", (int)(Math.random() * 250) + 50);
        catalogue.put("Design Patterns", (int)(Math.random() * 250) + 50);
        catalogue.put("Refactoring", (int)(Math.random() * 250) + 50);

        System.out.println("Vendeur " + getLocalName() + " pret. Prix pour 'Clean Code' : " + catalogue.get("Clean Code") + " MAD.");

        // 2. Inscription dans les Pages Jaunes (DF)
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("book-selling"); // Le type de service très important pour la recherche !
        sd.setName("JADE-book-trading");
        dfd.addServices(sd);
        
        try {
            DFService.register(this, dfd);
            System.out.println(getLocalName() + " s'est inscrit dans le DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // 3. Ajout des comportements d'écoute
        addBehaviour(new OfferRequestsServer());
        addBehaviour(new PurchaseOrdersServer());
    }

    // Nettoyage avant la mort de l'agent
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this); // On se désinscrit des pages jaunes
            System.out.println(getLocalName() + " se retire du marché.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    // --- COMPORTEMENT 1 : Répondre aux appels d'offres (CFP) ---
    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            // On n'écoute QUE les messages de type CFP
            ACLMessage msg = receive(jade.lang.acl.MessageTemplate.MatchPerformative(ACLMessage.CFP));
            if (msg != null) {
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();

                Integer price = catalogue.get(title);
                if (price != null) {
                    // Le livre est disponible
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(String.valueOf(price.intValue()));
                } else {
                    // Le livre n'est pas dans le catalogue
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not-available");
                }
                send(reply);
            } else {
                block();
            }
        }
    }

    // --- COMPORTEMENT 2 : Gérer les achats confirmés ---
    private class PurchaseOrdersServer extends CyclicBehaviour {
        public void action() {
            // On n'écoute QUE les acceptations de commandes
            ACLMessage msg = receive(jade.lang.acl.MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
            if (msg != null) {
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();

                // On confirme la vente avec INFORM
                reply.setPerformative(ACLMessage.INFORM);
                System.out.println(getLocalName() + " a vendu " + title + " a " + msg.getSender().getLocalName());
                send(reply);
            } else {
                block();
            }
        }
    }
}