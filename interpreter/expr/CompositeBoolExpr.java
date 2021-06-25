package interpreter.expr;

public class CompositeBoolExpr extends BoolExpr {

    private BoolExpr left;
    private RelOp op;
    private BoolExpr right;

    public CompositeBoolExpr(BoolExpr left, RelOp op, BoolExpr right, int line) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public boolean expr() { // OBS: Não sei fazer essa parte do expr, porém, deve retornar um boolean
        return true;
    }

}
