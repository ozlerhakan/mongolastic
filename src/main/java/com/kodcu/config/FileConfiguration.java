package com.kodcu.config;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

/**
 * Created by Hakan on 5/19/2015.
 */
public class FileConfiguration {

    private final Logger logger = LoggerFactory.getLogger(FileConfiguration.class);
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
        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
        }

        return config;
    }

}
