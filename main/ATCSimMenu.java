import java.util.Scanner;

//breakout the console menu system into its own file
public class ATCSimMenu extends ATCSim {

    static double meanArrivalFreq = 0.0;
    static double meanDepartureFreq = 0.0;
    static double simulationTime = 0.0;
    static ATCSim sim = new ATCSim();


    public static void simParameters() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Air Traffic Control Simulator\n");
        System.out.println("Enter mean departure frequency (0.0 < df > 1.0): ");
        if (scanner.hasNextDouble()) {
            meanDepartureFreq = scanner.nextDouble();
            scanner.nextLine();
        }
        System.out.println("Enter mean arrival frequency   0.0 < af > 1.0): ");
        if (scanner.hasNextDouble()) {
            meanArrivalFreq = scanner.nextDouble();
            scanner.nextLine();
        }
        System.out.println("Enter the simulation time in minutes (use 720 for 24hrs): ");
        if (scanner.hasNextDouble()) {
            simulationTime = scanner.nextDouble();
            scanner.nextLine();
        }
        //  Each i in the loop represents a minute, 720 x 2 = 1440 minutes in 24 hours
        for (int i = 0; i < simulationTime; i++) {
            sim.processArrival(meanArrivalFreq);
            sim.processDeparture(meanDepartureFreq);
        }
    }

    public static void printSimStats() {
        sim.printSimSummaryStatistics();
    }

    public static void printSimStatsFile() {
        sim.printSimSummaryStatisticsToFile();
        System.out.println("Goodbye.");
        System.exit(0);
    }

    public static void firstMenu(){
        System.out.println("ATC Menu\n");
        System.out.println("======================================");
        System.out.println("|     Please select an option:      |");
        System.out.println("======================================");
        System.out.println("| Options:                           |");
        System.out.println("|   [I] Input Parameters             |");
        System.out.println("|   [D] Display Stats in Console     |");
        System.out.println("|   [S] Save Stats to File           |");
        System.out.println("|   [Q] Quit                         |");
        System.out.println("======================================");
    }

}



