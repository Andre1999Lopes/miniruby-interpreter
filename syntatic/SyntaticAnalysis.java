package syntatic;

import java.io.IOException;

import interpreter.command.Command;
import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.LexicalException;
import lexical.TokenType;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Lexeme current;

    public SyntaticAnalysis(LexicalAnalysis lex) throws LexicalException, IOException {
        this.lex = lex;
        this.current = lex.nextToken();
    }

    public Command start() throws LexicalException, IOException {
        return null;
    }

    private void advance() throws LexicalException, IOException {
        // System.out.println("Advanced (\"" + current.token + "\", " +
        //     current.type + ")");
        current = lex.nextToken();
    }

    private void eat(TokenType type) throws LexicalException, IOException {
        System.out.println("Expected (..., " + type + "), found (\"" + 
            current.token + "\", " + current.type + ")");
        if (type == current.type) {
            current = lex.nextToken();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
    }

    // <code> ::= { <cmd> }
    private void procCode() {
        procCmd();
        while (current.type == TokenType.IF ||
                current.type == TokenType.UNLESS ||
                current.type == TokenType.WHILE ||
                current.type == TokenType.UNTIL ||
                current.type == TokenType.FOR ||
                current.type == TokenType.PUTS ||
                current.type == TokenType.PRINT ||
                current.type == TokenType.ID
            ) {
                procCmd();
            }
    }

    // <cmd> ::= <if> | <unless> | <while> | <until> | <for> | <output> | <assign>
    private void procCmd() {
        if (current.type == TokenType.IF) {
            procIf();
        }
        else if (current.type == TokenType.UNLESS) {
            procUnless();
        }
        else if (current.type == TokenType.WHILE) {
            procWhile();
        }
        else if (current.type == TokenType.UNTIL) {
            procUntil();
        }
        else if (current.type == TokenType.FOR) {
            procFor();
        }
        else if (current.type == TokenType.PUTS || current.type == TokenType.PRINT) {
            procOutput();
        }
        else if (current.type == TokenType.ID) {
            procAssign();
        }
    }

    // <if> ::= if <boolexpr> [ then ] <code> { elsif <boolexpr> [ then ] <code> } [ else <code> ] end
    private void procIf() {

    }

    // <unless> ::= unless <boolexpr> [ then ] <code> [ else <code> ] end 
    private void procUnless() {

    }

    // <while> ::= while <boolexpr> [ do ] <code> end
    private void procWhile() {

    }

    // <until> ::= until <boolexpr> [ do ] <code> end
    private void procUntil() {

    }

    // <for> ::= for <id> in <expr> [ do ] <code> end
    private void procFor() {

    }

    // <output> ::= ( puts | print ) [ <expr> ] [ <post> ] ';'
    private void procOutput() {

    }

    // <assign> ::= <access> { ',' <access> } '=' <expr> { ',' <expr> } [ <post> ] ';'
    private void procAssign() {

    }

    // <post> ::= ( if | unless ) <boolexpr>
    private void procPost() {

    }

    // <boolexpr> ::= [ not ] <cmpexpr> [ (and | or) <boolexpr> ]
    private void procBoolexpr() {

    }

    // <cmpexpr> ::= <expr> ( '==' | '!=' | '<' | '<=' | '>' | '>=' | '===' ) <expr>
    private void procCmpexpr() {

    }

    // <expr> ::= <arith> [ ( '..' | '...' ) <arith> ]
    private void procExpr() {

    }

    // <arith> ::= <term> { ('+' | '-') <term> }
    private void procArith() {

    }

    // <term> ::= <power> { ('*' | '/' | '%') <power> }
    private void procTerm() {

    }

    // <power> ::= <factor> { '**' <factor> }
    private void procPower() {

    }

    // <factor> ::= [ '+' | '-' ] ( <const> | <input> | <access> ) [ <function> ]
    private void procFactor() {

    }

    // <const> ::= <integer> | <string> | <array>
    private void procConst() {

    }

    // <input> ::= gets | rand
    private void procInput() {

    }

    // <array> ::= '[' [ <expr> { ',' <expr> } ] ']'
    private void procArray() {

    }

    // <access> ::= ( <id> | '(' <expr> ')' ) [ '[' <expr> ']' ]
    private void procAccess() {

    }

    // <function> ::= '.' ( length | to_i | to_s )
    private void procFunction() {

    }

}
