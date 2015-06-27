package com.kodcu.listener;

import com.kodcu.lang.QueryBaseListener;
import com.kodcu.lang.QueryParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hakan on 6/26/2015.
 */
public class QueryListener extends QueryBaseListener {

    private static final Logger logger = Logger.getLogger(QueryListener.class);
    private List<String> properties = null;
    private String prefix;
    private boolean fromEs;
    private boolean fromMongo;

    public QueryListener() {
        properties = new ArrayList<>();
    }

    @Override
    public void enterAndDeclaration(QueryParser.AndDeclarationContext ctx) {
    }

    @Override
    public void enterFromDeclaration(QueryParser.FromDeclarationContext ctx) {
    }

    @Override
    public void enterToDeclaration(QueryParser.ToDeclarationContext ctx) {
    }

    @Override
    public void enterCollectionName(QueryParser.CollectionNameContext ctx) {
        String collection = String.join(": ", "collection", "\"".concat(ctx.getText()).concat("\""));
        properties.add(collection);
    }

    @Override
    public void enterDatabaseName(QueryParser.DatabaseNameContext ctx) {
        String db = String.join(": ", "database", "\"".concat(ctx.getText()).concat("\""));
        properties.add(db);
    }

    @Override
    public void enterEsDeclaration(QueryParser.EsDeclarationContext ctx) {
        ParseTree pt = ctx.getParent().getRuleContext().getParent().getChild(0);
        String where = pt.getText();
        if (where.equalsIgnoreCase("from")) {
            fromEs = true;
        } else if (where.equalsIgnoreCase("and") && !fromEs) {
            String enableBulk = String.join(": ", "enableBulk", "true");
            properties.add(enableBulk);
        } else if (where.equalsIgnoreCase("and") && fromEs) {
            logger.error("INFO: You cannot set two elastic instances in a query!");
            System.exit(-1);
        }
        prefix = "es";
    }

    @Override
    public void enterFileDeclaration(QueryParser.FileDeclarationContext ctx) {
        String filename = String.join(": ", "fileName", ctx.fileConfiguration().STRINGLITERAL().getText());
        properties.add(filename);
    }

    @Override
    public void enterMongoDeclaration(QueryParser.MongoDeclarationContext ctx) {
        ParseTree pt = ctx.getParent().getRuleContext().getParent().getChild(0);
        String where = pt.getText();
        if (where.equalsIgnoreCase("from")) {
            String mongo = String.join(": ", "fromMongo", "true");
            properties.add(mongo);
            fromMongo = true;
        } else if (where.equalsIgnoreCase("and") && !fromMongo) {
            String enableBulk = String.join(": ", "enableBulk", "true");
            properties.add(enableBulk);
        } else if (where.equalsIgnoreCase("and") && fromMongo) {
            logger.error("INFO: You cannot set two mongo instances in a query!");
            System.exit(-1);
        }
        prefix = "mongo";
    }

    @Override
    public void enterProperty(QueryParser.PropertyContext ctx) {
        String key = String.join("",
                prefix,
                ctx.key().getText().substring(0, 1).toUpperCase(),
                ctx.key().getText().substring(1, ctx.key().getText().length()).toLowerCase());
        String property = String.join(": ", key, ctx.value().getText());
        properties.add(property);
    }

    public String getContent() {
        return String.join("\n", properties);
    }


}
