package interpreter.expr;

import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;
import interpreter.util.Utils;
import java.util.Random;
import java.util.Scanner;

public class InputExpr extends Expr {
	private InputOp op;

	public InputExpr (int line, InputOp op) {
		super(line);
		this.op = op;
	}

	@Override
	public Value<?> expr() {
		Value<?> value = null;
		Scanner scan = new Scanner(System.in);
		
		if (this.op == InputOp.GetsOp) {
			String input = scan.nextLine().trim();
			value = new StringValue(input);
		}
		else if (this.op == InputOp.RandOp) {
			Random random = new Random();
			value = new IntegerValue(random.nextInt());
		}
		else Utils.abort(super.getLine());

		return value;
	}
}
