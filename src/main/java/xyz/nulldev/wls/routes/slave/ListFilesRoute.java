package xyz.nulldev.wls.routes.slave;

import spark.Request;
import spark.Response;
import spark.Route;
import xyz.nulldev.wls.io.FileManager;
import xyz.nulldev.wls.io.ImmutableFile;
import xyz.nulldev.wls.models.APIResponse;
import xyz.nulldev.wls.server.SlaveServerImpl;

import java.io.File;
import java.util.List;

/**
 * Project: WebLinkedServer
 * Created: 16/01/16
 * Author: nulldev
 */
public class ListFilesRoute implements Route {

    public static final APIResponse FILE_NOT_FOUND
            = APIResponse.newErrorResponse("File not found!", 1);
    public static final APIResponse NOT_DIRECTORY
            = APIResponse.newErrorResponse("Not a valid directory!", 2);
    public static final APIResponse SECURITY_EXCEPTION
            = APIResponse.newErrorResponse("Security error!", 3);

    private SlaveServerImpl parentServer;

    public ListFilesRoute(SlaveServerImpl parentServer) {
        this.parentServer = parentServer;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String[] splat = request.splat();
        String path = String.join("/", splat);
        File resolvedFile = parentServer.getFILE_MANAGER().resolveRelative(path);
        try {
            parentServer.getFILE_MANAGER().validateSecure(resolvedFile);
        } catch (SecurityException e) {
            return SECURITY_EXCEPTION.asJSON();
        }
        if(!resolvedFile.exists()) {
            response.status(400);
            return FILE_NOT_FOUND.asJSON();
        } else if(!resolvedFile.isDirectory()) {
            response.status(400);
            return NOT_DIRECTORY.asJSON();
        }
        List<ImmutableFile> fileArrayList = parentServer.getFILE_MANAGER().listFiles(resolvedFile);
        response.status(200);
        return APIResponse.newSuccessResponse(FileManager.fileListToJSON(fileArrayList), 0).asJSON();
    }
}
