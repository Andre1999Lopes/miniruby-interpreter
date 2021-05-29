package lexical;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class LexicalAnalysis implements AutoCloseable {

    private int line;
    private SymbolTable st;
    private PushbackInputStream input;

    public LexicalAnalysis(String filename) throws LexicalException {
        try {
            input = new PushbackInputStream(new FileInputStream(filename));
        } catch (Exception e) {
            throw new LexicalException("Unable to open file");
        }

        st = new SymbolTable();
        line = 1;
    }

    public void close() throws IOException {
        input.close();
    }

    public int getLine() {
        return this.line;
    }

    public Lexeme nextToken() throws LexicalException, IOException {
        Lexeme lex = new Lexeme("", TokenType.END_OF_FILE);

        int state = 1;
        while (state != 12 && state != 13) {
            int c = getc();
            
            switch (state) {
                case 1:
                    // TODO: Implement me!
                    break;
                case 2:
                    if (c == '\n') {
                        state = 1;
                    }
                    else if (c == -1) {
                        lex.type = TokenType.END_OF_FILE;
                    }
                    else {
                        state = 2;
                    }
                    break;
                case 3:
                    // TODO: Implement me!
                    break;
                case 4:
                    // TODO: Implement me!
                    break;
                case 5:
                    // TODO: Implement me!
                    break;
                case 6:
                    // TODO: Implement me!
                    break;
                case 7:
                    // TODO: Implement me!
                    break;
                case 8:
                    if (c == '=') {
                        lex.token += (char) c;
                        state = 12;
                    }
                    else {
                        if (c == -1) {
                            lex.type = TokenType.UNEXPECTED_EOF;
                            state = 13;
                        }
                        else {
                            lex.type = TokenType.INVALID_TOKEN;
                        }
                    }
                    break;
                case 9:
                    // TODO: Implement me!
                    break;
                case 10:
                    if (Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 6;
                    }
                    else {
                        if (c != -1) {
                            ungetc(c);
                        }
                        lex.type = TokenType.INTEGER;
                        state = 13;
                    }
                    break;
                case 11:
                    if (c != '\'') {
                        lex.token += (char) c;
                    }
                    else {
                        if (c == -1) {
                            lex.type = TokenType.UNEXPECTED_EOF;
                            state = 13;
                        }
                        else {
                            lex.token += (char) c;
                            lex.type = TokenType.STRING;
                            state = 13;
                        }
                    }
                    break;
                default:
                    throw new LexicalException("Unreachable");
            }
        }

        if (state == 12)
            lex.type = st.find(lex.token);

        return lex;
    }

    private int getc() throws LexicalException {
        try {
            return input.read();
        } catch (Exception e) {
            throw new LexicalException("Unable to read file");
        }
    }

    private void ungetc(int c) throws LexicalException {
        if (c != -1) {
            try {
                input.unread(c);
            } catch (Exception e) {
                throw new LexicalException("Unable to ungetc");
            }
        }
    }
}
