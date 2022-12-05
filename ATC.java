//import java.io.IOException;
//import java.util.Map;
import java.util.Scanner;
//import java.util.concurrent.ConcurrentHashMap;
public class ATC extends ATCSimMenu{

    public static void main(String[] args) {
//Begin Menu Actual
//begin switch
            firstMenu();
        do {
            Scanner s = new Scanner(System.in);
            char menuItem = s.nextLine().toLowerCase().charAt(0);
            switch (menuItem) {
                case 'i' -> ATCSimMenu.simParameters();
                case 'd' -> ATCSimMenu.printSimStats();
                case 's' -> ATCSimMenu.printSimStatsFile();
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

    //Import into DB



}
