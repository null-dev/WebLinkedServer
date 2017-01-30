package xyz.nulldev.wls.io;

import com.google.gson.JsonSyntaxException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nulldev.wls.WebLinkedServer;
import xyz.nulldev.wls.models.APIResponse;
import xyz.nulldev.wls.models.Server;
import xyz.nulldev.wls.models.ServerList;
import xyz.nulldev.wls.routes.slave.ListFilesRoute;
import xyz.nulldev.wls.utils.GSONUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: WebLinkedServer
 * Created: 16/01/16
 * Author: nulldev
 */
public class ServerManager {
    private ServerList loadedServerList;
    private OkHttpClient client = new OkHttpClient();
    private boolean enableDirectoryListing;
    private Logger logger = LoggerFactory.getLogger(ServerManager.class);

    public ServerManager(ServerList loadedServerList) {
        this.loadedServerList = loadedServerList;
        this.enableDirectoryListing = Boolean.parseBoolean(WebLinkedServer
                .getCONFIGURATION()
                .getConfig()
                .getProperty("master.listDirectories", "true").toLowerCase());
    }

    public ServerList getLoadedServerList() {
        return loadedServerList;
    }

    public void setLoadedServerList(ServerList loadedServerList) {
        this.loadedServerList = loadedServerList;
    }

    public FileResponse statFile(String path) {
        List<ImmutableFile> listResponse = null;
        for(Server server : loadedServerList.getServers()) {
            try {
                Response httpResponse = client.newCall(new Request.Builder().url(server.getInternalUrl() + "/_WLS/LIST_FILES/" + path).build()).execute();
                APIResponse apiResponse
                        = GSONUtils.getGson().fromJson(httpResponse.body().string(), APIResponse.class);
                if (apiResponse.getResponseCode() == 0 && enableDirectoryListing) {
                    if (listResponse == null) {
                        listResponse = new ArrayList<>();
                    }
                    //Merge directory
                    List<ImmutableFile> toAddList = FileManager.fileListFromJSON(apiResponse.getResponseContent());
                    for (ImmutableFile toAdd : toAddList) {
                        boolean found = false;
                        for (ImmutableFile toCheck : listResponse) {
                            if (toCheck.getName().equals(toAdd.getName())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found)
                            listResponse.add(toAdd);
                    }
                } else if (listResponse == null) {
                    if (apiResponse.getResponseCode() == ListFilesRoute.NOT_DIRECTORY.getResponseCode()) {
                        String url = server.getExternalUrl();
                        if (!url.endsWith("/")) url += "/";
                        return new FileResponse(url + path);
                    }
                }
            } catch(IOException e) {
                logger.warn("Unable to connect to slave server: " + server, e);
            } catch(JsonSyntaxException e) {
                logger.warn("Slave server is returning corrupt/invalid API responses: " + server, e);
            }
        }
        if(listResponse != null)
            return new FileResponse(listResponse);
        return null;
    }

    public static class FileResponse {
        List<ImmutableFile> response = null;
        String targetURL = null;

        public FileResponse(List<ImmutableFile> response) {
            this.response = response;
        }

        public FileResponse(String targetURL) {
            this.targetURL = targetURL;
        }

        public List<ImmutableFile> getResponse() {
            return response;
        }

        public String getTargetURL() {
            return targetURL;
        }
    }
}
