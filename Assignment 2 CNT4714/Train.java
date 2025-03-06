
import java.util.Random;

public class Train implements Runnable{
    //define class vars
    protected Random gen = new Random();
   // protected final static int MAXSLEEP = 300; //= 500ms max sleep time
    protected final static int MAXWAITONLOCK = 1500;//max time train waits to try again after being denied a lock
    protected final static int MOVETIME = 300;//movetime for train thru yard

    protected int trainNum;
    protected int inboundTrackNum;
    protected int outboundTrackNum;
    protected Switch firstSwitch;
    protected Switch secondSwitch;
    protected Switch thirdSwitch;
    protected boolean hold;
    protected boolean dispatched;
    protected int dispatchSequence;
    protected int index;

    protected static boolean allLocks = false;
    protected static int counter = 0;
    Train[] output = new Train[TrainSimulator.MAXTRAINS];


    //train constructor method
    //a train as a...
    public Train(int trainNumArg, int inboundTrackNumArg, int outboundTrackNumArg, Switch switch1Arg, Switch switch2Arg, Switch switch3Arg, boolean holdArg, boolean dispatchedArg, int dispatchSequenceArg, int indexArg){
        //define a train obj
        this.trainNum = trainNumArg;
        this.inboundTrackNum = inboundTrackNumArg;
        this.outboundTrackNum = outboundTrackNumArg;
        this.firstSwitch = switch1Arg;
        this.secondSwitch = switch2Arg;
        this.thirdSwitch = switch3Arg;
        this.hold = holdArg;
        this.dispatched = dispatchedArg;
        this.dispatchSequence = dispatchSequenceArg;  
        this.index = indexArg;
    }//end train constructor

    
    //debugging method - print train's data
     /*private void printTrain(){
        //System.out.println("Train data from Train Class - Immediately after constructing a train...");
        System.out.println("Train Number " + trainNum + " assigned.  ");
        System.out.println("Train Number        Inbound Track       Outbound Track      Switch 1        Switch 2        Switch 3        Hold        Dispatched      Dispatch Sequence \n");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println(" " + trainNum + "\t\t" + inboundTrackNum + "\t\t" + outboundTrackNum + "\t\t" + firstSwitch.switchNum + "\t\t" + secondSwitch.switchNum + "\t\t" + thirdSwitch.switchNum +"\t\t" + hold + "\t\t" + dispatched + "\t\t" + dispatchSequence);
        System.out.println();
        System.out.println();
    }*/
     

     //method for train's sleep for rand amt of time
     public void waitTimeForLockRequest(){
        try{
            Thread.sleep(gen.nextInt(MAXWAITONLOCK));
        }//end try
        catch(InterruptedException e){
            e.printStackTrace();
        }//end catch
     }//end waittimeforlockrequest


     //moveTrain simulates the train moving thru the yard (might need to play w the num)
     public void moveTrain(){
        System.out.println("TRAIN " + trainNum + " begins moving through the train yard\n\n");
        try{
            Thread.sleep(MOVETIME);
        }//end try
        catch(InterruptedException e){
            e.printStackTrace();
        }//end catch
     }//end movetrain

     public void run(){
        //most output comes from here!!!VVVVV
        //this is what a train does
        //loops until train is dispatched - leaves the yard
        
        while(this.hold == false && this.dispatched == false){
            //acquire switch locks
            //if all locks acquired then move train
            if(firstSwitch.lockSwitch() == true){
                System.out.println("Train " + trainNum + ": HOLDS LOCK on Switch " + firstSwitch.switchNum);
                    if(secondSwitch.lockSwitch() == true){
                        System.out.println("Train " + trainNum + ": HOLDS LOCK on Switch " + secondSwitch.switchNum);
                            if(thirdSwitch.lockSwitch() == true){
                                System.out.println("Train " + trainNum + ": HOLDS LOCK on Switch " + thirdSwitch.switchNum);
                                System.out.println("Train " + trainNum + ": HOLDS ALL NEEDED SWITCH LOCKS - train movement begins...\n\n");
                                moveTrain();
                               
                                System.out.println("\n\nTrain " + trainNum + ": clear of yard control");
                                System.out.println("Train " + trainNum + ": Releasing all switch locks");
                                firstSwitch.unlockSwitch();
                                System.out.println("Train " + trainNum + ": Unlocks/Releases lock on Switch " + firstSwitch.switchNum);
                                secondSwitch.unlockSwitch();
                                System.out.println("Train " + trainNum + ": Unlocks/Releases lock on Switch " + secondSwitch.switchNum);
                                thirdSwitch.unlockSwitch();
                                System.out.println("Train " + trainNum + ": Unlocks/Releases lock on Switch " + thirdSwitch.switchNum);
                                
                                System.out.println("\n\nTrain " + trainNum + ": Has been dispatched and moves out of yard control and into CTC");
                                System.out.println("@ @ @ TRAIN " + trainNum + ": DISPATCHED @ @ @\n\n");
                                
                                counter++;
                                dispatchSequence = counter;
                                allLocks = true;
                                dispatched = true;
                            }//end inner if
                            else{
                                System.out.println("\nTrain " + trainNum + ": UNABLE TO LOCK third required switch: Switch " + thirdSwitch.switchNum);
                                System.out.println("Train " + trainNum + ": releasing lock on first and second required switches: Switch " + firstSwitch.switchNum + " and Switch " + secondSwitch.switchNum + ".\n Train will wait...\n");
                                firstSwitch.unlockSwitch();
                                secondSwitch.unlockSwitch();
                                waitTimeForLockRequest();
                            }
                    }//end center if
                    else{
                        System.out.println("\nTrain " + trainNum + ": UNABLE TO LOCK second required switch: Switch " + secondSwitch.switchNum);
                        System.out.println("Train " + trainNum + ": releasing lock on first required switch: Switch " + firstSwitch.switchNum);
                        firstSwitch.unlockSwitch();
                        waitTimeForLockRequest();
                    }
            }//end outer if
            //else release all locks, wait for a while, then try again
            else{
                System.out.println("\nTrain " + trainNum + ": UNABLE TO LOCK first required switch: Switch " + firstSwitch.switchNum + ". Train will wait...\n");
                waitTimeForLockRequest();//thread goes to sleep
            }
        }//end while
        if(allLocks){
            TrainSimulator.theFleet[index].dispatched = true;
            TrainSimulator.theFleet[index].dispatchSequence = dispatchSequence;
        }
    }//end run
}//end class train