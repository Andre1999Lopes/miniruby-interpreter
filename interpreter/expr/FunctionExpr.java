package interpreter.expr;

import interpreter.value.Value;
import interpreter.value.*;
import interpreter.util.Utils;
import java.util.Vector;

public class FunctionExpr extends Expr {
	private Expr expr;
	private FunctionOp op;

	public FunctionExpr(int line, Expr expr, FunctionOp op) {
		super(line);
		this.expr = expr;
		this.op = op;
	}

	@Override
	public Value<?> expr() {
		Value<?> value = this.expr.expr();

		switch(this.op) {
			case LengthOp:
				if (!(value instanceof ArrayValue)) Utils.abort(super.getLine());
				else {
					ArrayValue arrayValue = (ArrayValue) value;
					Vector<Value<?>> vector = arrayValue.value();

					value = new IntegerValue(vector.size());
				}
			break;

			case ToIntOp:
				try {
					if (!(value instanceof StringValue)) Utils.abort(super.getLine());

					else value = new IntegerValue(Integer.parseInt(value.toString()));
				}
				catch(Exception e) {
					value = new IntegerValue(0);
				}
			break;

			case ToStringOp:
				value = new StringValue(value.toString());
			break;
				
		}
		return value;
	}
}
