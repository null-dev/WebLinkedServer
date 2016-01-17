package xyz.nulldev.wls.routes.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import xyz.nulldev.wls.generator.FileListReponseGenerator;
import xyz.nulldev.wls.io.ImmutableFile;
import xyz.nulldev.wls.io.ServerManager;
import xyz.nulldev.wls.server.MasterServerImpl;

import java.util.Arrays;
import java.util.List;

/**
 * Project: WebLinkedServer
 * Created: 16/01/16
 * Author: nulldev
 */
public class FileRequestRoute implements Route {

    MasterServerImpl parentServer;
    Logger logger = LoggerFactory.getLogger(FileRequestRoute.class);

    public FileRequestRoute(MasterServerImpl parentServer) {
        this.parentServer = parentServer;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String[] splat = request.splat();
        String path = String.join("/", splat);
        String[] splitSplat = path.split("/");

        logger.info("File requested: {}", path);

        FileListReponseGenerator.SortType sortType
                = FileListReponseGenerator.SortType.NAME;
        FileListReponseGenerator.SortDirection sortDirection
                = FileListReponseGenerator.SortDirection.ASCENDING;
        //Try to process user sort query
        try {
            sortType = FileListReponseGenerator.SortType.valueOf(request.queryParams("sort"));
        } catch(IllegalArgumentException | NullPointerException ignored) {}
        try {
            sortDirection = FileListReponseGenerator.SortDirection.valueOf(request.queryParams("direction"));
        } catch(IllegalArgumentException | NullPointerException ignored) {}

        String parent = "";
        if(splitSplat.length >= 2) {
            String[] newSplat = new String[splitSplat.length - 1];
            System.arraycopy(splitSplat, 0, newSplat, 0, splitSplat.length - 1);
            parent = String.join("/", newSplat);
        }
        String dirname = "";
        if(splitSplat.length >= 1) {
            dirname = splitSplat[splitSplat.length - 1];
        }

        ServerManager.FileResponse checkResponse = parentServer.getServerManager().statFile(path);
        if(checkResponse == null) {
            response.status(404);
            logger.info("File '{}' not found!", path);
            return "<h1>404 Not Found</h1>";
        }
        if(checkResponse.getResponse() != null) {
            List<ImmutableFile> files =checkResponse.getResponse();
            FileListReponseGenerator responseGenerator
                    = new FileListReponseGenerator(dirname, parent, files, sortType, sortDirection);
            return responseGenerator.generate();
        } else if(checkResponse.getTargetURL() != null) {
            response.redirect(checkResponse.getTargetURL());
            return null;
        } else {
            logger.warn("Unhandled file request! ({})", Arrays.toString(splat));
            return "<h1>500 Internal Server Error</h1>";
        }
    }
}
