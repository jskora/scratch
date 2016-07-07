package jfskora.wiz;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import jfskora.wiz.health.WizTemplateHealthCheck;
import jfskora.wiz.resources.WizResource;

public class WizApplication extends Application<WizConfig> {
    public static void main(String[] args) throws Exception {
        new WizApplication().run(args);
    }

    @Override
    public String getName() {
        return "wiz";
    }

    @Override
    public void initialize(Bootstrap<WizConfig> bootstrap) {
        // nada
    }

    @Override
    public void run(WizConfig wizConfig, Environment environment) {
        final WizResource resource = new WizResource(
                wizConfig.getTemplate(),
                wizConfig.getDefaultName()
        );
        environment.jersey().register(resource);
        final WizTemplateHealthCheck healthCheck =
                new WizTemplateHealthCheck(wizConfig.getTemplate());
        environment.healthChecks().register("template", healthCheck);
    }
}
