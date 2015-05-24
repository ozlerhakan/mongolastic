package com.kodcu.config;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

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
            config = yaml.loadAs(content, YamlConfiguration.class);
            if (args.length > 1)
                this.checkConfigArguments(config);
        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
        }

        return config;
    }

    private void checkConfigArguments(YamlConfiguration config) {
        Class<?> clazz = config.getClass();
        final Method[] methodList = clazz.getDeclaredMethods();

        for (int i = 1; i < args.length; i++) {
            final String[] confParameter = args[i].split(":");
            switch (confParameter.length) {
                case 2:
                    String key = String.join("", "set", Character.toUpperCase(confParameter[0].charAt(0)) + confParameter[0].substring(1));
                    String value = confParameter[1];
                    boolean coupling = Arrays.asList(methodList).stream().anyMatch(method -> {
                        String methodName = method.getName();
                        if (methodName.equals(key)) {
                            try {
                                Object param = value;
                                Class[] types = method.getParameterTypes();

                                if (types[0].getName().equals("boolean"))
                                    param = Boolean.valueOf(value);
                                else if (types[0].getName().equals("int"))
                                    param = Integer.valueOf(value);

                                method.setAccessible(true);
                                method.invoke(config, param);
                            } catch (Exception ex) {
                                logger.error(ex.getMessage(), ex.fillInStackTrace());
                                return false;
                            }
                            return true;
                        } else
                            return false;
                    });
                    if (!coupling) {
                        logger.error("Incorrect parameter. Pass the correct parameter name.");
                        System.exit(-1);
                    }
            }
        }
    }

}
