package net.nehar.lox;

public class Interpreter implements Expr.Visitor<Object> {

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
            case MINUS -> -(double) right;
            case BANG ->  !isTruthy(right);
            default -> null;
        };
    }  //  end method visitUnaryExpr

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        return switch (expr.operator.type) {
            case MINUS -> (double)left - (double) right;
            case SLASH -> (double)left / (double) right;
            case STAR -> (double)left * (double) right;

            // case PLUS -> { if(left instanceof Double && right instanceof Double) return (double)left _+
            default -> null;
        };

    }  //  end method visitBinaryExpr

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }  //  end method evaluate

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }  //  end method isTruthy

}  //  end class Interpreter
