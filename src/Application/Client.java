package Application;

import Agents.ConsumerAgent;
import Agents.ProducerAgent;
import PlateformeJade.JadePlatform;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Client {
    public Client() {
        // Crée le conteneur MarketPlace
        ContainerController market = new JadePlatform().createAgentContainer("MarketPlace", "localhost");
        try {
            // Déploie le consommateur (qui cherche un LAPTOP)
            market.createNewAgent("Consumer", ConsumerAgent.class.getName(), new Object[]{"LAPTOP"}).start();
            // Déploie deux producteurs
            market.createNewAgent("Producer1", ProducerAgent.class.getName(), new Object[]{}).start();
            market.createNewAgent("Producer2", ProducerAgent.class.getName(), new Object[]{}).start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}