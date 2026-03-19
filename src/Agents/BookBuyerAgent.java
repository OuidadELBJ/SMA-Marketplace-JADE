package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BookBuyerAgent extends Agent {
    
    private String targetBookTitle; // Le livre qu'on cherche
    private AID[] sellerAgents;     // La liste des vendeurs trouvés dans le DF

    @Override
    protected void setup() {
        System.out.println("Bonjour ! L'acheteur " + getLocalName() + " est pret.");
        
        // 1. Récupération du titre du livre passé en argument
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetBookTitle = (String) args[0];
            System.out.println("Livre recherche : " + targetBookTitle);

            // 2. Comportement qui s'exécute chaque minute (60000 ms)
            addBehaviour(new TickerBehaviour(this, 60000) {
                @Override
                protected void onTick() {
                    System.out.println("--- Recherche de vendeurs pour " + targetBookTitle + " ---");
                    
                    // A. Préparation du filtre de recherche pour les Pages Jaunes (DF)
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("book-selling"); // On cherche exactement ce type de service
                    template.addServices(sd);
                    
                    try {
                        // B. Interrogation du DF
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        System.out.println(result.length + " vendeur(s) trouve(s).");
                        
                        sellerAgents = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
                            sellerAgents[i] = result[i].getName(); // On sauvegarde leurs adresses
                        }
                    } catch (FIPAException fe) {
                        fe.printStackTrace();
                    }

                    // C. Si on a trouvé des vendeurs, on lance l'appel d'offres
                    if (sellerAgents != null && sellerAgents.length > 0) {
                        myAgent.addBehaviour(new RequestPerformer());
                    }
                }
            });
        } else {
            System.out.println("Aucun titre de livre specifie en argument !");
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("L'acheteur " + getLocalName() + " a quitte la plateforme.");
    }

    // --- LE CŒUR DE LA NÉGOCIATION : FIPA Contract Net ---
    private class RequestPerformer extends Behaviour {
        private AID bestSeller;
        private int bestPrice;
        private int repliesCnt = 0;
        private MessageTemplate mt; 
        private int step = 0;

        public void action() {
            switch (step) {
                case 0:
                    // ÉTAPE 0 : Envoi de l'appel d'offres (CFP) à tous les vendeurs
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < sellerAgents.length; ++i) {
                        cfp.addReceiver(sellerAgents[i]);
                    }
                    cfp.setContent(targetBookTitle);
                    cfp.setConversationId("book-trade");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis()); 
                    send(cfp);

                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                                             MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    step = 1;
                    break;

                case 1:
                    // ÉTAPE 1 : Réception des PROPOSE et REFUSE
                    ACLMessage reply = receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            int price = Integer.parseInt(reply.getContent());
                            if (bestSeller == null || price < bestPrice) {
                                bestPrice = price;
                                bestSeller = reply.getSender();
                            }
                        }
                        repliesCnt++;
                        if (repliesCnt >= sellerAgents.length) {
                            step = 2; // Tous les vendeurs ont répondu, on passe au choix
                        }
                    } else {
                        block();
                    }
                    break;

                case 2:
                    // ÉTAPE 2 : Envoi de l'acceptation au meilleur, et refus aux autres
                    if (bestSeller != null) {
                        ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        order.addReceiver(bestSeller);
                        order.setContent(targetBookTitle);
                        order.setConversationId("book-trade");
                        order.setReplyWith("order" + System.currentTimeMillis());
                        send(order);

                        // On prépare le filtre pour attendre la confirmation finale (INFORM)
                        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                                                 MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                        step = 3;
                    } else {
                        System.out.println("Echec: Aucun vendeur ne propose " + targetBookTitle);
                        step = 4; // Fin
                    }
                    break;

                case 3:
                    // ÉTAPE 3 : Attente de la confirmation finale (INFORM)
                    reply = receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            System.out.println("=> SUCCES : Achat de " + targetBookTitle + " chez " + reply.getSender().getLocalName() + " pour " + bestPrice + " MAD !");
                            myAgent.doDelete(); // L'objectif est atteint, l'agent peut mourir
                        }
                        step = 4;
                    } else {
                        block();
                    }
                    break;
            }
        }

        public boolean done() {
            return ((step == 2 && bestSeller == null) || step == 4);
        }
    }
}