package net.nehar.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


public class GenerateAst {
    public static void main(String [] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast: <output directory>");
            System.exit(64);
        }

        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary: Expr Left, Token Operator, Expr Right",
                "Grouping: Expr Expression",
                "Literal: Object value",
                "Unary: Token Operator, Expr right"
        ));
    }  //  end main

    private static void defineAst(String outputDir,
                                  String baseName,
                                  List<String> types) throws IOException {

        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter("UTF-8");
        writer.println("package net.nehar.lox");
        writer.println();
        writer.println("import java.util.list");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        for (String type: types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[0].trim();
            defineType(writer, baseName, className, fields);
        }
        writer.println("}");
        writer.close();
    }  //  end defineAst

    private static void defineType(PrintWriter writer,
                                   String baseName,
                                   String className,
                                   String fieldList) {

        //  class header
        writer.println(" Static Class " + className + " extends " + baseName + " {");

        // constructor
        writer.println("    " + className + " extends " + baseName + " {");

        // parameters/fields
        String []fields = fieldList.split(", ");

        for (String field: fields) {
            String name = field.split(" ")[1];
            writer.println("    this." + name + " = " + name + ";");
        }
        writer.println("    }");

        //fields
        writer.println();
        for (String field: fields) {
            writer.println("    final " + field + ";");
        }

        writer.println("    }");
    }  //  end method defineType

}  //  end class GenerateAst
