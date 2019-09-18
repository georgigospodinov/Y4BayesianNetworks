package util;

import sun.security.action.GetPropertyAction;

import java.security.AccessController;
import java.util.Collection;

/**
 * Provides a method to "pretty-print" multiple objects.
 *
 * @version 2.1
 */
public class PrintFormatting {

    public static final String NEW_LINE = AccessController.doPrivileged(new GetPropertyAction("line.separator"));
    public static final String SEPARATOR = ",";

    public static void print(Object... objects) {
        for (Object o : objects)
            print(o, 0);
    }

    private static void printTabs(int numberOfTabs) {
        for (int i = 0; i < numberOfTabs; i++)
            System.out.print("\t");
    }

    private static void printArray(Object[] arr, int nestingLevel) {
        printTabs(nestingLevel - 1);
        print("[");

        for (Object o : arr)
            print(o, nestingLevel);

        printTabs(nestingLevel - 1);
        print("]");
    }

    private static void print(Object o, int nestingLevel) {
        if (o instanceof int[]) {
            int[] x = ((int[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) objs[i] = x[i];
            printArray(objs, nestingLevel + 1);
        }
        else if (o instanceof double[]) {
            double[] x = ((double[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) objs[i] = x[i];
            printArray(objs, nestingLevel + 1);
        }
        else if (o instanceof char[]) {
            char[] x = ((char[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) objs[i] = x[i];
            printArray(objs, nestingLevel + 1);
        }
        else if (o instanceof float[]) {
            float[] x = ((float[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) objs[i] = x[i];
            printArray(objs, nestingLevel + 1);
        }
        else if (o instanceof boolean[]) {
            boolean[] x = ((boolean[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) objs[i] = x[i];
            printArray(objs, nestingLevel + 1);
        }
        else if (o instanceof byte[]) {
            byte[] x = ((byte[]) o);
            Object[] objs = new Object[x.length];
            for (int i = 0; i < x.length; i++) objs[i] = x[i];
            printArray(objs, nestingLevel + 1);
        }
        else if (o instanceof Object[])
            printArray(((Object[]) o), nestingLevel + 1);
        else if (o instanceof Collection)
            printArray(((Collection) o).toArray(), nestingLevel + 1);
        else {
            printTabs(nestingLevel);
            System.out.println(String.valueOf(o));
        }
    }
}
