package networks;

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.table.BayesianTable;

/**
 * This implementation of {@link Creator} can create the two bayesian networks modeled in part 1 of this practical.
 *
 * @version 2.0
 */
public class Practical extends Creator {

    /**
     * Creates a {@link BayesianNetwork} and adds the events that bn1.xml and bn2.xml have in common.
     * This is the entire first network, as the second network builds on top of it.
     *
     * @return a {@link BayesianNetwork} with the events common for the two networks
     */
    private BayesianNetwork createCommonNetwork() {
        BayesianNetwork net = new BayesianNetwork();

        // Alert is the last event
        BayesianEvent alert = net.createEvent("Alert");

        // Phishing branch
        BayesianEvent phishing = net.createEvent("Phishing");
        BayesianEvent email = net.createEvent("Email");
        BayesianEvent phishingDetected = net.createEvent("Phishing Detected");
        net.createDependency(phishing, phishingDetected);
        net.createDependency(email, phishingDetected);
        net.createDependency(phishingDetected, alert);

        // Maintenance branch
        BayesianEvent maintenance = net.createEvent("Maintenance");
        BayesianEvent firewall = net.createEvent("Firewall");
        net.createDependency(maintenance, firewall);
        BayesianEvent maintenanceInfoOutdated = net.createEvent("Maintenance Info Outdated");
        BayesianEvent networkProtected = net.createEvent("Network Protected");
        net.createDependency(firewall, networkProtected);
        net.createDependency(maintenanceInfoOutdated, networkProtected);
        net.createDependency(networkProtected, alert);

        // Work day branch
        BayesianEvent workDay = net.createEvent("Work Day");
        BayesianEvent DDoSChance = net.createEvent("DDoS Chance");
        net.createDependency(workDay, DDoSChance);
        net.createDependency(DDoSChance, alert);

        // Activity branch
        BayesianEvent activity = net.createEvent("Activity");
        BayesianEvent logged = net.createEvent("Logged");
        net.createDependency(activity, logged);
        net.createDependency(logged, alert);

        return net;
    }

    /**
     * Creates the the Conditional Probability Tables of the events that the two networks have in common.
     *
     * @param net the {@link BayesianNetwork} to which the tables should be added
     */
    private void setCommonCPTs(BayesianNetwork net) {
        BayesianTable table;

        // Phishing branch
        net.getEvent("Phishing").getTable().addLine(0.2, true);
        net.getEvent("Email").getTable().addLine(0.1, true);
        table = net.getEvent("Phishing Detected").getTable();
        table.addLine(0.99, true, true, true);
        table.addLine(0.967, true, true, false);
        table.addLine(0.15, true, false, true);
        table.addLine(0.0165, true, false, false);

        // Maintenance branch
        net.getEvent("Maintenance").getTable().addLine(0.2, true);
        table = net.getEvent("Firewall").getTable();
        table.addLine(0.95, true, true);
        table.addLine(1.00, true, false);

        net.getEvent("Maintenance Info Outdated").getTable().addLine(0.02, true);
        table = net.getEvent("Network Protected").getTable();
        table.addLine(0.9, true, true, true);
        table.addLine(1.0, true, true, false);
        table.addLine(0.0, true, false, true);
        table.addLine(0.01, true, false, false);

        // Work day branch
        net.getEvent("Work Day").getTable().addLine(0.72, true);
        table = net.getEvent("DDoS Chance").getTable();
        table.addLine(0.01, true, true);
        table.addLine(0.10, true, false);

        // Activity branch
        net.getEvent("Activity").getTable().addLine(0.9, true);
        table = net.getEvent("Logged").getTable();
        table.addLine(0.3, true, true);
        table.addLine(0.7, true, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BayesianNetwork createBN1() {
        BayesianNetwork net = createCommonNetwork();
        net.finalizeStructure();
        setCommonCPTs(net);
        BayesianTable alert = net.getEvent("Alert").getTable();

        // Probability, TRUE, Phishing Detected, Network Protected, DDoS Chance, Logged
        alert.addLine(0.48, true, true, true, true, true);
        alert.addLine(0.40, true, true, true, true, false);
        alert.addLine(0.24, true, true, true, false, true);
        alert.addLine(0.16, true, true, true, false, false);

        alert.addLine(0.80, true, true, false, true, true);
        alert.addLine(0.72, true, true, false, true, false);
        alert.addLine(0.56, true, true, false, false, true);
        alert.addLine(0.48, true, true, false, false, false);

        alert.addLine(0.32, true, false, true, true, true);
        alert.addLine(0.24, true, false, true, true, false);
        alert.addLine(0.08, true, false, true, false, true);
        alert.addLine(8.0E-05, true, false, true, false, false);

        alert.addLine(0.64, true, false, false, true, true);
        alert.addLine(0.56, true, false, false, true, false);
        alert.addLine(0.40, true, false, false, false, true);
        alert.addLine(0.32, true, false, false, false, false);
        net.validate();
        return net;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BayesianNetwork createBN2() {
        BayesianNetwork net = createCommonNetwork();

        // SQL Inject branch
        BayesianEvent sqlInject = net.createEvent("SQL Inject");
        BayesianEvent sqlInjectDetected = net.createEvent("SQL Inject Detected");
        net.createDependency(sqlInject, sqlInjectDetected);
        net.createDependency(sqlInjectDetected.getLabel(), "Alert");
        net.finalizeStructure();

        setCommonCPTs(net);
        // SQL inject CPT
        sqlInject.getTable().addLine(0.2, true);
        BayesianTable table = sqlInjectDetected.getTable();
        table.addLine(0.9, true, true);
        table.addLine(0.1, true, false);

        // Alert CPT
        table = net.getEvent("Alert").getTable();
        // Probability, TRUE, Phishing Detected, Network Protected, DDoS Chance, Logged, SQL Inject Detected
        table.addLine(0.52, true, true, true, true, true, true);
        table.addLine(0.40, true, true, true, true, true, false);
        table.addLine(0.48, true, true, true, true, false, true);
        table.addLine(0.36, true, true, true, true, false, false);
        table.addLine(0.28, true, true, true, false, true, true);
        table.addLine(0.16, true, true, true, false, true, false);
        table.addLine(0.24, true, true, true, false, false, true);
        table.addLine(0.12, true, true, true, false, false, false);

        table.addLine(0.80, true, true, false, true, true, true);
        table.addLine(0.68, true, true, false, true, true, false);
        table.addLine(0.76, true, true, false, true, false, true);
        table.addLine(0.64, true, true, false, true, false, false);
        table.addLine(0.56, true, true, false, false, true, true);
        table.addLine(0.44, true, true, false, false, true, false);
        table.addLine(0.52, true, true, false, false, false, true);
        table.addLine(0.40, true, true, false, false, false, false);

        table.addLine(0.40, true, false, true, true, true, true);
        table.addLine(0.28, true, false, true, true, true, false);
        table.addLine(0.36, true, false, true, true, false, true);
        table.addLine(0.24, true, false, true, true, false, false);
        table.addLine(0.16, true, false, true, false, true, true);
        table.addLine(0.40, true, false, true, false, true, false);
        table.addLine(0.12, true, false, true, false, false, true);
        table.addLine(8.0E-5, true, false, true, false, false, false);

        table.addLine(0.68, true, false, false, true, true, true);
        table.addLine(0.56, true, false, false, true, true, false);
        table.addLine(0.64, true, false, false, true, false, true);
        table.addLine(0.52, true, false, false, true, false, false);
        table.addLine(0.44, true, false, false, false, true, true);
        table.addLine(0.32, true, false, false, false, true, false);
        table.addLine(0.40, true, false, false, false, false, true);
        table.addLine(0.28, true, false, false, false, false, false);

        net.validate();
        return net;
    }
}
