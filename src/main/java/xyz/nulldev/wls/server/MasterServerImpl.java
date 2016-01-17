package xyz.nulldev.wls.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nulldev.wls.WebLinkedServer;
import xyz.nulldev.wls.io.ServerManager;
import xyz.nulldev.wls.models.ServerList;
import xyz.nulldev.wls.routes.master.FileRequestRoute;
import xyz.nulldev.wls.utils.GSONUtils;
import xyz.nulldev.wls.utils.SparkMappingUtils;

import java.io.*;
import java.util.ArrayList;

/**
 * Project: WebLinkedServer
 * Created: 15/01/16
 * Author: nulldev
 */
public class MasterServerImpl implements Server {

    ServerManager serverManager = null;
    Logger logger = LoggerFactory.getLogger(MasterServerImpl.class);

    public MasterServerImpl() {
        File serverList = new File(WebLinkedServer.getCONFIGURATION().getConfig()
                .getProperty("master.serverListFile", "WLSServers.json"));
        ServerList list = null;
        if(serverList.exists()) {
            try (BufferedReader reader
                         = new BufferedReader(new InputStreamReader(new FileInputStream(serverList)))) {
                list = GSONUtils.getGson().fromJson(reader, ServerList.class);
            } catch (FileNotFoundException e) {
                logger.error("No server list found, all requests will 404!", e);
            } catch (IOException e) {
                logger.info("IOException reading server list! All requests will 404!", e);
            }
        }
        if(list == null) {
            list = new ServerList(new ArrayList<>());
        }
        serverManager = new ServerManager(list);
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    @Override
    public void start(String[] args) {
        SparkMappingUtils.lenientGetMap("/*", new FileRequestRoute(this));
        SparkMappingUtils.lenientPostMap("/*", new FileRequestRoute(this));
    }
}
