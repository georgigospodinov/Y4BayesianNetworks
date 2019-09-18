package networks;

import org.encog.ml.bayesian.BayesianNetwork;

/**
 * A {@link Creator} can create two {@link BayesianNetwork}s that share some events and can be merged.
 *
 * @version 1.2
 * @see main.Merger
 */
public abstract class Creator {

    /**
     * Creates and returns the first {@link BayesianNetwork} of the two.
     *
     * @return the first network
     */
    public abstract BayesianNetwork createBN1();

    /**
     * Creates and returns the second {@link BayesianNetwork} of the two.
     * @return the second network
     */
    public abstract BayesianNetwork createBN2();
}
