package xyz.nulldev.wls.server;

import xyz.nulldev.wls.WebLinkedServer;
import xyz.nulldev.wls.io.FileManager;
import xyz.nulldev.wls.routes.slave.GetFileRoute;
import xyz.nulldev.wls.routes.slave.ListFilesRoute;
import xyz.nulldev.wls.utils.SparkMappingUtils;

import java.io.File;

/**
 * Project: WebLinkedServer
 * Created: 15/01/16
 * Author: nulldev
 */
public class SlaveServerImpl implements Server {

    FileManager FILE_MANAGER;

    public SlaveServerImpl() {
        File file
                = new File(WebLinkedServer.getCONFIGURATION().getConfig().getProperty("slave.rootDir", "."));
        FILE_MANAGER
                = new FileManager(file);
    }

    public FileManager getFILE_MANAGER() {
        return FILE_MANAGER;
    }

    @Override
    public void start(String[] args) {
//        Spark.externalStaticFileLocation(FILE_MANAGER.getRootDirectory().getAbsolutePath());
        SparkMappingUtils.lenientGetMap("/_WLS/LIST_FILES/*", new ListFilesRoute(this));
        SparkMappingUtils.lenientGetMap("/*", new GetFileRoute(this));
        SparkMappingUtils.lenientPostMap("/*", new GetFileRoute(this));
    }
}
