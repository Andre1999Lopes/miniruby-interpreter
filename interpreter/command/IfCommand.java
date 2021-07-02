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

	}

	@Override
	public void execute() {
		
	}
	
}
