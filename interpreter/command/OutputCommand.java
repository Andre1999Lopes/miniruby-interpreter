package interpreter.command;

import interpreter.expr.Expr;
import interpreter.value.Value;

public class OutputCommand extends Command {
	private OutputOp op;
	private Expr expr;

	public OutputCommand(int line, OutputOp op, Expr expr) {
		super(line);
		this.op = op;
		this.expr = expr;
	}

	@Override
	public void execute() {
		if (this.expr != null) {
			Value<?> value = this.expr.expr();
			String string = value.toString();
			string = string.replace("'", "");
			
			if (this.op == OutputOp.PrintOp) System.out.print(string);
	
			else if (this.op == OutputOp.PutsOp) System.out.println(string);
		}
		else if (this.op == OutputOp.PutsOp) System.out.println();
	}
	
}
