import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class SimpleContainer {
    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        // false signifie que ce n'est PAS un Main Container
        Profile profile = new ProfileImpl(false); 
        // On lui dit de se connecter au Main Container qui est sur la machine locale
        profile.setParameter(Profile.MAIN_HOST, "localhost"); 
        
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        try {
            agentContainer.start();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }
}