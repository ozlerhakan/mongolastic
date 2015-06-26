package com.kodcu.listener;

import com.kodcu.lang.QueryBaseListener;
import com.kodcu.lang.QueryParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hakan on 6/26/2015.
 */
public class QueryListener extends QueryBaseListener {

    private List<String> properties = null;
    private String prefix;

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
        String enableBulk = String.join(": ", "enableBulk", "true");
        properties.add(enableBulk);
        prefix = "es";
    }

    @Override
    public void enterFileDeclaration(QueryParser.FileDeclarationContext ctx) {
        String filename = String.join(": ", "fileName", ctx.fileConfiguration().STRINGLITERAL().getText());
        properties.add(filename);
    }

    @Override
    public void enterMongoDeclaration(QueryParser.MongoDeclarationContext ctx) {
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
