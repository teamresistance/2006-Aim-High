package frc.subsystem;
/*
Author: Team 86
History: 
jch - 1/2020 - Original Release

TODO: - Need to test

Desc.
The lifter lifts the balls to the shooter.

Sequence:
(0)Default, the motor is set to 0.0, off, when no buttons pressed.
(1)Balls move up when up button is pressed.
(2)Balls move dn when dn button is pressed.
*/

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.Victor;

import frc.io.hdw_io.IO;
import frc.io.joysticks.JS_IO;
import frc.util.timers.OnDly;
import frc.util.timers.OnOffDly;

public class Lifter {
    private static Victor lifter = IO.lifter;

    private static double lifterPct = 0.7;

    private static int state;
    private static int prvState;

    //Constructor
    public Lifter() {
        init();
    }

    public static void init() {
        SmartDashboard.putNumber("Lift Spd", lifterPct);
        cmdUpdate(0.0);
        state = 0;
    }

    // I am the determinator
    private static void determ(){
        state = 0;
        if(JS_IO.lifterUp.get()) state = 1;
        if(JS_IO.lifterDn.get()) state = 2;
    }

    public static void update() {
        sdbUpdate();
        determ();
        //------------- Main State Machine --------------
        // cmd update( shooter speed )
        switch(state){
        case 0: // Default, mtr=0.0
            cmdUpdate( 0.0 );
            prvState = state;
            break;
        case 1: // Move balls up
            cmdUpdate( lifterPct );
            prvState = state;
            break;
        case 2: // Move balls down
            cmdUpdate( -lifterPct );
            prvState = state;
            break;
        default: // Bad state, mtr off
            cmdUpdate( 0.0 );
            prvState = state;
            System.out.println("Bad Lifter state - " + state);
            break;
        }
    }

    // Smartdashboard shtuff
    private static void sdbUpdate(){
        lifterPct = SmartDashboard.getNumber("Lift Spd", 0.7);
    }

    // Send commands to shooter motor
    private static void cmdUpdate(double spd){
        lifter.set(spd);
    }

    //Returns if motor is off.
    public static boolean get(){
        return Math.abs(lifter.get()) < -0.1;
    }
}
