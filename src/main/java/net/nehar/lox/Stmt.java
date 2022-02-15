package net.nehar.lox;



abstract class Stmt {
    interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
    }  //  end interface Visitor

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

    abstract <R> R accept(Visitor<R> visitor);

}  //  end abstract class Stmt
