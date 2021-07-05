package interpreter.expr;

import java.util.Vector;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;

public class BinaryExpr extends Expr {
		
	private Expr left;
	private Expr right;
	private BinaryOp op;

	public BinaryExpr (int line, Expr left, Expr right, BinaryOp op) {
		super(line);
		this.left = left;
		this.right = right;
		this.op = op;
	}

	@Override
	public Value<?> expr () {
		Value<?> value = null;
		Value<?> left = this.left.expr();
		Value<?> right = this.right.expr();

		switch (this.op) {
			case RangeWithOp:
				if (left instanceof IntegerValue && right instanceof IntegerValue) {
					int leftValue = Integer.parseInt(left.toString());
					int rightValue = Integer.parseInt(right.toString());
					Vector<Value<?>> result = new Vector<Value<?>>();

					for (int i = leftValue; i <= rightValue + 1; i++) {
						result.add(new IntegerValue(i));
					}
					
					value = new ArrayValue(result);
				}
				else Utils.abort(super.getLine());
			break;

				case RangeWithoutOp:
					if (left instanceof IntegerValue && right instanceof IntegerValue) {
						int leftValue = Integer.parseInt(left.toString());
						int rightValue = Integer.parseInt(right.toString());
						Vector<Value<?>> result = new Vector<Value<?>>();
						
						for (int i = leftValue; i < rightValue; i++) {
							result.add(new IntegerValue(i));
						}
						
						value = new ArrayValue(result);
					}
					else {
						Utils.abort(super.getLine());
					}
				break;

				case AddOp:
					if (left instanceof IntegerValue && right instanceof IntegerValue) {
						int leftValue = Integer.parseInt(left.toString());
						int rightValue = Integer.parseInt(right.toString());

						int result = leftValue + rightValue;

						value = new IntegerValue (result);
					}
					else if (left instanceof ArrayValue && right instanceof ArrayValue) {
						Vector<Value<?>> result = new Vector<Value<?>>();
						
						ArrayValue leftValue = (ArrayValue) left;
						Vector<Value<?>> leftVector = leftValue.value();
						ArrayValue rightValue = (ArrayValue) right;
						Vector<Value<?>> rightVector = rightValue.value();

						result.addAll(leftVector);
						result.addAll(rightVector);

						value = new ArrayValue(result);
					}
					else {
						String leftValue = left.toString();
						leftValue = leftValue.replace("'", "");

						String rightValue = right.toString();
						rightValue = rightValue.replace("'", "");

						String result = "'" + leftValue + rightValue + "'";

						value = new StringValue (result);
					}
				break;

				case SubOp:
					if (left instanceof IntegerValue && right instanceof IntegerValue) {
						int leftValue = Integer.parseInt(left.toString());
						int rightValue = Integer.parseInt(right.toString());

						int result = leftValue - rightValue;

						value = new IntegerValue (result);
					}
					else {
						Utils.abort(super.getLine());
					}
				break;

				case MulOp:
					if (left instanceof IntegerValue && right instanceof IntegerValue) {
						int leftValue = Integer.parseInt(left.toString());
						int rightValue = Integer.parseInt(right.toString());

						int result = leftValue * rightValue;

						value = new IntegerValue (result);
					}
					else {
						Utils.abort(super.getLine());
					}
				break;

				case DivOp:
					if (left instanceof IntegerValue && right instanceof IntegerValue) {
						int leftValue = Integer.parseInt(left.toString());
						int rightValue = Integer.parseInt(right.toString());

						int result = leftValue / rightValue;

						value = new IntegerValue(result);
					}
					else {
						Utils.abort(super.getLine());
					}
				break;

				case ModOp:
					if (left instanceof IntegerValue && right instanceof IntegerValue) {
						int leftValue = Integer.parseInt(left.toString());
						int rightValue = Integer.parseInt(right.toString());

						int result = leftValue % rightValue;

						value = new IntegerValue(result);
					}
					else {
						Utils.abort(super.getLine());
					}
				break;

				case ExpOp:
					if (left instanceof IntegerValue && right instanceof IntegerValue) {
						int leftValue = Integer.parseInt(left.toString());
						int rightValue = Integer.parseInt(right.toString());

						int result = (int) Math.pow(leftValue,rightValue);

						value = new IntegerValue(result);
					} else {
						Utils.abort(super.getLine());
					}
				break;
		}

		return value;
	}
}
