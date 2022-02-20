package net.nehar.lox;

import java.util.ArrayList;
import java.util.List;

/**
 * The frickin' parser.
 * Grammar:
 *
 * program        → declaration* EOF ;
 * declaration    → varDecl | statement ;
 * varDecl        → "var" IDENTIFIER ("=" expression)? ";";
 * statement      → exprStmt | ifStmt | printStmt | block;
 * exprStmt       → expression ";" ;
 * ifStmt         → "if" "(" expression ")" statement ( "else" statement )? ;
 * printStmt      → "print" expression ";" ;
 * block          → "{" declaration* "}" ;
 * expression     → assignment ;
 * assignment     → IDENTIFIER "=" assignment | equality | logic_or;
 * logic_or       → logic_and ("or" logic_and)*;
 * logic_and      → equality ("and" equality)*;
 * equality       → comparison ( ( "!=" | "==" ) comparison )* ;
 * comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
 * term           → factor ( ( "-" | "+" ) factor )* ;
 * factor         → unary ( ( "/" | "*" ) unary )* ;
 * unary          → ( "!" | "-" ) unary
 *                | primary ;
 * primary        → NUMBER | STRING | "true" | "false" | "nil"
 *                | "(" expression ")" | IDENTIFIER ;
 *
 */
public class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }  //  end constructor

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while( !isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }  //  end method parse

    private Stmt statement() {

        if(match(TokenType.IF)) return ifStatement();
        if (match(TokenType.PRINT)) return printStatement();
        if (match(TokenType.LEFT_BRACE)) return new Stmt.Block(block());

        return expressionStatement();
    }  //  end method statement

    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after 'if' condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if  (match(TokenType.ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);

    }  //  end method ifStatement

    private Stmt printStatement() {
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

        Expr initializer = match(TokenType.EQUAL) ? expression(): null;

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }  //  end method varDeclaration

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression");
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while(!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());

        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' at the end of block.");
        return statements;
    }  //  end method block

    private Expr expression() {
        return assignment();
    }  //  end method expression

    private Expr assignment() {
        Expr expr = or();

        if (match(TokenType.EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }  //  end if match(Equal)
        return expr;
    }  //  end method assign

    private Expr or() {
        Expr expr = and();

        while (match(TokenType.OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }  //  end method or

    private Expr and() {
        Expr expr = equality();

        while(match(TokenType.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }  //  end method and

    private Stmt declaration() {
        try {
            if (match(TokenType.VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }

    }  //  end method declaration

    private Expr equality() {
        Expr expr = comparison();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr =  new Expr.Binary(expr, operator, right);
        }

        return expr;
    }  //  end method equality

    private Expr comparison() {
        Expr expr = term();
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL,
                TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }  //  end method comparison

    private Expr term() {
        Expr expr = factor();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }  //  end method term

    private Expr factor() {
        Expr expr = unary();
        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }  //  end method factor

    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }  //  end method unary

    private Expr primary() {
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NIL)) return new Expr.Literal(null);

        if (match(TokenType.NUMBER,
                TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if(match(TokenType.IDENTIFIER))
            return new Expr.Variable(previous());

        if(match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression");
    }  //  end method primary

    private boolean match(TokenType...types) {
        for (TokenType type: types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }  //   end method match

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }  //  end method check

    private Token advance() {
        if (!isAtEnd()) current ++;
        return previous();
    }  //  end method advance

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }  //  end method isAtEnd

    private Token peek() {
        return tokens.get(current);
    }  //  end method peek

    private  Token previous() {
        return tokens.get(current - 1);
    }  //  end method previous

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }  //  end method error

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;

            switch (peek().type) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> { return; }
                default -> advance();
            }
        }
    }
}  //  end class Parser
