package com.kodcu.util;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import java.io.IOException;

/**
 * Created by Hakan on 9/9/2015.
 */
public class Log {

    public static void buildLog(){
        buildLog("mongolastic");
    }

    public static void buildLog(String fileName) {
        try {
            PatternLayout patternLayout = new PatternLayout("[%d{yyyy-MM-dd}] [%t] [%p]:%m%n");
            FileAppender fileAppender = new FileAppender(patternLayout, fileName.concat(".log"), false);
            fileAppender.setThreshold(Level.DEBUG);
            BasicConfigurator.configure(fileAppender);
            BasicConfigurator.configure();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
