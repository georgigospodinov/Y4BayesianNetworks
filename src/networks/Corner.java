package networks;

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;

/**
 * This implementation of {@link Creator} can create two bayesian networks
 * that were used to test a corner case of the merging algorithm.
 *
 * @version 1.0
 */
public class Corner extends Creator {

    /**
     * {@inheritDoc}
     */
    @Override
    public BayesianNetwork createBN1() {
        BayesianNetwork net = new BayesianNetwork();
        BayesianEvent a = net.createEvent("A");
        BayesianEvent b = net.createEvent("B");
        BayesianEvent c = net.createEvent("C");
        net.createDependency(a, c);
        net.createDependency(b, c);
        net.finalizeStructure();

        c.getTable().addLine(0.9, true, true, true);
        c.getTable().addLine(0.7, true, true, false);
        c.getTable().addLine(0.5, true, false, true);
        c.getTable().addLine(0.1, true, false, false);

        return net;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BayesianNetwork createBN2() {
        BayesianNetwork net = new BayesianNetwork();
        BayesianEvent c = net.createEvent("C");
        BayesianEvent d = net.createEvent("D");
        BayesianEvent b = net.createEvent("B");
        net.createDependency(b, c);
        net.createDependency(d, c);
        net.finalizeStructure();

        c.getTable().addLine(0.8, true, true, true);
        c.getTable().addLine(0.6, true, true, false);
        c.getTable().addLine(0.2, true, false, true);
        c.getTable().addLine(0.01, true, false, false);

        return net;
    }
}
