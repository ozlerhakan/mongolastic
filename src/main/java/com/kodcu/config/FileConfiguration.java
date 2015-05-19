package com.kodcu.config;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Hakan on 5/19/2015.
 */
public class FileConfiguration {

    private final Logger logger = Logger.getLogger("FileConfiguration");
    private final String file;

    public FileConfiguration(String file) {
        this.file = file;
    }

    public YamlConfiguration getFileContent() {
        YamlConfiguration config = null;
        try {
            File configFile = new File(file);
            String content = FileUtils.readFileToString(configFile, "utf-8");
            Yaml yaml = new Yaml();
            config = yaml.loadAs(content, YamlConfiguration.class);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        return config;
    }

}
