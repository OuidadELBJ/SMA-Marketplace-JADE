package PlateformeJade;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.ContainerController;

public class JadePlatform {
    private Runtime runtime;

    public JadePlatform() {
        runtime = Runtime.instance();
        Properties p = new ExtendedProperties();
        p.setProperty("gui", "true");
        ProfileImpl profile = new ProfileImpl(p);
        runtime.createMainContainer(profile);
    }

    public ContainerController createAgentContainer(String containerName, String host) {
        ProfileImpl profile = new ProfileImpl(false);
        profile.setParameter(ProfileImpl.MAIN_HOST, host);
        profile.setParameter(ProfileImpl.CONTAINER_NAME, containerName);
        return runtime.createAgentContainer(profile);
    }
}