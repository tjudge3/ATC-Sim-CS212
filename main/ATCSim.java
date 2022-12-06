package main;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
//For printing
import java.io.*;
import java.io.IOException;
import java.util.Date;

public class ATCSim {
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
        output_block();
    }
    
    public void printSimSummaryStatisticsToFile(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        try {
            PrintStream stdout = System.out;
            PrintStream o = new PrintStream(new File("resources/main.ATC-Stats-"+dateFormat.format(date)+".txt"));
            System.setOut(o);
            System.out.println("******************************************");
            System.out.println("Automated Air Traffic Control Simulator Summary Statistics");
            System.out.println("******************************************");
            System.out.println("Report Generated on : "+dateFormat.format(date));
            System.out.println("******************************************");
            output_block();
            System.out.println("******************************************");
            System.out.println("Report File Concludes");
            System.out.println("******************************************");
            //without setting and calling this, the menu/system appear to hang
            //what's actually happening is that it "breaks" system.out even after a flush and close
            System.setOut(stdout);
            o.flush();
            o.close();
            System.out.println("Report File Created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
//Reusable Stats Block
    void output_block(){
        System.out.println("Time period stimulated: " + (timeInterval/60 + ":" + String.format("%02d",timeInterval % 60)));
        System.out.println("Total number of flights handled: " + (arrivalStatistics.size() + departureStatistics.size()));
        System.out.println("Total idle time: " + (idleTime/60 + ":" + String.format("%02d",idleTime % 60)));
        System.out.printf("Percent time idle runway: %.2f percent\n", ((double) idleTime/timeInterval *100));
        System.out.println("Arrival Statistics");
        System.out.println("Number of arrivals: " + arrivalStatistics.size());
        System.out.println("Average number of arrival per hour: " + arrivalStatistics.size() / 24);
        System.out.println("Number of rerouted Arrivals: " + reroutedStatistics.size());
        System.out.println("Average arrival time in queue: " + averageArrivalTime() + " minutes");
        System.out.println("Arrivals remaining in queue: " + arrivalQueue.size());
        System.out.println("Departure Statistics");
        System.out.println("Number of departures: " + departureStatistics.size());
        System.out.println("Average number of departure per hour: " + departureStatistics.size() / 24);
        System.out.println("Number of rerouted Departures: " + delayedStatistics.size());
        System.out.println("Average departure time in queue: " + averageDepartureTime() + " minutes");
        System.out.println("Departures remaining in queue: " + departureQueue.size());
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

//p(k) = (mean^k / k!) * exp(-lambda) for k >= 0.
    public static int getPoissonRandom(double lambda){
        double L = Math.exp(-lambda);
        int x = 0;
        double p = 1.0;
        do {x++;
            p *= Math.random();
        } while (p > L);
        return x - 1;
    }
//Implement Negative Binomal Distrubtion

    void processArrival(double meanArrivalFreq){
        int count = 0;
        timerCounter++;
        timeInterval++;
        if ((count = getPoissonRandom(meanArrivalFreq)) > 0)
            addToArrivalQueue(count);
        else{
            if(arrivalQueue.isEmpty()){
                idleTime++;
            }
        }
        if (timerCounter >= MIN_FLIGHT_SPACING){
            if (arrivalQueue.size() > 0){
                timerCounter = 0;
                Flight flight = arrivalQueue.remove();
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
                Flight flight = departureQueue.remove();
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
                    System.out.println("Warning: Queue Full for "+new_flight + " rerouted at " + (timeInterval/60 + ":" + String.format("%02d",timeInterval % 60)));
                    delayedStatistics.add(new_flight);
                }
            }
            count--;
        }
    }

}
