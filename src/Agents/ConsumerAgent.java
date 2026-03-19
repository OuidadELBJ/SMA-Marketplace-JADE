package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ConsumerAgent extends Agent {
    
    private String targetProduct; // Le produit qu'on veut acheter

  @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetProduct = (String) args[0];
            System.out.println("Consommateur " + getLocalName() + " cherche à acheter : " + targetProduct);

            // On utilise un WakerBehaviour pour retarder l'achat de 20 secondes (20000 ms)
            addBehaviour(new jade.core.behaviours.WakerBehaviour(this, 20000) {
                @Override
                protected void onWake() {
                    myAgent.addBehaviour(new PurchaseOrderBehaviour());
                }
            });
        }
    }

    // --- Le comportement interne (Machine à états) ---
    private class PurchaseOrderBehaviour extends Behaviour {
    
        private AID[] sellerAgents = {
            new AID("Producer1", AID.ISLOCALNAME), 
            new AID("Producer2", AID.ISLOCALNAME)
        };
        
        private AID bestSeller = null;
        private double bestPrice = Double.MAX_VALUE; // On initialise au prix maximum possible
        private int repliesCnt = 0; // Compteur de réponses reçues
        private MessageTemplate mt; // Filtre pour ne lire que les bonnes réponses
        private int step = 0; // L'étape actuelle de notre machine à états

        @Override
        public void action() {
            switch (step) {
                case 0:
                    // ÉTAPE 0 : Envoyer la demande de prix (REQUEST)
                    ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                    for (int i = 0; i < sellerAgents.length; ++i) {
                        req.addReceiver(sellerAgents[i]); // On ajoute tous les vendeurs en destinataires
                    }
                    req.setContent(targetProduct);
                    req.setConversationId("achat-produit");
                    req.setReplyWith("req" + System.currentTimeMillis()); // Tag unique pour identifier la réponse
                    send(req); // On envoie !

                    // On prépare un filtre pour l'étape suivante : on ne lira que les réponses à CETTE requête
                    mt = MessageTemplate.and(
                            MessageTemplate.MatchConversationId("achat-produit"),
                            MessageTemplate.MatchInReplyTo(req.getReplyWith()));
                    step = 1; // On passe à l'étape suivante
                    break;

                case 1:
                    // ÉTAPE 1 : Recevoir et comparer les offres
                    ACLMessage reply = receive(mt); // On lit la boîte aux lettres avec notre filtre
                    if (reply != null) {
                        // Si c'est une proposition de prix
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            double price = Double.parseDouble(reply.getContent()); // On lit le prix
                            if (bestSeller == null || price < bestPrice) {
                                // On a trouvé un prix moins cher, on le garde en mémoire !
                                bestPrice = price;
                                bestSeller = reply.getSender();
                            }
                        }
                        repliesCnt++; // On compte la réponse (même si c'est un REFUSE)
                        
                        // Si tous les vendeurs ont répondu, on peut passer à l'achat
                        if (repliesCnt >= sellerAgents.length) {
                            step = 2;
                        }
                    } else {
                        block(); // Très important : on met en pause en attendant le prochain message
                    }
                    break;

                case 2:
                    // ÉTAPE 2 : Confirmer l'achat au meilleur vendeur (ACCEPT_PROPOSAL)
                    if (bestSeller != null) {
                        ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        order.addReceiver(bestSeller);
                        order.setContent(targetProduct);
                        order.setConversationId("achat-produit");
                        send(order);
                        System.out.println("=> SUCCÈS : " + getLocalName() + " a acheté " + targetProduct 
                                + " chez " + bestSeller.getLocalName() + " pour " + bestPrice + " MAD !");
                    } else {
                        System.out.println("=> ÉCHEC : " + getLocalName() + " n'a trouvé aucune offre pour " + targetProduct);
                    }
                    step = 3; // On termine le processus
                    break;
            }
        }

        @Override
        public boolean done() {
            // Le comportement s'arrête définitivement quand l'étape 3 est atteinte
            return (step == 3);
        }
    }
}