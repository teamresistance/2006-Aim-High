package frc.util;
/*
Author: Jim Hofmann
History: 
JCH - 11/8/2019 - Original Release

TODO: - Check it out.

Desc:
Can be used to set a wait timer
*/

public class WaitTmr{

    private long waitDlyTmr;

    //Constructor
    public WaitTmr(){
        //nothing here
    }

    //Set the timer, passed seconds
    public void setTmr( double dlyTm){
        waitDlyTmr = System.currentTimeMillis() + (long)(dlyTm * 1000);
    }

    //Set the timer passed mSec
    public void setTmr( int dlyTm){
        waitDlyTmr = System.currentTimeMillis() + dlyTm;
    }

    //Check on the timer.  Returns true if running.
    public boolean get(){
        return System.currentTimeMillis() > waitDlyTmr;
    }

    //Check on the timer.  Returns true if running.
    public boolean isWait(){
        return get();
    }

    //Check on the timer.  Returns true if running.
    public boolean isDone(){
        return !get();
    }
}