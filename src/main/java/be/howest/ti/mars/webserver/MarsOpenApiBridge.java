package be.howest.ti.mars.webserver;

import be.howest.ti.mars.logic.controller.MarsController;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.logging.Logger;

class MarsOpenApiBridge {
    private final MarsController controller;
    private static final Logger logger = Logger.getLogger(MarsOpenApiBridge.class.getName());

    MarsOpenApiBridge() {
        this.controller = new MarsController();
    }

    public Object getMessage(RoutingContext ctx) {
        return controller.getMessage();
    }

    public Object createUser(RoutingContext ctx) {
        logger.info("createUser");

        JsonObject json = ctx.getBodyAsJson();
        controller.createUser(json.getString("name"),
                json.getString("password"),
                json.getString("homeAddress"),
                String.valueOf(json.getInteger("homeEndpointID")));
        return null; // doesnt allow void
    }

    public Object login(RoutingContext ctx) {
        logger.info("login");

        JsonObject json = ctx.getBodyAsJson();
        return  controller.login(json.getString("name"), json.getString("password"));
    }
}
