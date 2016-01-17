package xyz.nulldev.wls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Project: WebLinkedServer
 * Created: 15/01/16
 * Author: nulldev
 */
public class Configuration {
    final File file;
    final Properties config;
    Logger logger = LoggerFactory.getLogger(Configuration.class);

    /**
     * Initialize a configuration.
     * @param file The configuration file.
     * @throws IOException Exception loading configuration.
     */
    public Configuration(File file) throws IOException {
        this.file = file;
        this.config = new Properties();
        load(); //Load configuration file
    }

    /**
     * Load the configuration.
     * @throws IOException Exception loading the configuration.
     */
    public void load() throws IOException {
        logger.info("Loading configuration from file...");
        try(FileInputStream stream = new FileInputStream(file)) {
            config.load(stream);
        } catch (FileNotFoundException e) {
            logger.warn("Configuration file does not exist!", e);
            createDefaultConfig();
        } catch (IOException e) {
            logger.error("IOException reading configuration file!", e);
            throw new IOException("IOException reading configuration file!", e);
        }
        logger.info("Configuration loaded.");
    }

    /**
     * Save the configuration. This logs any failures to console instead of throwing an exception.
     */
    public void lenientSave() {
        try {
            save();
        } catch (IOException e) {
            logger.error("Error saving configuration!", e);
        }
    }

    /**
     * Save the configuration.
     * @throws IOException Exception saving configuration.
     */
    public void save() throws IOException {
        logger.info("Saving configuration to file...");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            config.store(stream, "-=[WebLinkedServer Configuration]=-");
        } catch (FileNotFoundException e) {
            logger.warn("Configuration file does not exist!", e);
            createDefaultConfig();
        } catch (IOException e) {
            logger.error("IOException reading configuration file!", e);
            throw new IOException("IOException reading configuration file!", e);
        }
        logger.info("Configuration saved.");
    }

    /**
     * Create default configuration file.
     * @throws IOException Exception creating configuration file.
     */
    public void createDefaultConfig() throws IOException {
        logger.info("Creating new configuration file!");
        if(file.exists()) {
            logger.info("Configuration file already exists! Deleting...");
            boolean deleted = file.delete();
            if(!deleted) {
                logger.error("Failed to delete configuration file!");
                throw new IOException("Failed to delete old configuration file!");
            }
        }
        try {
            boolean created = file.createNewFile();
            if(!created) {
                logger.error("Failed to create new configuration file!");
                throw new IOException("Failed to create new configuration file!");
            }
        } catch (IOException e) {
            logger.error("IOException creating new configuration file!", e);
            throw new IOException("IOException creating new configuration file!", e);
        }
        if(!file.canWrite()) {
            logger.error("Configuration file location not writable!");
            throw new IOException("Configuration file not writable!");
        }
    }

    /**
     * Get the configuration file.
     * @return The configuration file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Get the configuration.
     * @return The configuration.
     */
    public Properties getConfig() {
        return config;
    }
}
