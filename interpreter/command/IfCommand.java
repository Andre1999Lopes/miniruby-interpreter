package interpreter.command;

import interpreter.expr.BoolExpr;

public class IfCommand extends Command{
	private BoolExpr cond;
	private Command thenCmds;
	private Command elseCmds;

	public IfCommand(int line, BoolExpr cond, Command thenCmds) {
		super(line);
		this.cond = cond;
		this.thenCmds = thenCmds;
	}

	public void setElseCommands(Command elseCmds) {
		this.elseCmds = elseCmds;
	}

	@Override
	public void execute() {
		if (this.cond.expr()) this.thenCmds.execute();

		else if (this.elseCmds != null) this.elseCmds.execute();
	}
	
}
