package com.kodcu.config;

import com.kodcu.lang.QueryLexer;
import com.kodcu.lang.QueryParser;
import com.kodcu.listener.QueryListener;
import com.kodcu.listener.QuerySyntaxErrorListener;
import com.kodcu.util.QueryWorker;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
            String content = this.readQueryToString(configFile);
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
            if (key.equals("fromMongo")) return "";
            else if (key.equals("db") || key.equals("index")) key = "database";
            else if (key.equals("c") || key.equals("type")) key = "collection";
            return String.join(": ", key, value);
        }).collect(Collectors.toList());
        extraParams.add(0, content);
        content = String.join("\n", extraParams);
        return yaml.loadAs(content, YamlConfiguration.class);
    }

    private String readQueryToString(File configFile) {
        try {
            QuerySyntaxErrorListener qsel = new QuerySyntaxErrorListener();
            InputStream query = new FileInputStream(configFile);
            QueryLexer lexer = new QueryLexer(new ANTLRInputStream(query));
            QueryParser parser = new QueryParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(qsel);

            QueryWorker worker = new QueryWorker();
            QueryListener ast = new QueryListener(worker);
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(ast, parser.query());
            return worker.getPropertiesAsString();
        } catch (IOException e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
            System.exit(-1);
        }
        return null;
    }
}
