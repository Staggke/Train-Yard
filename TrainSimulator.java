/*
Name: Kaitlyn Stagg
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 16, 2025
Class: TrainSimulator 
Description: This is the main driver class, it creates trains and begins their movement through the yard
*/

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;

public class TrainSimulator {

    public static int MAXTRAINS = 30;
    public static int MAXALIGNMENTS = 60;   
    public static int MAXSWITCHES = 10;
    public static Train[] theFleet = new Train[MAXTRAINS]; //all trains that need to move thru yard
    public static Switch[] theSwitches  = new Switch[MAXSWITCHES];//all switches that will be used by trains

    public static void main(String args[]) throws InterruptedException{
        int numOfTrains;
        int numOfPaths;

        //vars holding details of train
        int trainNum;
        int inboundTrack;
        int outboundTrack;
        boolean dispatched;
        boolean hold;
        int dispatchSequence;
        int index;

        ReentrantLock dummyLock = new ReentrantLock();
        Switch dummySwitch = new Switch(0, dummyLock);//to initialize a switch when train obj is first created before track assignments are made switchnum=0 doesnt exist in yard

        //vars holding details of yard
        int[] inboundYardTrack = new int[MAXALIGNMENTS];
        int[] firstSwitchNum = new int[MAXALIGNMENTS];
        int[] secondSwitchNum = new int[MAXALIGNMENTS];
        int[] thirdSwitchNum = new int[MAXALIGNMENTS];
        int[] outboundYardTrack = new int[MAXALIGNMENTS];

        File theFleetFile = new File("theFleetFile.csv");//info on trains
        File theYardFile = new File("theYardFile.csv");//info on yard configs
        FileReader theFleetFileReader = null;
        FileReader theYardFileReader = null;
        BufferedReader theFleetBufReader = null;
        BufferedReader theYardBufReader = null;
        Scanner theFleetFileScanner = null;
        Scanner theYardFileScanner = null;
        String aTrain = null;
        String anAlignment = null;
        int counter = 0;//counts num trains in sim run

            try{
                System.out.println("\n Spring 2025 - Project 2 - Train Movement Simulator \n\n");
                System.out.println("\n * * * * * * * * * * INTIALIZATION OF SIMULATION DETAILS BEGINS * * * * * * * * * * \n");
                System.out.println("\n\n");

                    //read trains details
                    counter = 0;
                    theFleetFileReader = new FileReader(theFleetFile);
                    theFleetBufReader = new BufferedReader(theFleetFileReader);
                    aTrain = theFleetBufReader.readLine();

                    while(aTrain != null){
                        theFleetFileScanner = new Scanner(aTrain).useDelimiter("\\s*,\\s*");
                        trainNum = theFleetFileScanner.nextInt();
                        inboundTrack = theFleetFileScanner.nextInt();
                        outboundTrack = theFleetFileScanner.nextInt();
                        hold = false;//train on perma hold if the path isn't listed in yard file
                        dispatched = false;
                        dispatchSequence = 0;//dispatch order not set yet
                        index = counter;

                        //create train object
                        theFleet[counter] = new Train(trainNum, inboundTrack, outboundTrack, dummySwitch, dummySwitch, dummySwitch, hold, dispatched, dispatchSequence, index);
                        counter++;

                        //get next line of train data from fleet file
                        aTrain = theFleetBufReader.readLine();
                    }
                    numOfTrains = counter;
                    theFleetBufReader.close();//close file
                
                    //read yard details
                    counter = 0;
                    theYardFileReader = new FileReader(theYardFile);
                    theYardBufReader = new BufferedReader(theYardFileReader);
                    anAlignment = theYardBufReader.readLine();

                    while(anAlignment != null){
                        theYardFileScanner = new Scanner(anAlignment).useDelimiter("\\s*,\\s*");
                        inboundYardTrack[counter] = theYardFileScanner.nextInt();
                        firstSwitchNum[counter] = theYardFileScanner.nextInt();
                        secondSwitchNum[counter] = theYardFileScanner.nextInt();
                        thirdSwitchNum[counter] = theYardFileScanner.nextInt();
                        outboundYardTrack[counter] = theYardFileScanner.nextInt();
                        counter++;

                        //get next line of train data from yard file
                        anAlignment = theYardBufReader.readLine();
                    }

                    numOfPaths = counter;
                    theYardBufReader.close();//close file

                int switchCount = 0;
                for(int i = 0; i < MAXALIGNMENTS; i++){
                    if(firstSwitchNum[i] > switchCount){
                        switchCount = firstSwitchNum[i];
                    }
                }

                //create switch objs
                ReentrantLock[] locks = new ReentrantLock[switchCount];
                for(int i = 0; i < switchCount; i++){
                    locks[i] = new ReentrantLock();
                    theSwitches[i] = new Switch((i+1), locks[i]);
                }

                //assign trains a route or put on perma hold
                for(int i = 0; i < numOfTrains; i++){
                    for(int j = 0; j < numOfPaths; j++){
                        if(theFleet[i].inboundTrackNum == inboundYardTrack[j] && theFleet[i].outboundTrackNum == outboundYardTrack[j]){
                           theFleet[i].firstSwitch = theSwitches[firstSwitchNum[j]-1];
                           theFleet[i].secondSwitch = theSwitches[secondSwitchNum[j]-1];
                           theFleet[i].thirdSwitch = theSwitches[thirdSwitchNum[j]-1];
                        }
                        else if((j == numOfPaths-1) && theFleet[i].firstSwitch.switchNum == 0){
                            theFleet[i].hold = true;//place on a permanent hold if path doesn't exist
                        }
                    }
                }

                ExecutorService TrainFleet = Executors.newFixedThreadPool(MAXTRAINS);//create train fleet
                
                for(int i = 0; i < numOfTrains; i++){//start trains
                    if(theFleet[i].hold != true){
                        try{
                            TrainFleet.execute(new Train(theFleet[i].trainNum, theFleet[i].inboundTrackNum, theFleet[i].outboundTrackNum, theFleet[i].firstSwitch, theFleet[i].secondSwitch, theFleet[i].thirdSwitch, theFleet[i].hold, theFleet[i].dispatched, theFleet[i].dispatchSequence, theFleet[i].index));
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                TrainFleet.shutdown();//shut down executorservice
                while(!TrainFleet.isTerminated()){//threads are still runnning
                    try {
                        System.out.println("Waiting for the service to terminate...");
                        if(TrainFleet.awaitTermination(1, TimeUnit.MINUTES)){
                            break;
                        }
                    } 
                    catch (InterruptedException e) {
                    }
                }
                System.out.println("Finished all threads");
                System.out.println("\n * % * % * % SIMULATION ENDS % * % * % * \n");

                //final status of trains
                for(int i = 0; i < numOfTrains; i++){
                    System.out.println("Train Number " + theFleet[i].trainNum + " assigned\n");
                    System.out.println("Train Number        Inbound Track       Outbound Track      Switch 1        Switch 2        Switch 3            Hold            Dispatched      Dispatch Sequence \n");
                    System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
                    System.out.println(" " + theFleet[i].trainNum + "\t\t\t" + theFleet[i].inboundTrackNum + "\t\t\t" + theFleet[i].outboundTrackNum + "\t\t" + theFleet[i].firstSwitch.switchNum + "\t\t" + theFleet[i].secondSwitch.switchNum + "\t\t" + theFleet[i].thirdSwitch.switchNum +"\t\t" + theFleet[i].hold + "\t\t" + theFleet[i].dispatched + "\t\t" + theFleet[i].dispatchSequence);
                    System.out.println();
                    System.out.println();
                }
            }
            catch(FileNotFoundException e){
                System.out.println("File not found");
            }
            catch(Exception e){
                e.printStackTrace();
            }
    }//end main
}//end class trainsim   
  