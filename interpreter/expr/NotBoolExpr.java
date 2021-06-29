package interpreter.expr;

public class NotBoolExpr extends BoolExpr {

    private BoolExpr expr;

    public NotBoolExpr(BoolExpr expr, int line) {
        super(line);
        this.expr = expr;
    }

    @Override
    public boolean expr() {
        return !expr.expr();
    }

}
