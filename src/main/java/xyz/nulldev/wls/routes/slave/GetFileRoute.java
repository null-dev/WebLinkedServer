package xyz.nulldev.wls.routes.slave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import xyz.nulldev.wls.server.SlaveServerImpl;

import javax.servlet.ServletOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Project: WebLinkedServer
 * Created: 17/01/16
 * Author: nulldev
 */
public class GetFileRoute implements Route {

    private Logger logger = LoggerFactory.getLogger(GetFileRoute.class);

    private SlaveServerImpl parentServer;

    public GetFileRoute(SlaveServerImpl parentServer) {
        this.parentServer = parentServer;
    }

    /**
     * We must override the default Spark static resource serving if we want to allow byte serving
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        //Allow chunked resource serving
        response.header("Accept-Ranges", "bytes");
        //Resolve and verify file exists
        String[] splat = request.splat();
        String path = String.join("/", splat);
        File resolvedFile = parentServer.getFILE_MANAGER().resolveRelative(path);
        try {
            parentServer.getFILE_MANAGER().validateSecure(resolvedFile);
        } catch (SecurityException e) {
            response.status(403);
            return null;
        }
        if(!resolvedFile.exists() || resolvedFile.isDirectory()) {
            response.status(404);
            return null;
        }
        long length = resolvedFile.length();
        long fileStart = 0;
        long fileEnd = length - 1;
        int statusCode = 200;

        if(request.headers().contains("Range")) {
            String typeSplit[] = request.headers("Range").trim().split("=");
            if(typeSplit.length < 2) {
                response.status(400);
                return null;
            }
            String rangeSplit[] = typeSplit[1].trim().split("-");
            try {
                fileStart = Long.parseLong(rangeSplit[0]);
                if(rangeSplit.length >= 2) {
                    fileEnd = Long.parseLong(rangeSplit[1]);
                }
                statusCode = 206;
            } catch(NumberFormatException e) {
                e.printStackTrace();
                response.status(400);
                return null;
            }
        }
        //Do not serve invalid ranges (still serve it if the file is 0 bytes originally)
        if(fileEnd - fileStart <= 0 && length > 0) {
            //Requested range not satisfiable
            response.status(416);
            return null;
        }
        response.status(statusCode);
        response.header("Content-Length", String.valueOf(fileEnd - fileStart + 1));
        response.header("Content-Range", "bytes " + fileStart + "-" + fileEnd + "/" + length);
        //Allow download forcing
        if(request.queryParams().contains("force-download")
                && Objects.equals(request.queryParams("force-download").toUpperCase(), "TRUE")) {
            response.type("application/force-download");
            response.header("Content-Transfer-Encoding", "binary");
            response.header("Content-Disposition","attachment; filename=\"" + resolvedFile.getName() + "\"");
        }
        try (BufferedInputStream stream
                     = new BufferedInputStream(new FileInputStream(resolvedFile));
             ServletOutputStream outputStream
                     = response.raw().getOutputStream()) {
            //Skip some bytes
            long needToSkip = fileStart;
            while(needToSkip > 0) {
                long skipped = stream.skip(needToSkip);
                if(skipped < 0) break; //Can't skip anymore for some reason
                needToSkip -= skipped;
            }
            long remainingBytes = fileEnd - fileStart + 1;
            while(true) {
                byte data[] = new byte[8192];
                int r = stream.read(data, 0, data.length);
                if(r < 0) break;
                int toWrite = (int) Math.min(remainingBytes, r);
                outputStream.write(data, 0, toWrite);
                remainingBytes -= toWrite;
                if(remainingBytes <= 0) break;
            }
        } catch (IOException e) {
            logger.warn("IOException reading from file!", e);
        }
        return null;
    }
}
