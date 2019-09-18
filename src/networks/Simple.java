package networks;

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;

/**
 * This implementation of {@link Creator} can create two simple bayesian networks
 * that were used to test the merging algorithm.
 *
 * @version 1.1
 * @see main.Merger
 */
public class Simple extends Creator {

    /**
     * {@inheritDoc}
     */
    @Override
    public BayesianNetwork createBN1() {
        BayesianNetwork net = new BayesianNetwork();
        BayesianEvent a = net.createEvent("A");
        BayesianEvent b = net.createEvent("B");
        net.createDependency(a, b);
        net.finalizeStructure();

        a.getTable().addLine(0.3, true);
        b.getTable().addLine(0.6, true, true);
        b.getTable().addLine(0.2, true, false);

        return net;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BayesianNetwork createBN2() {
        BayesianNetwork net = new BayesianNetwork();
        BayesianEvent c = net.createEvent("C");
        BayesianEvent b = net.createEvent("B");
        net.createDependency(c, b);
        net.finalizeStructure();

        c.getTable().addLine(0.5, true);
        b.getTable().addLine(0.3, true, true);
        b.getTable().addLine(0.8, true, false);

        return net;
    }
}
