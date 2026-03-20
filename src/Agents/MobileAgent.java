package Agents;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.behaviours.WakerBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.ArrayList;
import java.util.List;

public class MobileAgent extends Agent {

    private List<Location> itinerary;
    private int step = 0;

    @Override
    protected void setup() {
        System.out.println("Agent Mobile [" + getLocalName() + "] initialise.");
        
        itinerary = new ArrayList<>();
        Object[] args = getArguments();
        if (args != null) {
            for (Object arg : args) {
                itinerary.add(new ContainerID((String) arg, null)); 
            }
        }

        if (!itinerary.isEmpty()) {
            System.out.println(getLocalName() + " : Depart dans 30 secondes...");
            
            // Délai de 30 secondes avant le tout premier départ
            addBehaviour(new WakerBehaviour(this, 30000) {
                @Override
                protected void onWake() {
                    System.out.println(getLocalName() + " : Debut de la tournee -> " + itinerary.get(step).getName());
                    myAgent.doMove(itinerary.get(step));
                }
            });
        } else {
            System.out.println("Erreur : Aucun itineraire fourni.");
            doDelete();
        }
    }

    @Override
    protected void afterMove() {
        Location currentLocation = here();
        System.out.println("\n--- [" + getLocalName() + "] est arrive sur " + currentLocation.getName() + " ---");

        // 1. Exécution de la tâche (déploiement de l'agent stationnaire)
        try {
            AgentContainer container = getContainerController();
            String cloneName = "Ag_" + (step + 1);
            AgentController ac = container.createNewAgent(cloneName, "Agents.StationaryAgent", new Object[]{});
            ac.start();
        } catch (Exception e) {
            System.out.println("Erreur lors de la creation de l'agent stationnaire : " + e.getMessage());
        }

        // 2. Préparation du prochain saut
        step++;
        if (step < itinerary.size()) {
            System.out.println("[" + getLocalName() + "] : Tache terminee. Prochain saut dans 5 secondes -> " + itinerary.get(step).getName());
            
            // Petite pause de 5 secondes pour bien voir le mouvement dans le Sniffer
            addBehaviour(new WakerBehaviour(this, 5000) {
                @Override
                protected void onWake() {
                    myAgent.doMove(itinerary.get(step));
                }
            });
        } else {
            System.out.println("[" + getLocalName() + "] : Itineraire statique termine. Fin de mission.");
            doDelete();
        }
    }
}