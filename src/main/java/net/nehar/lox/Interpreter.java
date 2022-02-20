package net.nehar.lox;

import java.util.List;


public class Interpreter implements
        Expr.Visitor<Object>, Stmt.Visitor<Void> {

    private Environment environment = new Environment();

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement: statements)
                execute(statement);
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }  //  end method interpret

    private void execute(Stmt statement) {
        statement.accept(this);
    }  //  end method execute

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }  //  end method visitBlockStmt

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;

        try {
            this.environment = environment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }  //  end method executeBlock

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }  //  end method visitLiteralExpr

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    } //  end method visitGroupingExp

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        return switch (expr.operator.type) {
            case MINUS -> {
                checkNumberOperand(expr.operator, right);
                yield -(double) right;
            }

            case BANG ->  !isTruthy(right);
            default -> null;
        };
    }  //  end method visitUnaryExpr

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }  //  end method visitVariableExpr

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        return switch (expr.operator.type) {
            case GREATER -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left > (double)right;
            }
            case GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left >= (double)right;
            }
            case LESS -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left <= (double)right;
            }
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);
            case MINUS -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left - (double) right;
            }
            case SLASH -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left / (double) right;
            }
            case STAR -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double)left * (double) right;
            }
            case PLUS -> {
                if (left instanceof Double && right instanceof Double)
                    yield  (double)left + (double)right;
                if (left instanceof String && right instanceof String)
                    yield (String)left + (String)right;
                throw new RuntimeError(expr.operator,
                        "Operands must be two numbers or strings.");
            }

            default -> null;
        };

    }  //  end method visitBinaryExpr

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }  //  end method evaluate

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }  //  end method visitExpressionStmt

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition)))
            execute(stmt.thenBranch);
        else if (stmt.elseBranch != null)
            execute(stmt.elseBranch);

        return null;
    }  //  end method visitIfStmt

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }  //  end method visitPrintStmt

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {

        Object value = (stmt.initializer != null) ? evaluate(stmt.initializer):null;
        environment.define(stmt.name.lexeme, value);

        return null;
    }  //  end method visitVarStmt

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }  //  end method visitAssignExpr

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }  //  end method isTruthy

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }  //  end method isEqual

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();

            if(text.endsWith(".0"))
                text = text.substring(0, text.length() - 2);

            return text;
        }

        return object.toString();

    }  //  end method stringify

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }  //  end method checkNumberOperand

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be numbers.");
    }
}  //  end class Interpreter
