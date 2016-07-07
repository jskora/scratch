package jfskora.wiz.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import jfskora.wiz.api.WizMessage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/wiz")
@Produces(MediaType.APPLICATION_JSON)
public class WizResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    public WizResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public WizMessage sayMessage(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.or(defaultName));
        return new WizMessage(counter.incrementAndGet(), value);
    }
}