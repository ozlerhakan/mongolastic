package com.kodcu.config;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Hakan on 5/19/2015.
 */
public class FileConfiguration {

    private final Logger logger = LoggerFactory.getLogger(FileConfiguration.class);
    private final String[] args;

    public FileConfiguration(String[] args) {
        this.args = args;
    }

    public YamlConfiguration getFileContent() {
        String fileName = args[0];
        YamlConfiguration config = null;
        try {
            File configFile = new File(fileName);
            String content = FileUtils.readFileToString(configFile, "utf-8");
            Yaml yaml = new Yaml();
            if (args.length > 1)
                config = this.checkConfigArguments(yaml, content);
            else
                config = yaml.loadAs(content, YamlConfiguration.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
        }

        return config;
    }

    private YamlConfiguration checkConfigArguments(Yaml yaml, String content) {
        List<String> extraParams = Stream.of(args).filter(arg -> arg.split(":").length == 2).map(arg -> {
            final String[] confParameter = arg.split(":");
            String key = confParameter[0];
            String value = confParameter[1];
            return String.join(": ", key, value);
        }).collect(Collectors.toList());
        extraParams.add(0, content);
        content = String.join("\n", extraParams);
        return yaml.loadAs(content, YamlConfiguration.class);
    }

}
