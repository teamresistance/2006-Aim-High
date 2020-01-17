package frc.util;
/*
Author: Jim Hofmann
History: 
JCH - 11/8/2019 - Original Release

TODO: - Look at changing type to enum with set like kOnDly, kOffDly, kOnOffDly, kPulse.

Desc:
Can be used to set a various tyoes of timers.  0=Delay on, 1=Delay off,
2=on&off delay, 3=Pulse (repeats while input true)
*/

public class Timer{
    private static long currentMSec;
    private boolean status;
    private long delayTm;
    private long delayTmr;
    private int type;       //0=Delay on, 1=Delay off, 2=on&off delay, 3=Pulse (repeats while input true)
    private boolean prvTrigger;

    // Constructor for mSec.
    public Timer(long mSecDelay, int type){
        set(mSecDelay);         // sets time delayTm
        this.type = type;
        this.status = false;
    }

    // Constructor for seconds
    public Timer(double secDelay, int type){
        set(secDelay);         // sets time delayTm
        this.type = type;
        this.status = false;
    }

    // Update the delay status.  0=On delay, 1=off delay, 2=on & off delay, 3=pulse
    public boolean update( boolean trigger ){
        currentMSec = System.currentTimeMillis();
        switch( type ){
            case 0:     //On delay, wait delayTm before returning true.  False when trigger goes false
                if( trigger != prvTrigger ){
                    delayTmr = trigger ? currentMSec + delayTm : 0;
                }
                // prvTrigger = trigger;
                status = trigger && currentMSec > delayTmr;
            break;

            case 1:     //Off delay, once pressed wait delayTm before returning false.
                if( trigger != prvTrigger ){
                    delayTmr = !trigger ? currentMSec + delayTm : 0;
                }
                // prvTrigger = trigger;
                status = trigger || currentMSec < delayTmr;
            break;

            case 2:     //Delay on/off, delay on, AND delay off.
                if( trigger != prvTrigger ){
                    delayTmr = currentMSec + delayTm;
                }
                // prvTrigger = trigger;
                status = (trigger && currentMSec > delayTmr) ||
                        (!trigger && currentMSec < delayTmr);
            break;

            case 3:     //Pulse, while triggered, on for delay then off for delay, repeat until no trigger.
                if( !trigger ){
                    status = false;
                }else{
                    if( !(currentMSec < delayTmr) ){
                        status = !status;
                        delayTmr = currentMSec + delayTm;
                    }
                }
            break;

        }
        prvTrigger = trigger;
        return status;
    }

    // setters
    public void set( int mSecTm ){ delayTm = mSecTm; }
    public void set( double secTm ){ delayTm = (long)(secTm * 1000); }
}