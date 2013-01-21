package com.omt.midisync;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class MainApplication extends Application {

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        Router router = new Router(getContext());

        // Defines only one route
        router.attach("/hi",HiResource.class);
        router.attach("/bye",ByeResource.class);
        router.attachDefault(HelloWorldResource.class);
     

        return router;
    }
}