package interpreter.expr;

import java.util.List;
import java.util.Vector;

import interpreter.value.ArrayValue;
import interpreter.value.Value;

public class ArrayExpr extends Expr {
    
	private List<Expr> exprs;

	public ArrayExpr (int line, List<Expr> exprs) {
		super(line);
		this.exprs = exprs;
	}

	@Override
	public Value<?> expr () {
		Vector<Value<?>> vector = new Vector<Value<?>>();
		
		for (Expr expr : exprs) {  
			Value<?> v = expr.expr();  
			vector.add(v);
		}

		ArrayValue arrayValue = new ArrayValue(vector);
		return arrayValue;
	}
}
