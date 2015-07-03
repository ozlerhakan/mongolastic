package com.kodcu.listener;

import com.kodcu.lang.QueryBaseListener;
import com.kodcu.lang.QueryParser;
import com.kodcu.util.QueryWorker;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Created by Hakan on 6/26/2015.
 */
public class QueryListener extends QueryBaseListener {

    private final QueryWorker worker;

    public QueryListener(final QueryWorker worker) {
        this.worker = worker;
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
    public void enterNewCollectionName(QueryParser.NewCollectionNameContext ctx) {
        worker.setAsCollectionName(ctx.getText());
    }

    @Override
    public void enterNewDatabaseName(QueryParser.NewDatabaseNameContext ctx) {
        worker.setAsDatabaseName(ctx.getText());
    }

    @Override
    public void enterCollectionName(QueryParser.CollectionNameContext ctx) {
        worker.setCollectionName(ctx.getText());
    }

    @Override
    public void enterDatabaseName(QueryParser.DatabaseNameContext ctx) {
        worker.setDatabaseName(ctx.getText());
    }

    @Override
    public void enterEsDeclaration(QueryParser.EsDeclarationContext ctx) {
        ParseTree pt = ctx.getParent().getRuleContext().getParent().getChild(0);
        String where = pt.getText();
        worker.esDeclaration(where);
    }

    @Override
    public void enterFileProperty(QueryParser.FilePropertyContext ctx) {
    }

    @Override
    public void enterMongoDeclaration(QueryParser.MongoDeclarationContext ctx) {
        ParseTree pt = ctx.getParent().getRuleContext().getParent().getChild(0);
        String where = pt.getText();
        worker.mongoDeclaration(where);
    }

    @Override
    public void enterProperty(QueryParser.PropertyContext ctx) {
        worker.addKeyValue(ctx);
    }

    @Override
    public void exitQuery(QueryParser.QueryContext ctx) {
        worker.setDefaultValues();
    }

    @Override
    public void enterQuery(QueryParser.QueryContext ctx) {
        worker.initializePropertyList();
    }
}
