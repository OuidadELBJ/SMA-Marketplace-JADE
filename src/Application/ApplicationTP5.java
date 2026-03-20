package Application;

import PlateformeJade.JadePlatform;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class ApplicationTP5 {

    public ApplicationTP5() {
        JadePlatform platform = new JadePlatform();

        try {
            System.out.println("Creation des conteneurs C1 a C5...");
            platform.createAgentContainer("C1", "localhost");
            platform.createAgentContainer("C2", "localhost");
            platform.createAgentContainer("C3", "localhost");
            platform.createAgentContainer("C4", "localhost");
            platform.createAgentContainer("C5", "localhost");

            ContainerController home = platform.createAgentContainer("Home", "localhost");

            // Itinéraire statique passé en argument
            Object[] itineraryArgs = new Object[]{"C1", "C2", "C3", "C4", "C5"};

            System.out.println("Deploiement du MobileAgent...");
            home.createNewAgent("Voyageur", "Agents.MobileAgent", itineraryArgs).start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ApplicationTP5();
    }
}