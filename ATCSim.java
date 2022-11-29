//tjudge
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
//For printing
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class ATCSim
{
    final int MIN_FLIGHT_SPACING = 10;
    int flight_number;
    int timerCounter;
    int timeInterval;
    int idleTime;
    ArrayDeque<Flight> arrivalQueue = new ArrayDeque<>();
    ArrayDeque<Flight> departureQueue = new ArrayDeque<>();
    ArrayList<Flight> reroutedStatistics = new ArrayList<>();
    ArrayList<Flight> delayedStatistics =  new ArrayList<>();
    ArrayList<Flight> arrivalStatistics = new ArrayList<>();
    ArrayList<Flight> departureStatistics =  new ArrayList<>();

    public void printSimSummaryStatistics(){
        System.out.println("******************************************");
        System.out.println("Automated Air Traffic Control Simulator Summary Statistics");
        System.out.println("******************************************");
        System.out.println("Time period stimulated: " + (timeInterval/60 + ":" + String.format("%02d",timeInterval % 60)));
        System.out.println("Number of arrivals: " + arrivalStatistics.size());
        System.out.println("Number of departures: " + departureStatistics.size());
        System.out.println("Total number of flights handled: " + (arrivalStatistics.size() + departureStatistics.size()));
        System.out.println("Average number of arrival per hour: " + arrivalStatistics.size() / 24);
        System.out.println("Average number of departure per hour: " + departureStatistics.size() / 24);
        System.out.println("Departures remaining in queue: " + departureQueue.size());
        System.out.println("Arrivals remaining in queue: " + arrivalQueue.size());
        System.out.println("Number of rerouted Arrivals: " + reroutedStatistics.size());
        System.out.println("Number of delayed Departures: " + delayedStatistics.size());
        System.out.println("Total idle time: " + (idleTime/60 + ":" + String.format("%02d",idleTime % 60)));
        System.out.printf("Percent time idle runway: %.2f percent\n", ((double) idleTime/timeInterval *100));
        System.out.println("Average departure time in queue: " + averageDepartureTime() + " minutes");
        System.out.println("Average arrival time in queue: " + averageArrivalTime() + " minutes");
    }

//Was trying to think of some way to try for extra credit, decided on printing the stats to a file
    public void printSimSummaryStatisticsToFile(){
        Date date = new Date();
        //wanted each file name to be unique so using date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        try {
            //Went with txt vs a csv so it would be more like a structured "Report"
            PrintWriter printWriter = new PrintWriter(new FileWriter("resources/ATC-Stats-"+dateFormat.format(date)+".txt", true));
            printWriter.println("******************************************");
            printWriter.println("Automated Air Traffic Control Simulator Summary Statistics");
            printWriter.println("******************************************");
            printWriter.println("Report Generated on : "+dateFormat.format(date));
            printWriter.println("******************************************");
            printWriter.println("Time period stimulated: " + (timeInterval/60 + ":" + String.format("%02d",timeInterval % 60)));
            printWriter.println("Number of arrivals: " + arrivalStatistics.size());
            printWriter.println("Number of departures: " + departureStatistics.size());
            printWriter.println("Total number of flights handled: " + (arrivalStatistics.size() + departureStatistics.size()));
            printWriter.println("Average number of arrival per hour: " + arrivalStatistics.size() / 24);
            printWriter.println("Average number of departure per hour: " + departureStatistics.size() / 24);
            printWriter.println("Departures remaining in queue: " + departureQueue.size());
            printWriter.println("Arrivals remaining in queue: " + arrivalQueue.size());
            printWriter.println("Number of rerouted Arrivals: " + reroutedStatistics.size());
            printWriter.println("Number of delayed Departures: " + delayedStatistics.size());
            printWriter.println("Total idle time: " + (idleTime/60 + ":" + String.format("%02d",idleTime % 60)));
            printWriter.println("Percent time idle runway: " + ((double) idleTime/timeInterval *100) + " percent");
            printWriter.println("Average departure time in queue: " + averageDepartureTime() + " minutes");
            printWriter.println("Average arrival time in queue: " + averageArrivalTime() + " minutes");
            printWriter.println("******************************************");
            printWriter.println("Report File Concludes");
            printWriter.println("******************************************");
            System.out.println("Report File Created");
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int averageDepartureTime(){
        int sum = 0;
        for(Flight flight : departureStatistics){
            sum += flight.timeInQueue();
        }
        return  sum/departureStatistics.size();
    }
    private int averageArrivalTime(){
        int sum = 0;
        for(Flight flight : arrivalStatistics){
            sum += flight.timeInQueue();
        }
        return  sum/arrivalStatistics.size();
    }

//The given math in the assignment was to use mean here (double mean)
//But to me, it seemed like using lambda made more sennse here, or double lambda as it were.
//We bascially are saying : Let x=1,p=1 ... Generate ui=U(0,1) and make x=x(ui);
//So If x≤exp(-lambda), then x-1 is the value we're looking for. Else it should be, x=x+1 and kick back to the generate ui portion.
    public static int getPoissonRandom(double lambda){
        double L = Math.exp(-lambda);
        int x = 0;
        double p = 1.0;
        do {x++;
            p *= Math.random();
        } while (p > L);
        return x - 1;
    }
    void processArrival(double meanArrivalFreq){
        int count;
        timerCounter++;
        timeInterval++;
        if ((count = getPoissonRandom(meanArrivalFreq)) > 0){
            addToArrivalQueue(count);
        }
        else{
            if(arrivalQueue.isEmpty()){
                idleTime++;
            }
        }
        if (timerCounter >= MIN_FLIGHT_SPACING){
            if (arrivalQueue.size() > 0){
                timerCounter = 0;
                Flight flight = arrivalQueue.pop();
                flight.setMinuteOutQueue(timeInterval);
                arrivalStatistics.add(flight);
                System.out.println(flight + " arrived at " + (timeInterval/60 + ":" + String.format("%02d",timeInterval % 60)));
            }
        }
    }
    private void addToArrivalQueue(int count){
        while(count > 0){
            Flight new_flight = new Flight("AA" + ++flight_number , FlightType.Arrival);
            if(arrivalQueue.size() < 5){
                new_flight.setMinuteInQueue(timeInterval);
                arrivalQueue.add(new_flight);
            }
            else{
                System.out.println("Warning: Queue Full for "+new_flight + " rerouted at " + (timeInterval/60 + ":" + String.format("%02d",timeInterval % 60)));
                reroutedStatistics.add(new_flight);
            }
            count--;
        }
    }
    void processDeparture(double meanDepartureFreq){
        int count;
        timerCounter++;
        timeInterval++;
        if ((count = getPoissonRandom(meanDepartureFreq)) > 0){
            addToDepartureQueue(count);
        }
        else
        {
            if(departureQueue.isEmpty()){
                idleTime++;
            }
        }
        if (timerCounter >= MIN_FLIGHT_SPACING){
            if (departureQueue.size() > 0){
                timerCounter = 0;
                Flight flight = departureQueue.pop();
                flight.setMinuteOutQueue(timeInterval);
                departureStatistics.add(flight);
                System.out.println(flight + " departed at " + (timeInterval/60 + ":" + String.format("%02d",timeInterval % 60)));
            }
        }
    }
    private void addToDepartureQueue(int count){
        while(count > 0){
            Flight new_flight = new Flight("UA" + ++flight_number , FlightType.Departure);
            if(arrivalQueue.isEmpty()){
                if(departureQueue.size() < 5){
                    new_flight.setMinuteOutQueue(timeInterval);
                    departureQueue.add(new_flight);
                }
                else{
                    System.out.println(new_flight + " delayed at " + (timeInterval/60 + ":" + String.format("%02d",timeInterval % 60)));
                    delayedStatistics.add(new_flight);
                }
            }
            count--;
        }
    }
}