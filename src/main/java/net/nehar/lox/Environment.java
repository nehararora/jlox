package net.nehar.lox;

import java.util.HashMap;
import java.util.Map;
/**
 * Class to store variable bindings.
 *
 */
public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    void define(String name, Object value) {
        values.put(name, value);
    }  //  end method define

    Object get(Token name) {
        if(values.containsKey(name.lexeme))
            return values.get(name.lexeme);

        throw new RuntimeError(name, "Undefined variable " + name.lexeme + ".");
    }  //  end method get

}  //  end class Environment
