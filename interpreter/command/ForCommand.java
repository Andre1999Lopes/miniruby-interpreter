package interpreter.command;

import java.util.Vector;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.IntegerValue;
import interpreter.value.Value;

public class ForCommand extends Command {
	private Variable var;
	private Expr expr;
	private Command cmds;

	public ForCommand(int line, Variable var, Expr expr, Command cmds) {
		super(line);
		this.var = var;
		this.expr = expr;
		this.cmds = cmds;
	}

	@Override
	public void execute() {
		Value<?> value = this.expr.expr();

		if (value instanceof IntegerValue) {
			int i = 0;
			int cond = Integer.parseInt(value.toString());
			
			while (i < cond) {
				this.cmds.execute();
				i++;
				var.setValue(new IntegerValue(i));
			}
		}
		else if (value instanceof ArrayValue) {
			ArrayValue arrayValue = (ArrayValue) value;
			Vector<Value<?>> vector = arrayValue.value();
			int i = Integer.parseInt(vector.get(0).toString());
			var.setValue(new IntegerValue(i));
			int cond = Integer.parseInt(vector.get(vector.size() - 1).toString());

			while (i < cond) {
				this.cmds.execute();
				i++;
				var.setValue(new IntegerValue(i));
			}
		}
		else Utils.abort(super.getLine());
	}
	
}
