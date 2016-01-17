package xyz.nulldev.wls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import xyz.nulldev.wls.server.MasterServerImpl;
import xyz.nulldev.wls.server.Server;
import xyz.nulldev.wls.server.ServerRole;
import xyz.nulldev.wls.server.SlaveServerImpl;

import java.io.File;
import java.io.IOException;

/**
 * Project: WebLinkedServer
 * Created: 15/01/16
 * Author: nulldev
 */
public class WebLinkedServer {

    static int DEFAULT_PORT = 1919;
    static String DEFAULT_IP = "0.0.0.0";

    static File DEFAULT_CONFIGURATION_FILE = new File("WebLinkedServer.config");
    static Configuration CONFIGURATION;
    static Logger logger = LoggerFactory.getLogger(WebLinkedServer.class);
    static ServerRole SERVER_ROLE;
    static Server SERVER;
    static int PORT;
    static String IP;

    public static void main(String[] args) {
        logger.info("WebLinkedServer is starting...");
        //Load configuration
        try {
            CONFIGURATION = new Configuration(DEFAULT_CONFIGURATION_FILE);
        } catch (IOException e) {
            logger.error("Error loading configuration file, server aborting...");
            System.exit(-1);
            return;
        }
        //Get server port
        try {
            PORT = Integer.parseInt(CONFIGURATION.getConfig().getProperty("server.port",
                    String.valueOf(DEFAULT_PORT)));
        } catch(NumberFormatException e) {
            logger.error("Invalid port specified in config! Falling back to default port...", e);
            PORT = DEFAULT_PORT;
        }
        Spark.port(PORT);

        //Get server ip
        IP = CONFIGURATION.getConfig().getProperty("server.ip",
                DEFAULT_IP);
        Spark.ipAddress(IP);
        //Get server role
        try {
            SERVER_ROLE
                    = ServerRole.valueOf(CONFIGURATION.getConfig().getProperty("server.role",
                    ServerRole.SLAVE.name())
                    .toUpperCase());
        } catch(IllegalArgumentException e) {
            logger.error("Invalid server role specified in configuration, server aborting...");
            System.exit(-2);
            return;
        }
        //Get correct server for role
        if(SERVER_ROLE == ServerRole.SLAVE) {
            SERVER = new SlaveServerImpl();
        } else if(SERVER_ROLE == ServerRole.MASTER) {
            SERVER = new MasterServerImpl();
        } else {
            logger.error("Unimplemented server role specified in configuration, server aborting...");
            System.exit(-3);
            return;
        }
        logger.info("Starting server with role: {}...", SERVER_ROLE.name());
        //Start server
        SERVER.start(args);
        //Save config
        CONFIGURATION.lenientSave();
    }

    public static Configuration getCONFIGURATION() {
        return CONFIGURATION;
    }
}
