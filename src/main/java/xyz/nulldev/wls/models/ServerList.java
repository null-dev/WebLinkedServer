package xyz.nulldev.wls.models;

import java.util.ArrayList;

/**
 * Project: WebLinkedServer
 * Created: 16/01/16
 * Author: nulldev
 */
public class ServerList {
    ArrayList<String> servers = new ArrayList<>();

    public ServerList(ArrayList<String> servers) {
        this.servers = servers;
    }

    public ArrayList<String> getServers() {
        return servers;
    }

    public void setServers(ArrayList<String> servers) {
        this.servers = servers;
    }
}
