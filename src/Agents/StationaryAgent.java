package Agents;

import jade.core.Agent;

public class StationaryAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Agent stationnaire [" + getLocalName() + "] deploye avec succes sur : " + here().getName());
    }
}