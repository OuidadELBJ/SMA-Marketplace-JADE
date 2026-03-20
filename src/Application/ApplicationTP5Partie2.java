package Application;

import PlateformeJade.JadePlatform;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class ApplicationTP5Partie2 {

    public ApplicationTP5Partie2() {
        JadePlatform platform = new JadePlatform();

        try {
            System.out.println("Creation aleatoire de conteneurs sur la plateforme...");
            
            // On crée quelques conteneurs avec des noms différents
            platform.createAgentContainer("Serveur-Alpha", "localhost");
            platform.createAgentContainer("Serveur-Beta", "localhost");
            platform.createAgentContainer("Serveur-Gamma", "localhost");

            // On crée le conteneur de départ
            ContainerController home = platform.createAgentContainer("Home", "localhost");

            System.out.println("Deploiement de l'agent mobile dynamique (sans argument d'itineraire)...");
            
            // Remarquez ici : new Object[]{} (aucun itinéraire n'est fourni !)
            home.createNewAgent("IndianaJones", "Agents.DynamicMobileAgent", new Object[]{}).start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ApplicationTP5Partie2();
    }
}