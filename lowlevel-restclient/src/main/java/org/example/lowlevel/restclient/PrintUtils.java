package org.example.lowlevel.restclient;

@SuppressWarnings("UnusedReturnValue")
public class PrintUtils {

    public static void title(Object title) {
        println("");
        println("-".repeat(("" + title).length()));
    }
    public static final String LINE_SEPARATOR = System.lineSeparator();
    public static final String RESET = "\033[0m";
    public static final String GREEN = "\u001B[32m";
    public static  final String CYAN = "\u001B[36m";

    public static  <T> T  green(T title) {
        println("");
        println(GREEN + title + RESET);
        println("-".repeat(("" + title).length()));
        return title;
    }

    public static  <T> T  red(T title) {
        final String RED = "\u001B[31m";
        println("");
        println(RED + title + RESET);
        println("-".repeat(("" + title).length()));
        return title;
    }

    public static  <T> T  cyan(T title) {
        println("");
        println(CYAN + title + RESET);
        println("-".repeat(("" + title).length()));
        return title;
    }
    public static <T> T println(T t) {
        System.out.println(t);
        return t;
    }
    public static <T> T println(String description, T t) {
        System.out.print("description: "+ description + " ");
        println(t);
        return t;
    }
}
