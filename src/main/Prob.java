package main;

import networks.Creator;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;

import static main.Merger.FALSE;
import static main.Merger.TRUE;
import static util.PrintFormatting.print;

/**
 * The class with the executable main method.
 *
 * @version 2.2
 */
public class Prob {

    /**
     * Creates and returns a printable {@link String} that contains the full Conditional Probability Table
     * of the given {@link BayesianEvent} as well as the labels of parent events.
     *
     * @param e the {@link BayesianEvent} for which to build a {@link String}
     * @return the {@link String} representation of the given {@link BayesianEvent} as described above
     * @see BayesianEvent#getParents()
     * @see BayesianEvent#getLabel()
     */
    public static String getCPT(BayesianEvent e) {
        StringBuilder sb = new StringBuilder();
        String label = e.getLabel();
        sb.append(label + "\n");

        // If there are parents, add them in a line.
        if (e.getParents().size() > 0) {
            e.getParents().forEach(p -> sb.append(p.getLabel() + ", "));
            // Remove the space and the comma.
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
        }

        e.getTable().getLines().forEach(line -> {
            if (line.getResult() == FALSE) return;

            // If there are arguments, add them to the line.
            if (line.getArguments().length > 0) {
                for (int arg : line.getArguments())
                    sb.append(arg == TRUE ? "True, " : "False, ");
                // Remove the comma.
                sb.deleteCharAt(sb.length() - 2);
                sb.append("\t");
            }
            sb.append(String.format("%.2f %.2f\n", line.getProbability(), 1 - line.getProbability()));
        });

        return sb.toString();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            print("Please provide the simple class name of the network creator to be used. (For example, Practical)");
            return;
        }
        String className = "networks." + Character.toUpperCase(args[0].charAt(0)) + args[0].substring(1).toLowerCase();
        Creator creator;
        try {
            creator = (Creator) Class.forName(className).getConstructor().newInstance();
        }
        catch (Exception e) {
            print("Could not create instance of class \"" + className + "\"");
            return;
        }

        BayesianNetwork bn1 = creator.createBN1(), bn2 = creator.createBN2();

        print("BN1 {");
        bn1.getEvents().forEach(e -> print(getCPT(e)));
        print("}\n");

        print("BN2 {");
        bn2.getEvents().forEach(e -> print(getCPT(e)));
        print("}\n");

        BayesianNetwork bnt = Merger.merge(bn1, bn2);
        print("\nBNT {");
        bnt.getEvents().forEach(e -> print(getCPT(e)));
        print("}");
    }
}
