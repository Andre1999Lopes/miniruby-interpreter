package interpreter.expr;

public class CompositeBoolExpr extends BoolExpr {

    private BoolExpr left;
    private BoolOp op;
    private BoolExpr right;

    public CompositeBoolExpr(BoolExpr left, BoolOp op, BoolExpr right, int line) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public boolean expr() {
        boolean booleanLeft = this.left.expr();
        boolean booleanRight = this.right.expr();
        switch (this.op) {
            case AND:
                return booleanLeft && booleanRight;
            case OR:
                return booleanLeft || booleanRight;
            default:
                return booleanLeft;
        }
    }

}
