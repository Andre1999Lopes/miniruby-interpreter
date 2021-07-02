package interpreter.command;

import interpreter.expr.BoolExpr;

public class UnlessCommand extends Command {
	private BoolExpr cond;
	private Command thenCmds;
	private Command elseCmds;

	public UnlessCommand(int line, BoolExpr cond, Command thenCmds, Command elseCmds) {
		super(line);
		this.cond = cond;
		this.thenCmds = thenCmds;

		if (elseCmds != null) this.elseCmds = elseCmds;

		else this.elseCmds = null;
	}

	@Override
	public void execute() {
		
	}
	
}
