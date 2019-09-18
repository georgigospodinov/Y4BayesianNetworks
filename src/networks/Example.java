package networks;

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.table.BayesianTable;

/**
 * This implementation of {@link Creator} can create the two example networks that were provided on studres.
 *
 * @version 1.0
 */
public class Example extends Creator {

    /**
     * {@inheritDoc}
     */
    @Override
    public BayesianNetwork createBN1() {
        BayesianNetwork net = new BayesianNetwork();
        BayesianEvent influenza = net.createEvent("Influenza");
        BayesianEvent soreThroat = net.createEvent("Sore Throat");
        BayesianEvent fever = net.createEvent("Fever");
        net.createDependency(influenza, soreThroat);
        net.createDependency(influenza, fever);

        BayesianEvent smokes = net.createEvent("Smokes");
        BayesianEvent bronchitis = net.createEvent("Bronchitis");
        net.createDependency(influenza, bronchitis);
        net.createDependency(smokes, bronchitis);

        BayesianEvent wheezing = net.createEvent("Wheezing");
        BayesianEvent coughing = net.createEvent("Coughing");
        net.createDependency(bronchitis, wheezing);
        net.createDependency(bronchitis, coughing);
        net.finalizeStructure();

        influenza.getTable().addLine(0.05, true);

        BayesianTable table = soreThroat.getTable();
        table.addLine(0.3, true, true);
        table.addLine(0.001, true, false);

        table = fever.getTable();
        table.addLine(0.9, true, true);
        table.addLine(0.05, true, false);

        table = bronchitis.getTable();
        table.addLine(0.99, true, true, true);
        table.addLine(0.90, true, true, false);
        table.addLine(0.7, true, false, true);
        table.addLine(1.0E-4, true, false, false);

        table = wheezing.getTable();
        table.addLine(0.6, true, true);
        table.addLine(0.001, true, false);

        smokes.getTable().addLine(0.2, true);

        table = coughing.getTable();
        table.addLine(0.8, true, true);
        table.addLine(0.07, true, false);

        net.validate();
        return net;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BayesianNetwork createBN2() {
        BayesianNetwork net = new BayesianNetwork();
        BayesianEvent smokes = net.createEvent("Smokes");
        BayesianEvent soreThroat = net.createEvent("Sore Throat");
        net.createDependency(smokes, soreThroat);

        BayesianEvent cold = net.createEvent("Cold");
        BayesianEvent fever = net.createEvent("Fever");
        net.createDependency(cold, fever);

        BayesianEvent bronchitis = net.createEvent("Bronchitis");
        net.createDependency(soreThroat, bronchitis);

        BayesianEvent wheezing = net.createEvent("Wheezing");
        net.createDependency(bronchitis, wheezing);

        BayesianEvent asthma = net.createEvent("Asthma");
        BayesianEvent coughing = net.createEvent("Coughing");
        net.createDependency(bronchitis, coughing);
        net.createDependency(asthma, coughing);
        net.finalizeStructure();

        BayesianTable table = soreThroat.getTable();
        table.addLine(0.7, true, true);
        table.addLine(0.2, true, false);

        table = fever.getTable();
        table.addLine(0.8, true, true);
        table.addLine(0.3, true, false);

        table = bronchitis.getTable();
        table.addLine(0.4, true, true);
        table.addLine(0.001, true, false);

        table = wheezing.getTable();
        table.addLine(0.6, true, true);
        table.addLine(0.1, true, false);

        smokes.getTable().addLine(0.2, true);

        table = coughing.getTable();
        table.addLine(0.99, true, true, true);
        table.addLine(0.85, true, true, false);
        table.addLine(0.5, true, false, true);
        table.addLine(0.1, true, false, false);

        cold.getTable().addLine(0.3, true);

        asthma.getTable().addLine(0.15, true);

        net.validate();
        return net;
    }
}
