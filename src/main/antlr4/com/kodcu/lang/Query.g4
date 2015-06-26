grammar Query;
/**
 * Created by HAKAN on 26/06/2015.
 */

query
    :  queryDeclaration EOF
    ;

queryDeclaration
    :  queryTag dataLocation dataset
    ;

queryTag
    :   tag=(EXPORT|TRANSFER|COPY)
    ;

dataLocation
    :   databaseName '/' collectionName
    ;

databaseName
    :   IDENTIFIER
    ;

collectionName
    :   IDENTIFIER
    ;

dataset
    :   fromDeclaration toDeclaration (andDeclaration)?
    ;

fromDeclaration
    :   FROM declarations
    ;

toDeclaration
    :   TO  fileDeclaration
    ;

andDeclaration
    :   AND declarations
    ;

declarations
    :   mongoDeclaration
    |   esDeclaration
    ;

mongoDeclaration
    :   MONGO '=' configuration
    ;

fileDeclaration
    :   FILE '=' fileConfiguration
    ;

esDeclaration
    :   ES '=' configuration
    ;

fileConfiguration
    :  '{' NAME ':' STRINGLITERAL '}'
    ;

configuration
    :  '{' property  (',' property)* '}'
    ;

property
    :   key ':' value
    ;

key
    :   HOST
    |   PORT
    ;

value
    :   IDENTIFIER
    |   STRINGLITERAL
    |   NUMBER
    ;

// LEXER
// KEYWORDS

TO          : 'to'|'TO';
ES          : 'es'|'ES';
AND         : 'and'|'AND';
FROM        : 'from'|'FROM';
HOST        : 'host'|'HOST';
PORT        : 'port'|'PORT';
NAME        : 'name'|'NAME';
FILE        : 'file'|'FILE';
COPY        : 'copy'|'COPY';
MONGO       : 'mongo'|'MONGO';
EXPORT      : 'export'|'EXPORT';
TRANSFER    : 'transfer'|'TRANSPORT';


STRINGLITERAL
    :   '"' .*? '"'
    ;

NUMBER
    :  [0-9]+
    ;

IDENTIFIER
    :  [a-zA-Z_] [a-zA-Z_0-9]*
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;
SPACES
    :   [ \t\r\n]+ -> skip
    ;