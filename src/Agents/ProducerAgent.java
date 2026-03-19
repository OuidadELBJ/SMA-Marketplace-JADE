package Agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.HashMap;
import java.util.Map;

public class ProducerAgent extends Agent {
    
    // Le catalogue de produits
    protected Map<String, Double> products;

    // Le constructeur (initialisation du catalogue comme demandé dans le TP)
    public ProducerAgent() {
        products = new HashMap<String, Double>();
        products.put("LAPTOP", 6590.0);
        products.put("PRINTER", 2648.0);
        products.put("PHONE", 5600.0);
        products.put("CAMERA", 2500.0);
    }

    @Override
    protected void setup() {
        System.out.println("Producteur " + getLocalName() + " est prêt et attend les clients.");

        // Ajout du comportement cyclique pour écouter les messages en boucle
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // On écoute la boîte de réception
                ACLMessage msg = receive();
                
                if (msg != null) {
                    // 1. Si on reçoit une demande de prix (REQUEST)
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        String requestedProduct = msg.getContent(); // On lit le nom du produit
                        System.out.println(getLocalName() + " a reçu une demande pour : " + requestedProduct);
                        
                        // On prépare la réponse (l'enveloppe de retour)
                        ACLMessage reply = msg.createReply();

                        // On vérifie si le produit existe dans notre catalogue
                        if (products.containsKey(requestedProduct)) {
                            reply.setPerformative(ACLMessage.PROPOSE); // On fait une offre
                            reply.setContent(String.valueOf(products.get(requestedProduct))); // On met le prix
                            System.out.println(getLocalName() + " propose un prix de " + products.get(requestedProduct));
                        } else {
                            reply.setPerformative(ACLMessage.REFUSE); // On refuse car on n'a pas le produit
                            reply.setContent("Produit indisponible");
                        }
                        // On envoie la réponse
                        send(reply);
                    } 
                    // 2. Si on reçoit une confirmation d'achat (ACCEPT_PROPOSAL)
                    else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        System.out.println(getLocalName() + " a conclu la vente avec " + msg.getSender().getLocalName() + " !");
                    }
                } else {
                    // Si aucun message n'est là, on met le comportement en pause
                    block();
                }
            }
        });
    }
}