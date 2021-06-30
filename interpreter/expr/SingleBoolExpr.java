package interpreter.expr;

import java.util.Vector;

import interpreter.expr.RelOp;
import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;

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
	public boolean expr() {
		Value<?> left = this.leftExpr.expr();
		Value<?> right = this.rightExpr.expr();
		if (left instanceof IntegerValue && right instanceof IntegerValue) {

			IntegerValue integerValueLeft = (IntegerValue) left;
			IntegerValue integerValueRight = (IntegerValue) right;
			int integerLeft = integerValueLeft.value();
			int integerRight = integerValueRight.value();

			switch (this.op) {
				case EqualsOp:
					return integerLeft == integerRight;
				case NotEqualsOp:
					return integerLeft != integerRight;
				case LowerThanOp:
					return integerLeft < integerRight;
				case LowerEqualOp:
					return integerLeft <= integerRight;
				case GreaterThanOp:
					return integerLeft > integerRight;
				case GreaterEqualOp:
					return integerLeft >= integerRight;
				default:
					Utils.abort(super.getLine());
			}
		} else if (left instanceof StringValue && right instanceof StringValue) {
			StringValue stringValueLeft = (StringValue) left;
			StringValue stringValueRight = (StringValue) right;
			String stringLeft = stringValueLeft.value();
			String stringRight = stringValueRight.value();

			switch (this.op) {
				case EqualsOp:
					return stringLeft.equals(stringRight);
				case NotEqualsOp:
					return !stringLeft.equals(stringRight);
				default:
					Utils.abort(super.getLine());
			}

		} else if (left instanceof StringValue && right instanceof ArrayValue
				|| left instanceof IntegerValue && right instanceof ArrayValue) {
			ArrayValue arrayValueRight = (ArrayValue) right;
			Vector<Value<?>> vector = arrayValueRight.value();

			return vector.contains(left);

		}
		return false;
	}

}
