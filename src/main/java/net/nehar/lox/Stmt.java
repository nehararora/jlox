package net.nehar.lox;

import java.util.List;

abstract class Stmt {
    interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
    }  //  end interface Visitor

    static class Block extends Stmt {
        Block(List<Stmt> statements) {
            this.statements = statements;
        } //  end constructor

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        final List<Stmt> statements;
    }  //  end class Block

    static class Expression extends Stmt {
        Expression(Expr expression) {
            this.expression = expression;
        } //  end constructor

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        final Expr expression;
    }  //  end class Expression

    static class Print extends Stmt {
        Print(Expr expression) {
            this.expression = expression;
        } //  end constructor

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        final Expr expression;
    }  //  end class Print

    static class Var extends Stmt {
        Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        } //  end constructor

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        final Token name;
        final Expr initializer;
    }  //  end class Var

    abstract <R> R accept(Visitor<R> visitor);

}  //  end abstract class Stmt
