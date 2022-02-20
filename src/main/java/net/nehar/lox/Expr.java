package net.nehar.lox;

import java.util.List;

abstract class Expr {
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
    }  //  end interface Visitor

    static class Assign extends Expr {
        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        } //  end constructor

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        final Token name;
        final Expr value;
    }  //  end class Assign

    static class Binary extends Expr {
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        } //  end constructor

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }  //  end class Binary

    static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression = expression;
        } //  end constructor

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final Expr expression;
    }  //  end class Grouping

    static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
        } //  end constructor

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }  //  end class Literal

    static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        } //  end constructor

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }  //  end class Logical

    static class Unary extends Expr {
        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        } //  end constructor

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expr right;
    }  //  end class Unary

    static class Variable extends Expr {
        Variable(Token name) {
            this.name = name;
        } //  end constructor

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        final Token name;
    }  //  end class Variable

    abstract <R> R accept(Visitor<R> visitor);

}  //  end abstract class Expr
