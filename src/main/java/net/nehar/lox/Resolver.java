package net.nehar.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class Resolver implements
        Expr.Visitor<Void>, Stmt.Visitor<Void> {

    private final Interpreter interpreter;

    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }  //  end method visitBlockStatement

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null)
            resolve(stmt.initializer);

        define(stmt.name);
        return null;
    }  //  end method visitVarStmt

    void resolve(List<Stmt> statements) {
        for(Stmt statement: statements) {
            resolve(statement);
        }
    }  //  end method resolve(stmts)

    void resolve(Stmt statement) {
        statement.accept(this);
    }  //  end method resolve(stmt)

    void resolve(Expr expr) {
        expr.accept(this);
    }  //  end method resolve(expr)


    void beginScope() {
        scopes.push(new HashMap<>());
    }  //  end method beginScope

    void endScope() {
        scopes.pop();
    }  //  end method endScope

}  //  end class Resolver
