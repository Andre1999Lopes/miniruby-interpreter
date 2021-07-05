package interpreter.command;

import java.util.ArrayList;
import java.util.List;
import interpreter.expr.Expr;
import interpreter.expr.SetExpr;
import interpreter.value.Value;
import interpreter.util.Utils;

public class AssignCommand extends Command {
	private List<Expr> left;
	private List<Expr> right;

	public AssignCommand(int line, List<Expr> left, List<Expr> right) {
		super(line);
		this.left = left;
		this.right = right;
	}

	@Override
	public void execute() {
		if (this.left.size() != this.right.size()) Utils.abort(super.getLine());

		List<Value<?>> values = new ArrayList<Value<?>>();

		for (Expr expr : this.right) {
			values.add(expr.expr());
		}

		for (int i = 0; i < this.left.size(); i++) {
			if (!(left.get(i) instanceof SetExpr)) Utils.abort(super.getLine());

			SetExpr setExpr = (SetExpr) left.get(i);
			setExpr.setValue(values.get(i));
		}
	}
	
}
