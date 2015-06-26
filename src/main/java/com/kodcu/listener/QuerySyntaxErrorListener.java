package com.kodcu.listener;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Created by Hakan on 6/26/2015.
 */
public class QuerySyntaxErrorListener extends BaseErrorListener {

    private final Logger logger = LoggerFactory.getLogger(QuerySyntaxErrorListener.class);

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
        Collections.reverse(stack);
        logger.error(String.join("","line", String.valueOf(line) ,":", String.valueOf(charPositionInLine), "at", String.valueOf(offendingSymbol) ,":",msg));
        System.exit(1);
    }
}
