package syntatic;


import interpreter.expr.ConstExpr;
import interpreter.value.*;

import java.util.ArrayList;
import java.util.List;

import interpreter.command.*;
import interpreter.expr.*;
import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;

public class SyntaticAnalysis {

	private LexicalAnalysis lex;
	private Lexeme current;

	public SyntaticAnalysis(LexicalAnalysis lex) {
		this.lex = lex;
		this.current = lex.nextToken();
	}

	public Command start() {
		Command command = procCode();
		eat(TokenType.END_OF_FILE);
		return command;
	}

	private void advance() {
		// System.out.println("Advanced (\"" + current.token + "\", " +
		//     current.type + ")");
		current = lex.nextToken();
	}

	private void eat(TokenType type) {
		// System.out.println("Expected (..., " + type + "), found (\"" + 
		// 	current.token + "\", " + current.type + ")");
		if (type == current.type) {
			current = lex.nextToken();
		} else {
			showError();
		}
	}

	private void showError() {
		System.out.printf("%02d: ", lex.getLine());

		switch (current.type) {
			case INVALID_TOKEN:
				System.out.printf("Lexema inválido [%s]\n", current.token);
				break;
			case UNEXPECTED_EOF:
			case END_OF_FILE:
				System.out.printf("Fim de arquivo inesperado\n");
				break;
			default:
				System.out.printf("Lexema não esperado [%s]\n", current.token);
				break;
		}

		System.exit(1);
	}

	// <code> ::= { <cmd> }
	private BlocksCommand procCode() {
		List<Command> commands = new ArrayList<Command>();
		procCmd();
		while (current.type == TokenType.IF ||
				current.type == TokenType.UNLESS ||
				current.type == TokenType.WHILE ||
				current.type == TokenType.UNTIL ||
				current.type == TokenType.FOR ||
				current.type == TokenType.PUTS ||
				current.type == TokenType.PRINT ||
				current.type == TokenType.ID ||
				current.type == TokenType.OPEN_PAR
			) {
			commands.add(procCmd());
		}
		return new BlocksCommand(lex.getLine(), commands);
	}

	// <cmd> ::= <if> | <unless> | <while> | <until> | <for> | <output> | <assign>
	private Command procCmd() {
		Command command = null;
		switch(current.type) {
			case IF:
				command = procIf();
				break;

			case UNLESS:
				command = procUnless();
				break;

			case WHILE:
				command = procWhile();
				break;

			case UNTIL:
				command = procUntil();
				break;

			case FOR:
				command = procFor();
				break;

			case PUTS:
			case PRINT:
				command = procOutput();
				break;

			case ID:
			case OPEN_PAR:
				command = procAssign();
				break;

			default:
				showError();
		}
		return command;
	}

	// <if> ::= if <boolexpr> [ then ] <code> { elsif <boolexpr> [ then ] <code> } [ else <code> ] end
	private IfCommand procIf() {
		eat(TokenType.IF);
		BoolExpr boolExpr = procBoolexpr();

		if(current.type == TokenType.THEN) advance();

		Command thenCommand = procCode();
		// TODO: classe IfCommand
		IfCommand ifCommand = new IfCommand(lex.getLine(), boolExpr, thenCommand);
		List<IfCommand> arrayList = new ArrayList<IfCommand>();

		for (int i = 0; current.type == TokenType.ELSIF; i++) {
			advance();
			BoolExpr boolExpr_aux = procBoolexpr();
			
			if(current.type == TokenType.THEN) advance();
			
			Command thenCommand_aux = procCode();
			arrayList.add(new IfCommand(lex.getLine(), boolExpr_aux, thenCommand_aux));

			if (arrayList.size() > 1) arrayList.get(i-1).setElseCommands(arrayList.get(i));
		}		
		
		Command elseCommand = null;
		if (current.type == TokenType.ELSE) {
			advance();
			elseCommand = procCode();					
		}

		if (!(arrayList.isEmpty())) {
			arrayList.get(arrayList.size()-1).setElseCommands(elseCommand);						
			ifCommand.setElseCommands(arrayList.get(0));
		} 
		else ifCommand.setElseCommands(elseCommand);

		eat(TokenType.END);

		return ifCommand;
	}

	// <unless> ::= unless <boolexpr> [ then ] <code> [ else <code> ] end 
	private UnlessCommand procUnless() {
		eat(TokenType.UNLESS);		
		BoolExpr boolExpr = procBoolexpr();

		if(current.type == TokenType.THEN) {
			advance();
		}

		Command thenCommand = procCode();
		Command elseCommand = null;

		if(current.type == TokenType.ELSE) {
			advance();
			elseCommand = procCode();
		}

		eat(TokenType.END);
		// TODO: classe UnlessCommand
		return new UnlessCommand(lex.getLine(), boolExpr, thenCommand, elseCommand);
	}

	// <while> ::= while <boolexpr> [ do ] <code> end
	private WhileCommand procWhile() {
		eat(TokenType.WHILE);
		BoolExpr boolExpr = procBoolexpr();

		if(current.type == TokenType.DO) advance();

		Command command = procCode();
		eat(TokenType.END);
		// TODO: classe WhileCommand
		return new WhileCommand(lex.getLine(), boolExpr, command);
	}

	// <until> ::= until <boolexpr> [ do ] <code> end
	private UntilCommand procUntil() {
		eat(TokenType.UNTIL);
		BoolExpr boolExpr = procBoolexpr();

		if(current.type == TokenType.DO) advance();

		Command command = procCode();
		eat(TokenType.END);
		// TODO: classe UntilCommand
		return new UntilCommand (lex.getLine(), boolExpr, command);
	}

	// <for> ::= for <id> in <expr> [ do ] <code> end
	private ForCommand procFor() {
		eat(TokenType.FOR);
		Variable var = procId();
		eat(TokenType.IN);
		Expr expr = procExpr();

		if (current.type == TokenType.DO) advance();

		Command command = procCode();
		eat(TokenType.END);
		// TODO: classe ForCommand
		return new ForCommand (lex.getLine(), var, expr, command);
	}

	// <output> ::= ( puts | print ) [ <expr> ] [ <post> ] ';'
	private Command procOutput() {
		OutputOp op = null;
		Expr expr = null;
		Command command = null;

		if (current.type == TokenType.PUTS) {
            op = OutputOp.PutsOp;
			advance();
        }
		else if (current.type == TokenType.PRINT) {
			op = OutputOp.PrintOp;
            advance();
        }
		else showError();		
	
        if (current.type == TokenType.ADD      ||
			current.type == TokenType.SUB      ||
			current.type == TokenType.INTEGER  ||
            current.type == TokenType.STRING   ||
			current.type == TokenType.OPEN_PAR ||
			current.type == TokenType.GETS 	   ||
            current.type == TokenType.RAND 	   ||
			current.type == TokenType.ID 	   ||
			current.type == TokenType.OPEN_BRA
		) {
            expr = procExpr();
        }		
		
		// TODO: classe OutputCommand
        OutputCommand outputCommand = new OutputCommand(lex.getLine(), op, expr);

		if (current.type == TokenType.IF || current.type == TokenType.UNLESS) command = procPost(outputCommand);

        else command = outputCommand;

        eat(TokenType.SEMI_COLON);
		
		return command;
	}

	// <assign> ::= <access> { ',' <access> } '=' <expr> { ',' <expr> } [ <post> ] ';'
	private Command procAssign() {
		List<Expr> left = new ArrayList<Expr>();
		List<Expr> right = new ArrayList<Expr>();
		Command command = null;
		left.add(procAccess());

		while (current.type == TokenType.COMMA) {
			advance();
			left.add(procAccess());
		}

		eat(TokenType.ASSIGN);
		right.add(procExpr());

		while (current.type == TokenType.COMMA) {
			advance();
			right.add(procExpr());
		}

		// TODO: classe AssignCommand
		AssignCommand assignCommand = new AssignCommand(lex.getLine(), left, right);

		if (current.type == TokenType.IF ||current.type == TokenType.UNLESS) command = procPost(assignCommand);

        else command = assignCommand;

		eat(TokenType.SEMI_COLON);

		return command;
	}

	// <post> ::= ( if | unless ) <boolexpr>
	private Command procPost(Command inCommand) {
		BoolExpr boolExpr = null;
		TokenType op = null;
		Command command = null;

		if (current.type == TokenType.IF) {
			op = TokenType.IF;
			advance();
		}
		else if (current.type == TokenType.UNLESS) {
			op = TokenType.UNLESS;
			advance();
		}

		boolExpr = procBoolexpr();

		// TODO: classes IfCommand e UnlessCommand
		if(op == TokenType.IF) {
			IfCommand ifCommand = new IfCommand(lex.getLine(), boolExpr, inCommand);
			command = ifCommand;
		}
		else if (op == TokenType.UNLESS) {
			UnlessCommand unlessCommand = new UnlessCommand(lex.getLine(), boolExpr, inCommand, null);
			command = unlessCommand;			
		}		

		return command;
	}

	// <boolexpr> ::= [ not ] <cmpexpr> [ (and | or) <boolexpr> ]
	private BoolExpr procBoolexpr() {
		BoolExpr boolExpr = null;
		BoolExpr left = null;
		BoolExpr right = null;
		boolean not = false;
		BoolOp op = null;

		if (current.type == TokenType.NOT) {
			not = true;
			advance();
		}

		left = procCmpexpr();

		if (
			current.type == TokenType.AND ||
			current.type == TokenType.OR
			) {
			if (current.type == TokenType.AND) op = BoolOp.AND;

			else if (current.type == TokenType.OR) op = BoolOp.OR;

			advance();
			right = procBoolexpr();
		}
		
		if (op != null) {
			CompositeBoolExpr compositeBoolExpr = new CompositeBoolExpr(left, op, right, lex.getLine());

			if (not) { 
				boolExpr = new NotBoolExpr(compositeBoolExpr, lex.getLine()); 
			}
			else boolExpr = compositeBoolExpr;
		} 
		else boolExpr = left;

		return boolExpr;
	}

	// <cmpexpr> ::= <expr> ( '==' | '!=' | '<' | '<=' | '>' | '>=' | '===' ) <expr>
	private BoolExpr procCmpexpr() {
		Expr left = procExpr();
		Expr right = null;
		RelOp op = null;

		if (
			current.type == TokenType.EQUALS     ||
			current.type == TokenType.NOT_EQUALS ||
			current.type == TokenType.LOWER      || 
			current.type == TokenType.LOWER_EQ   ||
			current.type == TokenType.GREATER    ||
			current.type == TokenType.GREATER_EQ ||
			current.type == TokenType.CONTAINS
			) {
				if(current.type == TokenType.EQUALS) op = RelOp.EqualsOp;

				else if (current.type == TokenType.NOT_EQUALS) op = RelOp.NotEqualsOp;

				else if (current.type == TokenType.LOWER) op = RelOp.LowerThanOp;

				else if (current.type == TokenType.LOWER_EQ) op = RelOp.LowerEqualOp;

				else if (current.type == TokenType.GREATER) op = RelOp.GreaterThanOp;

				else if (current.type == TokenType.GREATER_EQ) op = RelOp.GreaterEqualOp;

				else op = RelOp.ContainsOp;
				
				advance();
		}
		else showError();

		right = procExpr();
		SingleBoolExpr singleBoolExpr = new SingleBoolExpr(left, op, right, lex.getLine());
		BoolExpr boolExpression = singleBoolExpr;

		return boolExpression;
	}

	// <expr> ::= <arith> [ ( '..' | '...' ) <arith> ]
	private Expr procExpr() {
		Expr expr = null;
		Expr left = procArith();
		Expr right = null;
		BinaryOp op = null;

		if (
			current.type == TokenType.RANGE_WITH ||
			current.type == TokenType.RANGE_WITHOUT
		) {
			if (current.type == TokenType.RANGE_WITH) op = BinaryOp.RangeWithOp;

			else if (current.type == TokenType.RANGE_WITHOUT) op = BinaryOp.RangeWithoutOp;

			advance();
			right = procArith();
		}
		
		if (op != null) {
			BinaryExpr binaryExpr = new BinaryExpr(lex.getLine(), left, right, op);
			expr = binaryExpr;
		}
		else expr = left;

		return expr;
	}

	// <arith> ::= <term> { ('+' | '-') <term> }
	private Expr procArith() {
		Expr left = procTerm();
		Expr right = null;
		BinaryOp op = null;

		while (
				current.type == TokenType.ADD ||
				current.type == TokenType.SUB)
			{
			if (current.type == TokenType.ADD) op = BinaryOp.AddOp;

			else if (current.type == TokenType.SUB) op = BinaryOp.SubOp;

			advance();
			right = procTerm();
			BinaryExpr binaryExpr = new BinaryExpr(lex.getLine(), left, right, op);
			left = binaryExpr;
		}

		return left;
	}

	// <term> ::= <power> { ('*' | '/' | '%') <power> }
	private Expr procTerm() {
		Expr left = procPower();
		Expr right = null;
		BinaryOp op = null;

		while (
				current.type == TokenType.MUL ||
				current.type == TokenType.DIV ||
				current.type == TokenType.MOD
		) {
			if (current.type == TokenType.MUL) op = BinaryOp.MulOp;

			else if (current.type == TokenType.DIV) op = BinaryOp.DivOp;

			else if (current.type == TokenType.MOD) op = BinaryOp.ModOp;

			advance();
			right = procTerm();
			BinaryExpr binaryExpr = new BinaryExpr(lex.getLine(), left, right, op);
			left = binaryExpr;
		}

		return left;
	}

	// <power> ::= <factor> { '**' <factor> }
	private Expr procPower() {
		Expr left = procFactor();
		Expr right = null;
		BinaryOp op = null;

		while (current.type == TokenType.EXP) {
			op = BinaryOp.ExpOp;
			advance();
			right = procFactor();
			BinaryExpr binaryExpr = new BinaryExpr(lex.getLine(), left, right, op);
			left = binaryExpr;
		}

		return left;
	}

	// <factor> ::= [ '+' | '-' ] ( <const> | <input> | <access> ) [ <function> ]
	private Expr procFactor() {
		ConvOp op = null;
		Expr expr = null;

		if (current.type == TokenType.ADD) {
			op = ConvOp.PlusOp;
			advance();
		}
		else if (current.type == TokenType.SUB) {
			op = ConvOp.MinusOp;
			advance();
		}

		switch(current.type) {
			case INTEGER:
			case STRING:
			case OPEN_BRA:
				expr = procConst();
				break;

			case GETS:
			case RAND:
				procInput();
				break;

			case ID:
			case OPEN_PAR:
				expr = procAccess();
				break;

			default:
				showError();
		}

		if (current.type == TokenType.DOT) {
			FunctionExpr functionExpr = procFunction(expr);
			expr = functionExpr;
		}

		if (op != null) {
			ConvExpr convExpr = new ConvExpr(lex.getLine(), op, expr);
			expr = convExpr;
		}

		return expr;
	}

	// <const> ::= <integer> | <string> | <array>
	private Expr procConst() {
		Expr expr = null;

		switch(current.type) {
			case INTEGER:
				expr = procInteger();
				break;

			case STRING:
				expr = procString();
				break;

			case OPEN_BRA:
				expr = procArray();
				break;

			default:
				showError();
		}

		return expr;
	}

	// <input> ::= gets | rand
	private InputExpr procInput() {
		InputOp op = null;
		
		if (current.type == TokenType.GETS) {
			op = InputOp.GetsOp;
			advance();
		}
		else if (current.type == TokenType.RAND) {
			op = InputOp.RandOp;
			advance();
		}
		else showError();

		return new InputExpr(lex.getLine(), op);
	}

	private ConstExpr procInteger() {
		String token = current.token;
		eat(TokenType.INTEGER);
		int value;

		try {
			value = Integer.parseInt(token);
		}
		catch (Exception e) {
			value = 0;
		}

		return new ConstExpr(lex.getLine(), new IntegerValue(value));
	}

	private ConstExpr procString() {
		String token = current.token;
		eat(TokenType.STRING);
		return new ConstExpr(lex.getLine(), new StringValue(token));
	}

	// <array> ::= '[' [ <expr> { ',' <expr> } ] ']'
	private ArrayExpr procArray() {
		List<Expr> exprList = new ArrayList<Expr>();
		eat(TokenType.OPEN_BRA);

		switch(current.type) {
			case ADD:
			case SUB:
			case INTEGER:
			case STRING:
			case OPEN_BRA:
			case GETS:
			case RAND:
			case ID:
			case OPEN_PAR:
				exprList.add(procExpr());
				
				while (current.type == TokenType.COMMA) {
					advance();
					exprList.add(procExpr());
				}
				eat(TokenType.CLOSE_BRA);
				break;

			default:
				eat(TokenType.CLOSE_BRA);
		}

		ArrayExpr arrayExpr = new ArrayExpr(lex.getLine(), exprList);
		return arrayExpr;
	}

	// <access> ::= ( <id> | '(' <expr> ')' ) [ '[' <expr> ']' ]
	private Expr procAccess() {
		Expr base = null;
		Expr index = null;

		if (current.type == TokenType.ID) base = procId();

		else if (current.type == TokenType.OPEN_PAR) {
			advance();
			base = procExpr();
			eat(TokenType.CLOSE_PAR);
	   }
	   else showError();

	   if (current.type == TokenType.OPEN_BRA) {
		   advance();
		   index = procExpr();
		   eat(TokenType.CLOSE_BRA);
	   }

	   AccessExpr accessExpr = new AccessExpr(lex.getLine(), base, index);
	   Expr expr = accessExpr;
	   
	   return expr;
	}

	// <function> ::= '.' ( length | to_i | to_s )
	private FunctionExpr procFunction(Expr expr) {
		FunctionOp op = null;

		eat(TokenType.DOT);

		switch (current.type) {
			case LENGTH:
				op = FunctionOp.LengthOp;
				advance();
				break;
			
			case TO_INT:
				op = FunctionOp.ToIntOp;
				advance();
				break;
			
			case TO_STR:
				op = FunctionOp.ToStringOp;
				advance();
				break;

			default:
				showError();
		}

		return new FunctionExpr(lex.getLine(), expr, op);
	}

	private Variable procId () {
		String token = current.token;
		eat(TokenType.ID);
		return new Variable(lex.getLine(), token);
	}

}
