package interpreter.expr;

public abstract class BoolExpr {
    private int line;

    public BoolExpr(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public boolean expr() { // OBS: Não sei fazer essa parte do expr, porém, deve retornar um boolean
        return true;
    }

}
