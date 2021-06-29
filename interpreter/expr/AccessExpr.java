package interpreter.expr;

import java.util.Vector;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;

public class AccessExpr extends SetExpr{
	private Expr base;
	private Expr index;

	public AccessExpr(int line, Expr base, Expr index) {
		super(line);
		this.base = base;
		this.index = index;
	}

	@Override
	public void setValue(Value<?> value) {
		SetExpr setExpr = (SetExpr) base;

		if (this.index != null) {
			Value<?> v = this.base.expr();
			if (!(v instanceof ArrayValue)) Utils.abort(super.getLine());

			else {
				Value<?> index = this.index.expr();
				ArrayValue array = (ArrayValue) value;
				Vector<Value<?>> vector = array.value();
				int ind = 0;

				if (index instanceof IntegerValue || index instanceof StringValue)
					ind = Integer.parseInt(index.toString());

				else Utils.abort(super.getLine());

				vector.set(ind, v);
				ArrayValue newArray = new ArrayValue(vector);

				setExpr.setValue(newArray);
			}
		}
		else {
			if (value instanceof IntegerValue) {
				IntegerValue integerValue = (IntegerValue) value;
				setExpr.setValue(new IntegerValue(integerValue.value()));
			}
			else if (value instanceof StringValue) {
				StringValue stringValue = (StringValue) value;
				setExpr.setValue(new StringValue(stringValue.value()));
			}
			else setExpr.setValue(value);
		}
	}

	@Override
	public Value<?> expr() {
		Value<?> value = this.base.expr();

		if (this.index != null) {
			if (!(value instanceof ArrayValue)) Utils.abort(super.getLine());

			else {
				Value<?> index = this.index.expr();
				ArrayValue array = (ArrayValue) value;
				Vector<Value<?>> vector = array.value();
				int ind = 0;

				if (index instanceof IntegerValue || index instanceof StringValue)
					ind = Integer.parseInt(index.toString());
				
				else Utils.abort(super.getLine());

				return vector.get(ind);
			}
		}

		return value;
	}
}
