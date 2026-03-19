package Agents;

import jade.core.Agent;
import java.io.Serializable;

public class FirstAgent extends Agent implements Serializable {
    
    @Override
    protected void setup() {
        System.out.println("Salut je suis l'acheteur!");
        System.out.println("My Name is " + this.getAID().getName()); 
        System.out.println("Je me prépare .....");
    }

    @Override
    protected void beforeMove() {
        System.out.println("Avant de migrer vers une nouvelle location .....");
    }

    @Override
    protected void afterMove() {
        System.out.println("Je viens d'arriver à une nouvelle location ..");
    }

    @Override
    protected void takeDown() {
        System.out.println("avant de mourir .....");
    }
}