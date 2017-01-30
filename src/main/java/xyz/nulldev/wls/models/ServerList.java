package xyz.nulldev.wls.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: WebLinkedServer
 * Created: 16/01/16
 * Author: nulldev
 */
public class ServerList {
    private static Logger logger = LoggerFactory.getLogger(ServerList.class);

    private List<Server> servers = new ArrayList<>();

    public ServerList(List<Server> servers) {
        this.servers = servers;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public static ServerList fromJson(JsonElement element) {
        JsonArray array;
        if(element.isJsonObject()) {
            array = element.getAsJsonObject().getAsJsonArray("servers");
        } else if(element.isJsonArray()) {
            array = element.getAsJsonArray();
        } else {
            logger.error("Invalid server list configuration!");
            return null;
        }
        List<Server> servers = new ArrayList<>();
        for(JsonElement server : array) {
            if(server.isJsonArray()) {
                JsonArray entry = server.getAsJsonArray();
                //[internal, external]
                servers.add(new Server(entry.get(0).getAsString(),
                        entry.get(1).getAsString()));
            } else if(server.isJsonObject()) {
                JsonObject entry = server.getAsJsonObject();
                /*
                {
                  "internal": "127.0.0.1",
                  "external": "0.0.0.0"
                }
                 */
                servers.add(new Server(entry.get("internal").getAsString(),
                        entry.get("external").getAsString()));
            } else {
                //"127.0.0.1"
                servers.add(new Server(server.getAsString()));
            }
        }
        return new ServerList(servers);
    }
}
