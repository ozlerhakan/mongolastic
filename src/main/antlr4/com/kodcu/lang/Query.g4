grammar Query;
/**
 * Created by HAKAN on 26/06/2015.
 */

query
    :  queryDeclaration EOF
    ;

queryDeclaration
    :  queryTag dataLocation asStatement? dataset
    ;

queryTag
    :   tag=(EXPORT|TRANSFER|COPY|CLONE)
    ;

dataLocation
    :   databaseName '/' collectionName
    ;

asStatement
    :  AS  newDatabaseName '/' newCollectionName
    ;

newDatabaseName
    :   IDENTIFIER
    ;

newCollectionName
    :   IDENTIFIER
    ;

databaseName
    :   IDENTIFIER
    ;

collectionName
    :   IDENTIFIER
    ;

dataset
    :   fromDeclaration toDeclaration
    ;

fromDeclaration
    :   FROM declarations
    ;

toDeclaration
    :   TO  declarations
    ;

andDeclaration
    :   AND declarations
    ;

declarations
    :   mongoDeclaration
    |   esDeclaration
    ;

mongoDeclaration
    :   MONGO ('=' configuration)?
    ;

fileDeclaration
    :   FILE '=' fileConfiguration
    ;

esDeclaration
    :   ES ('=' configuration)?
    ;

fileConfiguration
    :  '{' fileProperty '}'
    ;

fileProperty
    :    NAME ':' STRINGLITERAL
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
AS          : 'as'|'AS';
AND         : 'and'|'AND';
FROM        : 'from'|'FROM';
HOST        : 'host'|'HOST';
PORT        : 'port'|'PORT';
NAME        : 'name'|'NAME';
FILE        : 'file'|'FILE';
COPY        : 'copy'|'COPY';
CLONE       : 'clone'|'CLONE';
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