package interpreter.command;

import interpreter.expr.Expr;

public class OutputCommand extends Command {
	private OutputOp op;
	private Expr expr;

	public OutputCommand(int line, OutputOp op, Expr expr) {
		super(line);
		this.op = op;

		if (expr != null) this.expr = expr;

		else expr = null;
	}

	@Override
	public void execute() {
		
	}
	
}
