package main;

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.table.TableLine;

import java.util.LinkedHashSet;
import java.util.List;

import static util.PrintFormatting.print;

/**
 * An implementation of the Feng et al Algorithm as presented in the lectures.
 * The static method {@link Merger#merge(BayesianNetwork, BayesianNetwork)} should be called.
 *
 * @version 3.0
 */
public class Merger {

    /**
     * The integer representation of 'true' inside {@link BayesianNetwork}.
     */
    public static final int TRUE = 0;
    /**
     * The integer representation of 'false' inside {@link BayesianNetwork}.
     */
    public static final int FALSE = 1 - TRUE;

    /**
     * Merges the two given {@link BayesianNetwork}s and returns the resulting one.
     * The two given networks are not changed.
     * Internally, an instance of {@link Merger} is created to store
     * references to the different {@link BayesianNetwork}s and
     * sets of events used by the algorithm.
     * This way they don't need to be passed as parameters everywhere.
     *
     * @param bn1 the first {@link BayesianNetwork} to merge
     * @param bn2 the second {@link BayesianNetwork} to merge
     * @return the resulting merged {@link BayesianNetwork}
     */
    public static BayesianNetwork merge(BayesianNetwork bn1, BayesianNetwork bn2) {
        Merger m = new Merger(bn1, bn2);
        m.buildSets();

        m.addOutsideDependencies();
        m.deleteRuleDependencies();
        m.mergeDependencies();
        m.BNT.finalizeStructure();

        m.addOutsideCPT();
        m.deleteRuleCPT();
        m.mergeCPTs();
        m.BNT.validate();

        return m.BNT;
    }

    /**
     * References to the two given {@link BayesianNetwork}s and the one containing the merge result.
     */
    private final BayesianNetwork BN1, BN2, BNT = new BayesianNetwork();
    /**
     * The set of labels of the internal events.
     */
    private final LinkedHashSet<String> internal = new LinkedHashSet<>();
    /**
     * Tet of labels of the external events.
     */
    private final LinkedHashSet<String> external = new LinkedHashSet<>();
    /**
     * The set of labels of the outside events (those that are not in the intersection).
     */
    private final LinkedHashSet<String> outside = new LinkedHashSet<>();
    /**
     * The set of labels of the intersection events.
     */
    private final LinkedHashSet<String> Z = new LinkedHashSet<>();

    /**
     * Constructor used to create an instance of this class internally.
     *
     * @param bn1 the first {@link BayesianNetwork}
     * @param bn2 the second {@link BayesianNetwork}
     */
    private Merger(BayesianNetwork bn1, BayesianNetwork bn2) {
        this.BN1 = bn1;
        this.BN2 = bn2;
    }

    /**
     * Fills the intersection set {@link Merger#Z} with the labels of events found in both networks.
     */
    private void determineIntersection() {
        print("Determining intersection...");
        Z.addAll(BN1.getEventMap().keySet());
        Z.retainAll(BN2.getEventMap().keySet());
    }

    /**
     * Within the given {@link BayesianNetwork},
     * gets the {@link BayesianEvent} associated with this label and retrieves its parent events.
     * Returns true if all of these parents are within the intersection set {@link Merger#Z}.
     *
     * @param bn    the {@link BayesianNetwork} to look in
     * @param label the label of the {@link BayesianEvent} to look for
     * @return true iff all parents of the {@link BayesianEvent} are inside the intersection set {@link Merger#Z}
     */
    private boolean parentsInIntersection(BayesianNetwork bn, String label) {
        for (BayesianEvent bn1Parent : bn.getEvent(label).getParents()) {
            // If there is a parent that is not in the intersection, return false.
            if (!Z.contains(bn1Parent.getLabel()))
                return false;
        }

        // All parents are in the intersection, return true.
        return true;
    }

    /**
     * Calls {@link Merger#determineIntersection()} and fills the sets used by the algorithm.
     * Implements steps 2 and 3 of the Feng et al Algorithm as described in the lectures.
     *
     * @see Merger#internal
     * @see Merger#external
     * @see Merger#outside
     */
    private void buildSets() {
        print("Building sets of nodes...");
        determineIntersection();
        print("Sorting out internal and external nodes...");
        for (String label : Z) {
            // All parents from BN1 are in the intersection.
            if (parentsInIntersection(BN1, label)) {
                internal.add(label);
                continue;
            }
            // All parents from BN2 are in the intersection.
            if (parentsInIntersection(BN2, label))
                internal.add(label);
                // Neither the BN1 parents, nor the BN2 parents are in the intersection.
            else external.add(label);
        }

        BN1.getEvents().forEach(e -> {
            if (!Z.contains(e.getLabel()))
                outside.add(e.getLabel());
        });
        BN2.getEvents().forEach(e -> {
            if (!Z.contains(e.getLabel()))
                outside.add(e.getLabel());
        });

        print("Adding the nodes to BNT...");
        print("internal:", internal);
        internal.forEach(BNT::createEvent);
        print("external:", external);
        external.forEach(BNT::createEvent);
        print("non-intersection:", outside);
        outside.forEach(BNT::createEvent);
    }

    /**
     * Retrieves the {@link BayesianEvent} associated with the given label.
     * First checks if the event is in {@link Merger#BN1} and returns that on true.
     * Otherwise it returns the event from {@link Merger#BN2}.
     * <p>
     * This method is meant to be used for labels from {@link Merger#outside} - the set of nodes outside the intersection.
     *
     * @param label the label of the event to look for.
     * @return the {@link BayesianEvent} with the given label
     */
    private BayesianEvent getOldEvent(String label) {
        if (BN1.getEventMap().containsKey(label))
            return BN1.getEvent(label);
        else return BN2.getEvent(label);
    }

    /**
     * Adds the dependencies between events outside the intersection set.
     * The dependencies are added to {@link Merger#BNT}.
     * Implements the dependencies part of step 4 of the Feng et al Algorithm as described in the lectures.
     */
    private void addOutsideDependencies() {
        print("Adding dependencies of non-intersection nodes...");
        outside.forEach(label -> {
            print("\t" + label);
            getOldEvent(label).getParents().forEach(parent -> BNT.createDependency(parent.getLabel(), label));
        });
    }

    /**
     * Adds the Conditional Probability Tables of events outside the intersection set.
     * The CPTs are added to {@link Merger#BNT}.
     * Implements the CPTs part of step 4 of the Feng et al Algorithm as described in the lectures.
     */
    private void addOutsideCPT() {
        print("Adding the Conditional Probability Tables of non-intersection nodes...");
        outside.forEach(label -> {
            print("\t" + label);
            BayesianEvent oldEvent = getOldEvent(label);
            List<TableLine> lines = BNT.getEvent(label).getTable().getLines();
            lines.clear();
            lines.addAll(oldEvent.getTable().getLines());
        });
    }

    /**
     * Iterates the {@link Merger#internal} events and chooses which dependencies to add to {@link Merger#BNT}
     * based on the DELETE RULE described in the lectures.
     * Implements the dependencies part of steps 6 and 7 of the Feng et al Algorithm as described in the lectures.
     */
    private void deleteRuleDependencies() {
        print("Applying the delete rule on internal nodes and saving dependencies...");
        internal.forEach(label -> {
            List<BayesianEvent> bn1EventParents = BN1.getEvent(label).getParents();
            if (!parentsInIntersection(BN1, label)) {
                print("\t" + label + " CASE A, saving node from BN1");
                bn1EventParents.forEach(parent -> BNT.createDependency(parent.getLabel(), label));
                return;
            }

            List<BayesianEvent> bn2EventParents = BN2.getEvent(label).getParents();
            if (!parentsInIntersection(BN2, label)) {
                print("\t" + label + " CASE B, saving node from BN2");
                bn2EventParents.forEach(parent -> BNT.createDependency(parent.getLabel(), label));
                return;
            }

            if (bn1EventParents.size() < bn2EventParents.size()) {
                print("\t" + label + " CASE C, more parents in BN2, saving node from BN2");
                bn2EventParents.forEach(parent -> BNT.createDependency(parent.getLabel(), label));
            }
            else {  // Default to BN1 if the number of parents is equal
                print("\t" + label + " CASE C, more parents in BN1, saving node from BN1");
                bn1EventParents.forEach(parent -> BNT.createDependency(parent.getLabel(), label));
            }
        });
    }

    /**
     * Iterates the {@link Merger#internal} events and chooses which Conditional Probability Tables to add to
     * {@link Merger#BNT} based on the DELETE RULE described in the lectures.
     * Implements the CPTs part of steps 6 and 7 of the Feng et al Algorithm as described in the lectures.
     */
    private void deleteRuleCPT() {
        print("Applying the delete rule on internal nodes and saving Conditional Probability Tables...");
        internal.forEach(label -> {
            // Clear CPT in BNT's event.
            List<TableLine> lines = BNT.getEvent(label).getTable().getLines();
            lines.clear();

            BayesianEvent bn1Event = BN1.getEvent(label);
            if (!parentsInIntersection(BN1, label)) {
                print("\t" + label + " CASE A, saving node from BN1");
                lines.addAll(bn1Event.getTable().getLines());
                return;
            }
            BayesianEvent bn2Event = BN2.getEvent(label);
            if (!parentsInIntersection(BN2, label)) {
                print("\t" + label + " CASE B, saving node from BN2");
                lines.addAll(bn2Event.getTable().getLines());
                return;
            }

            if (bn1Event.getParents().size() < bn2Event.getParents().size()) {
                print("\t" + label + " CASE C, more parents in BN2, saving node from BN2");
                lines.addAll(bn2Event.getTable().getLines());
            }
            else {  // Default to BN1 if the number of parents is equal
                print("\t" + label + " CASE C, more parents in BN1, saving node from BN1");
                lines.addAll(bn1Event.getTable().getLines());
            }
        });
    }

    /**
     * Iterates the {@link Merger#external} events and adds all their dependencies to {@link Merger#BNT}.
     * Implements step 9 of the Feng et al Algorithm as described in the lectures.
     */
    private void mergeDependencies() {
        print("Adding all dependencies of external nodes...");
        external.forEach(label -> {
            print("\t" + label);
            BN1.getEvent(label).getParents().forEach(parent -> BNT.createDependency(parent.getLabel(), label));
            BN2.getEvent(label).getParents().forEach(parent -> BNT.createDependency(parent.getLabel(), label));
        });
    }

    /**
     * Iterates the {@link Merger#external} events, merges their Conditional Probabilities Tables, and adds the
     * created CPTs to {@link Merger#BNT}.
     * Implements step 10 of the Feng et al Algorithm as described in the lectures.
     * <br>
     * Recursively goes through all permutations of values for the parents in {@link Merger#BNT}.
     *
     * @see Merger#recursePermutations(int[], int, String)
     */
    private void mergeCPTs() {
        print("Merging Conditional Probability Tables of external nodes...");
        external.forEach(label -> {
            print("\t" + label);
            int[] args = new int[BNT.getEvent(label).getParents().size()];
            int startIndex = 0;
            recursePermutations(args, startIndex, label);
        });
    }

    /**
     * Recursively fills the given array with all possible permutations of {@link Merger#TRUE} and {@link Merger#FALSE}.
     * When the array is filled, the current permutation is a key for the CPT of the {@link BayesianEvent} with the
     * given label in {@link Merger#BNT}.
     *
     * @param args  the array to be filled
     * @param index the current index to put a value at
     * @param label the label of the {@link BayesianEvent}
     * @see Merger#mergeLine(int[], String)
     */
    private void recursePermutations(int[] args, int index, String label) {
        if (index == args.length) {
            mergeLine(args, label);
            return;
        }

        args[index] = TRUE;
        recursePermutations(args, index + 1, label);

        args[index] = FALSE;
        recursePermutations(args, index + 1, label);
    }

    /**
     * Adds a line to the CPT of the {@link BayesianEvent} with the given label in {@link Merger#BNT}.
     * The arguments of the line are in the given array.
     * This method iterates the parents of the {@link BayesianEvent} in {@link Merger#BN1} and in {@link Merger#BN2}
     * and constructs two arrays to be used as arguments when accessing the original CPTs of the {@link BayesianEvent}.
     * The values in the arrays correspond to values in the given array.
     *
     * @param args  the arguments used to access the CPT of the {@link BayesianEvent}
     * @param label the label of the {@link BayesianEvent}
     */
    private void mergeLine(int[] args, String label) {
        List<BayesianEvent> bn1Parents = BN1.getEvent(label).getParents();
        List<BayesianEvent> bn2Parents = BN2.getEvent(label).getParents();
        List<BayesianEvent> bntParents = BNT.getEvent(label).getParents();
        int[] argsInBN1 = new int[bn1Parents.size()];
        int[] argsInBN2 = new int[bn2Parents.size()];

        // Iterate the parents in BNT, and set the args in BN1 and BN2
        for (int bnti = 0; bnti < bntParents.size(); bnti++) {
            String plabel = bntParents.get(bnti).getLabel();

            // Find the matching parent in BN1
            for (int bn1i = 0; bn1i < bn1Parents.size(); bn1i++) {
                if (bn1Parents.get(bn1i).getLabel().equals(plabel)) {
                    // and set the appropriate index
                    argsInBN1[bn1i] = args[bnti];
                    break;
                }
            }

            // Find the matching parent in BN2
            for (int bn2i = 0; bn2i < bn2Parents.size(); bn2i++) {
                if (bn2Parents.get(bn2i).getLabel().equals(plabel)) {
                    // and set the appropriate index
                    argsInBN2[bn2i] = args[bnti];
                    break;
                }
            }
        }

        // Find the probabilities with the given arguments.
        double p1, p2, ptTrue, ptFalse;
        p1 = BN1.getEvent(label).getTable().findLine(TRUE, argsInBN1).getProbability();
        p2 = BN2.getEvent(label).getTable().findLine(TRUE, argsInBN2).getProbability();
        ptTrue = p1 + p2 - p1 * p2;
        p1 = BN1.getEvent(label).getTable().findLine(FALSE, argsInBN1).getProbability();
        p2 = BN2.getEvent(label).getTable().findLine(FALSE, argsInBN2).getProbability();
        ptFalse = p1 + p2 - p1 * p2;
        double p = ptTrue / (ptTrue + ptFalse);  // normalized
        BNT.getEvent(label).getTable().addLine(p, TRUE, args);
    }
}
