//import java.io.IOException;
//import java.util.Map;
import java.util.Scanner;
//import java.util.concurrent.ConcurrentHashMap;
public class ATC
{
    static double meanArrivalFreq = 0.0;
    static double meanDepartureFreq = 0.0;
    static double simulationTime = 0.0;
    static ATCSim sim = new ATCSim();

    public static void main(String[] args) {
        System.out.println("ATC Menu\n");

//Begin Menu Actual
//begin switch
            firstMenu();
        do {
            Scanner s = new Scanner(System.in);
//            String menuItem = s.nextLine();
            char menuItem = s.nextLine().toLowerCase().charAt(0);
            switch (menuItem) {
                case 'i' -> simParameters();
                case 'd' -> printSimStats();
                case 's' -> printSimStatsFile();
                case 'r', 'q' -> {
                    System.out.println("Goodbye.");
                    System.exit(0);
                }
                //return;
                default -> System.out.println("Error.");
            }


        }while(true);
//end switch
//End Menu Actual

    }


    //I probably wouldn't use "public static object" for production code, but for the assignment it was easiest.
    public static Object simParameters() {
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
        return null;
    }

    public static Object printSimStats() {
        sim.printSimSummaryStatistics();
        return null;
    }

    public static Object printSimStatsFile() {
        sim.printSimSummaryStatisticsToFile();
        System.out.println("Goodbye.");
        System.exit(0);
        return null;
    }

    public static void firstMenu(){
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


//The menu system went through a lot of different iterations
// Print out statistics calculated from the simulation.
//sim.printSimSummaryStatistics();
// Print out statistics calculated from the simulation.
//sim.printSimSummaryStatisticsToFile();
