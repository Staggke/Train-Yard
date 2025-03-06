/*
Name: Kaitlyn Stagg
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 16, 2025
Class: Switch
Description: This class implements the sharable/lockable/synchronized
             switch objects shared by trains moving through the yard
*/

import java.util.concurrent.locks.ReentrantLock;

public class Switch {
    protected int switchNum;
    protected ReentrantLock lock;


    //create switch object
    public Switch(int switchNumArg, ReentrantLock lockArg){
      this.switchNum = switchNumArg;
      this.lock = lockArg;
    }//end constructor switch

    //request switch locks
    public boolean lockSwitch(){
      return lock.tryLock();
    }

    public void unlockSwitch(){
      //lock is released from train currently holding the switch lock
      lock.unlock();
    }
}//end class switch
