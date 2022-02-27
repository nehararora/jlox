package net.nehar.lox;

import java.util.List;


public class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;

    LoxFunction(Stmt.Function declaration) {
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }  //  end method arity

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }  //  end method toString

    @Override
    public Object call(Interpreter interpreter,
                       List<Object> arguments) {
        // create the new scope
        Environment environment = new Environment(interpreter.globals);

        // add arguments to current scope
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme,
                    arguments.get(i));
        }

        // run the callable
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        return null;
    }  //  end method call

}  //  end class LoxFunction
