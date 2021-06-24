package interpreter.expr;

import interpreter.value.Value;

public class ConstExpr extends Expr {
    private Value<?> value;

    protected ConstExpr(int line, Value<?> value) {
        super(line);
        this.value = value;
    }

    public Value<?> expr() {
        return this.value;
    }
}