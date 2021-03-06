package net.nehar.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


public class GenerateAst {
    private static final String packageName = "net.nehar.lox";
    private static final String className = "Expr";

    public static void main(String [] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast: <output directory>");
            System.exit(64);
        }

        String outputDir = args[0];
        defineAst(outputDir, className, Arrays.asList(
                "Assign: Token name, Expr value",
                "Binary: Expr left, Token operator, Expr right",
                "Call: Expr callee, Token paren, List<Expr> arguments",
                "Grouping: Expr expression",
                "Literal: Object value",
                "Logical: Expr left, Token operator, Expr right",
                "Unary: Token operator, Expr right",
                "Variable: Token name"
        ));

        defineAst(outputDir, "Stmt", Arrays.asList(
                "Block: List<Stmt> statements",
                "Expression: Expr expression",
                "Function: Token name, List<Token> params, List<Stmt> body",
                "If: Expr condition, Stmt thenBranch, Stmt elseBranch",
                "Print: Expr expression",
                "Return: Token keyword, Expr value",
                "Var: Token name, Expr initializer",
                "While: Expr condition, Stmt body"
                ));
    }  //  end main

    private static void defineAst(String outputDir,
                                  String baseName,
                                  List<String> types) throws IOException {

        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package " + packageName + ";");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        for (String type: types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");
        writer.println("\n}  //  end abstract class " + baseName);
        writer.close();
    }  //  end defineAst

    private static void defineVisitor(PrintWriter writer,
                                      String baseName,
                                      List<String> types) {
        writer.println("    interface Visitor<R> {");

        for (String type: types) {
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName + "(" +
                   typeName +" " + baseName.toLowerCase() + ");");
        }

        writer.println("    }  //  end interface Visitor");
    }  //  end defineVisitor

    private static void defineType(PrintWriter writer,
                                   String baseName,
                                   String className,
                                   String fieldList) {

        //  class header
        writer.println("\n    static class " + className + " extends " + baseName + " {");

        // constructor
        writer.println("        " + className + "(" + fieldList + ") {");

        // parameters/fields
        String []fields = fieldList.split(", ");

        for (String field: fields) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }
        writer.println("        } //  end constructor");

        // Visitor
        writer.println();
        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" +
                className + baseName + "(this);");
        writer.println("        }");

        // fields
        writer.println();
        for (String field: fields) {
            writer.println("        final " + field + ";");
        }

        writer.println("    }  //  end class " + className);
    }  //  end method defineType

}  //  end class GenerateAst
