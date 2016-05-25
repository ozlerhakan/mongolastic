package com.kodcu.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

/**
 * Created by Hakan on 5/19/2015.
 */
public class FileConfiguration {

    private final Logger logger = LoggerFactory.getLogger(FileConfiguration.class);
    private final String parameter;

    public FileConfiguration(String parameter) {
        this.parameter = parameter;
    }

    public YamlConfiguration getFileContent() {
        YamlConfiguration config = null;
        try {
            File ymlFile = new File(parameter);
            Yaml yaml = new Yaml();
            if (ymlFile.isFile()) {
                FileInputStream configFile = new FileInputStream(ymlFile);
                config = yaml.loadAs(configFile, YamlConfiguration.class);
            }
            else {
                // we expect that this is just a string including yaml format
                config = yaml.loadAs(parameter, YamlConfiguration.class);
            }
            config = this.controlAsSettings(config);
            logger.info(System.lineSeparator() + "Config Output:" + System.lineSeparator() + config.toString() + System.lineSeparator());
        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
        }

        return config;
    }

    private YamlConfiguration controlAsSettings(YamlConfiguration config) {
        String dIndexAs = config.getMisc().getDindex().getAs();
        String cTypeAs = config.getMisc().getCtype().getAs();
        if (Objects.isNull(dIndexAs))
            config.getMisc().getDindex().setAs(config.getMisc().getDindex().getName());
        if (Objects.isNull(cTypeAs))
            config.getMisc().getCtype().setAs(config.getMisc().getCtype().getName());
        if (config.getMisc().getBatch() < 200)
            config.getMisc().setBatch(200);

        return config;
    }

}
