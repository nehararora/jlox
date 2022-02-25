package net.nehar.lox;

public class RuntimeError extends RuntimeException{

    final Token token;

    RuntimeError(Token token, String message){
        super(message);
        this.token = token;
    }

}  //  end class RuntimeError
