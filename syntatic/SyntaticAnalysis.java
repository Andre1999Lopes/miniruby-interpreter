package syntatic;


import interpreter.expr.ConstExpr;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
// import interpreter.command.Command;
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

	public void start() {
		procCode();
		// eat(TokenType.END_OF_FILE);
	}

	private void advance() {
		System.out.println("Advanced (\"" + current.token + "\", " +
		    current.type + ")");
		current = lex.nextToken();
	}

	private void eat(TokenType type) {
		System.out.println("Expected (..., " + type + "), found (\"" + 
			current.token + "\", " + current.type + ")");
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
	private void procCode() {
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
				procCmd();
			}
	}

	// <cmd> ::= <if> | <unless> | <while> | <until> | <for> | <output> | <assign>
	private void procCmd() {
		switch(current.type) {
			case IF:
				procIf();
				break;

			case UNLESS:
				procUnless();
				break;

			case WHILE:
				procWhile();
				break;

			case UNTIL:
				procUntil();
				break;

			case FOR:
				procFor();
				break;

			case PUTS:
			case PRINT:
				procOutput();
				break;

			case ID:
			case OPEN_PAR:
				procAssign();
				break;

			default:
				showError();
		}
	}

	// <if> ::= if <boolexpr> [ then ] <code> { elsif <boolexpr> [ then ] <code> } [ else <code> ] end
	private void procIf() {
		eat(TokenType.IF);
		procBoolexpr();

		if (current.type == TokenType.THEN) advance();

		procCode();

		while (current.type == TokenType.ELSIF) {
			advance();
			procBoolexpr();
			if (current.type == TokenType.THEN) advance();
			procCode();
		}
		if (current.type == TokenType.ELSE) {
			advance();
			procCode();
		}

		eat(TokenType.END);
	}

	// <unless> ::= unless <boolexpr> [ then ] <code> [ else <code> ] end 
	private void procUnless() {
		eat(TokenType.UNLESS);
		procBoolexpr();

		if (current.type == TokenType.THEN) advance();

		procCode();
		
		if (current.type == TokenType.ELSE) {
			advance();
			procCode();
		}

		eat(TokenType.END);
	}

	// <while> ::= while <boolexpr> [ do ] <code> end
	private void procWhile() {
		eat(TokenType.WHILE);
		procBoolexpr();

		if (current.type == TokenType.DO) advance();

		procCode();
		eat(TokenType.END);
	}

	// <until> ::= until <boolexpr> [ do ] <code> end
	private void procUntil() {
		eat(TokenType.UNTIL);
		procBoolexpr();

		if (current.type == TokenType.DO) advance();

		procCode();
		eat(TokenType.END);
	}

	// <for> ::= for <id> in <expr> [ do ] <code> end
	private void procFor() {
		eat(TokenType.FOR);
		eat(TokenType.ID);
		eat(TokenType.IN);
		procExpr();

		if (current.type == TokenType.DO) advance();

		procCode();
		eat(TokenType.END);
	}

	// <output> ::= ( puts | print ) [ <expr> ] [ <post> ] ';'
	private void procOutput() {
		if (current.type == TokenType.PUTS || current.type == TokenType.PRINT) {
			advance();

			if (
				current.type == TokenType.ADD ||
				current.type == TokenType.SUB ||
				current.type == TokenType.INTEGER ||
				current.type == TokenType.STRING ||
				current.type == TokenType.ID ||
				current.type == TokenType.OPEN_PAR
			) {
				procExpr();
			}

			if (current.type == TokenType.IF || current.type == TokenType.UNLESS) procPost();
			
			eat(TokenType.SEMI_COLON);
		}
		else showError();
		
	}

	// <assign> ::= <access> { ',' <access> } '=' <expr> { ',' <expr> } [ <post> ] ';'
	private void procAssign() {
		procAccess();

		if (current.type == TokenType.COMMA) {
			advance();
			procAccess();
		}

		eat(TokenType.ASSIGN);
		procExpr();

		if (current.type == TokenType.COMMA) {
			advance();
			procExpr();
		}

		if (current.type == TokenType.IF || current.type == TokenType.UNLESS) {
			procPost();
		}
		eat(TokenType.SEMI_COLON);
	}

	// <post> ::= ( if | unless ) <boolexpr>
	private void procPost() {
		if (
			current.type == TokenType.IF ||
			current.type == TokenType.UNLESS
		) {
			advance();
			procBoolexpr();
		}
		else showError();
	}

	// <boolexpr> ::= [ not ] <cmpexpr> [ (and | or) <boolexpr> ]
	private void procBoolexpr() {
		if (current.type == TokenType.NOT) advance();
		procCmpexpr();

		if (
			current.type == TokenType.AND ||
			current.type == TokenType.OR
		) {
			advance();
			procBoolexpr();
		}
	}

	// <cmpexpr> ::= <expr> ( '==' | '!=' | '<' | '<=' | '>' | '>=' | '===' ) <expr>
	private void procCmpexpr() {
		procExpr();
		switch(current.type) {
			case EQUALS:
			case NOT_EQUALS:
			case LOWER:
			case LOWER_EQ:
			case GREATER:
			case GREATER_EQ:
			case CONTAINS:
				advance();
				procExpr();
				break;

			default:
				showError();
				break;
		}
	}

	// <expr> ::= <arith> [ ( '..' | '...' ) <arith> ]
	private void procExpr() {
		procArith();

		if (
				current.type == TokenType.RANGE_WITH ||
				current.type == TokenType.RANGE_WITHOUT) {
			advance();
			procArith();
		}
	}

	// <arith> ::= <term> { ('+' | '-') <term> }
	private void procArith() {
		procTerm();

		while (
			current.type == TokenType.ADD ||
			current.type == TokenType.SUB
		) {
			advance();
			procArith();
		}
	}

	// <term> ::= <power> { ('*' | '/' | '%') <power> }
	private void procTerm() {
		procPower();

		while (
					current.type == TokenType.MUL ||
					current.type == TokenType.DIV ||
					current.type == TokenType.MOD
		) {
			advance();
			procPower();
		}
	}

	// <power> ::= <factor> { '**' <factor> }
	private void procPower() {
		procFactor();

		while (current.type == TokenType.EXP) {
			advance();
			procFactor();
		}
	}

	// <factor> ::= [ '+' | '-' ] ( <const> | <input> | <access> ) [ <function> ]
	private void procFactor() {
		if (current.type == TokenType.ADD || current.type == TokenType.SUB) advance();

		switch(current.type) {
			case INTEGER:
			case STRING:
			case OPEN_BRA:
				procConst();
				break;

			case GETS:
			case RAND:
				procInput();
				break;

			case ID:
			case OPEN_PAR:
				procAccess();
				break;

			default:
				showError();
				break;
		}

		if (current.type == TokenType.DOT) {
			procFunction();
		}
	}

	// <const> ::= <integer> | <string> | <array>
	private void procConst() {
		switch(current.type) {
			case INTEGER:
				procInteger();
				break;

			case STRING:
				procString();
				break;

			case OPEN_BRA:
				procArray();
				break;

			default:
				showError();
				break;
		}
	}

	// <input> ::= gets | rand
	private void procInput() {
		if (
				current.type == TokenType.GETS ||
				current.type == TokenType.RAND
		) {
			advance();
		}
	}

	private ConstExpr procInteger() {
		String var = current.token;
		advance();
		int value = Integer.parseInt(var);
		ConstExpr expr = new ConstExpr(lex.getLine(), new IntegerValue(value));
		return expr;
	}

	private ConstExpr procString() {
		String var = current.token;
		advance();
		ConstExpr expr = new ConstExpr(lex.getLine(), new StringValue(var));
		return expr;
	}

	// <array> ::= '[' [ <expr> { ',' <expr> } ] ']'
	private void procArray() {
		eat(TokenType.OPEN_BRA);

		switch(current.type) {
			case ADD:
			case SUB:
			case INTEGER:
			case STRING:
			case OPEN_BRA:
			case ID:
			case OPEN_PAR:
				procExpr();
				while (current.type == TokenType.COMMA) {
					advance();
					procExpr();
				}
				eat(TokenType.CLOSE_BRA);
				break;
			default:
				eat(TokenType.CLOSE_BRA);
				break;
		}
	}

	// <access> ::= ( <id> | '(' <expr> ')' ) [ '[' <expr> ']' ]
	private void procAccess() {
		if (current.type == TokenType.ID) {
			advance();
		}
		else {
			eat(TokenType.OPEN_PAR);
			procExpr();
			eat(TokenType.CLOSE_PAR);
		}

		if(current.type == TokenType.OPEN_BRA) {
			advance();
			procExpr();
			eat(TokenType.CLOSE_BRA);
		}
	}

	// <function> ::= '.' ( length | to_i | to_s )
	private void procFunction() {
		eat(TokenType.DOT);
		if (
				current.type == TokenType.LENGTH ||
				current.type == TokenType.TO_INT ||
				current.type == TokenType.TO_STR
		) {
			advance();
		}
		else showError();
	}

}
