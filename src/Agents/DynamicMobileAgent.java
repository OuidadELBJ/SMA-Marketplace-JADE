package Agents;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.ArrayList;
import java.util.List;

public class DynamicMobileAgent extends Agent {

    private List<Location> itinerary;
    private int step = 0;

    @Override
    protected void setup() {
        System.out.println("Agent Mobile Dynamique [" + getLocalName() + "] initialise.");
        itinerary = new ArrayList<>();

        // 1. Enregistrement obligatoire du langage (SL) et de l'ontologie spécifique à JADE
        getContentManager().registerLanguage(new SLCodec());
        getContentManager().registerOntology(JADEManagementOntology.getInstance());

        System.out.println(getLocalName() + " : Depart dans 30 secondes... ");

        addBehaviour(new WakerBehaviour(this, 30000) {
            @Override
            protected void onWake() {
                construireItineraireEtPartir();
            }
        });
    }

    private void construireItineraireEtPartir() {
        System.out.println("\n--- " + getLocalName() + " interroge l'AMS pour trouver les conteneurs ---");
        
        // 2. Préparation de l'action
        QueryPlatformLocationsAction query = new QueryPlatformLocationsAction();
        Action action = new Action(getAMS(), query);

        // 3. Création formelle du message REQUEST avec les bons paramètres
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(getAMS());
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
        request.setOntology(JADEManagementOntology.getInstance().getName());
        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

        try {
            // 4. On injecte l'action dans le message et on l'envoie à l'AMS
            getContentManager().fillContent(request, action);
            ACLMessage response = FIPAService.doFipaRequestClient(this, request);

            // 5. On traite le message INFORM de retour
            if (response != null && response.getPerformative() == ACLMessage.INFORM) {
                Result result = (Result) getContentManager().extractContent(response);
                jade.util.leap.List locations = (jade.util.leap.List) result.getValue();

                System.out.println("Reponse AMS : " + locations.size() + " conteneurs detectes sur la plateforme.");

                for (int i = 0; i < locations.size(); i++) {
                    ContainerID cid = (ContainerID) locations.get(i);
                    if (!cid.getName().equalsIgnoreCase("Main-Container")) {
                        itinerary.add(cid);
                        System.out.println("- Destination decouverte : " + cid.getName());
                    }
                }

                if (!itinerary.isEmpty()) {
                    System.out.println("=> Demarrage de la tournee ! Prochain saut -> " + itinerary.get(step).getName());
                    doMove(itinerary.get(step));
                } else {
                    System.out.println("Aucun conteneur peripherique disponible pour voyager.");
                    doDelete();
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la communication avec l'AMS : " + e.getMessage());
            e.printStackTrace();
            doDelete();
        }
    }

    @Override
    protected void afterMove() {
        Location currentLocation = here();
        System.out.println("\n--- [" + getLocalName() + "] est arrive sur " + currentLocation.getName() + " ---");

        try {
            AgentContainer container = getContainerController();
            String cloneName = "DynAg_" + (step + 1);
            AgentController ac = container.createNewAgent(cloneName, "Agents.StationaryAgent", new Object[]{});
            ac.start();
        } catch (Exception e) {
            System.out.println("Erreur creation agent : " + e.getMessage());
        }

        step++;
        if (step < itinerary.size()) {
            System.out.println("[" + getLocalName() + "] : Tache terminee. Prochain saut dans 5s -> " + itinerary.get(step).getName());
            
            addBehaviour(new WakerBehaviour(this, 5000) {
                @Override
                protected void onWake() {
                    myAgent.doMove(itinerary.get(step));
                }
            });
        } else {
            System.out.println("[" + getLocalName() + "] : Itineraire dynamique termine. Fin de mission.");
            doDelete();
        }
    }
}