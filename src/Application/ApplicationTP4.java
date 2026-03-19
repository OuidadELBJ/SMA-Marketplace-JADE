package Application;

import Agents.BookBuyerAgent;
import Agents.BookSellerAgent;
import PlateformeJade.JadePlatform;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class ApplicationTP4 {
    
    public ApplicationTP4() {
        // 1. On lance la plateforme et on crée un conteneur pour notre librairie
        ContainerController libraryContainer = new JadePlatform().createAgentContainer("LibraryContainer", "localhost");
        
        try {
            // 2. On déploie 3 vendeurs (Ils vont générer leurs prix aléatoires et s'inscrire au DF)
            libraryContainer.createNewAgent("Seller1", BookSellerAgent.class.getName(), new Object[]{}).start();
            libraryContainer.createNewAgent("Seller2", BookSellerAgent.class.getName(), new Object[]{}).start();
            libraryContainer.createNewAgent("Seller3", BookSellerAgent.class.getName(), new Object[]{}).start();
            
            // 3. On déploie notre acheteur qui cherche spécifiquement "Clean Code"
            libraryContainer.createNewAgent("Buyer", BookBuyerAgent.class.getName(), new Object[]{"Clean Code"}).start();
            
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ApplicationTP4();
    }
}