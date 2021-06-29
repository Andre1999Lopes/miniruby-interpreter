package interpreter.expr;

public class SingleBoolExpr extends BoolExpr {

    private Expr leftExpr;
    private RelOp op;
    private Expr rightExpr;

    public SingleBoolExpr(Expr leftExpr, RelOp op, Expr rightExpr, int line) {
        super(line);
        this.leftExpr = leftExpr;
        this.op = op;
        this.rightExpr = rightExpr;
    }

    @Override
    public boolean expr() { // OBS: Não sei fazer essa parte do expr, porém, deve retornar um boolean
        return true;
    }

}
