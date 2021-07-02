package interpreter.command;

import java.util.List;
import interpreter.expr.Expr;

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
		
	}
	
}
