package frc.subsystem;
/*
Author: Team 86
History: 
jch - 1/2020 - Original Release

TODO: - Need to add Limelight control.

Desc.
The turret rotates the turret to track a target using CV Limelight.  Can be manually controlled.
A pot is used to limit rotation from -135 to 135 with 0 being forward and setpoint control.

Sequence:
(0)Default, the motor is set to 0.0, off.
(1)JS used to manually rotate.
(2)If a POV is pressed switch to setpoint control, for testing. 0/45/90/.../315
(3)Chgs SP to 0 then rotates forward.
*/

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Victor;

import frc.io.hdw_io.IO;
import frc.io.joysticks.JS_IO;
import frc.util.BotMath;
import frc.util.timers.OnDly;
import frc.util.timers.OnOffDly;

public class Turret {
    private static Victor turret = IO.turret;
    private static AnalogInput turretPot = IO.turretPot;

    private static double turretPct = 0.7;  // Used as -/+ limit
    private static double turretFB = 0.0;   // Scaled turret pot to degrees
    private static double turretSP = 0.0;   // SP degrees -135 to 135, 0 forward

    private static int state;
    private static int prvState;

    //Constructor
    public Turret() {
        init();
    }

    public static void init() {
        cmdUpdate(0.0);
        state = 0;
    }

    // I am the determinator
    private static void determ(){
        if(JS_IO.turretJSDir.get()) state = 1;
        if(!JS_IO.turretSP.isNone()) state = 2; // If POV pressed switch to POV SP
        if(JS_IO.turretZero.get()) state = 3;
    }

    public static void update() {
        sdbUpdate();
        determ();
        turretPotUpd();
        //------------- Main State Machine --------------
        // cmd update( shooter speed )
        switch(state){
        case 0: // Default, mtr=0.0
            turretPct = 0.0;
            cmdUpdate( turretPct );
            prvState = state;
            break;
        case 1: //Control with JS
            turretPct = JS_IO.turretRot.get();
            cmdUpdate( turretPct );
            prvState = state;
            break;
        case 2: //Control position to SP, by POV
            turretSP = JS_IO.turretSP.get() - 180.0;
            cmdUpdate( propCtl(turretSP, turretFB) );
            prvState = state;
            break;
        case 3: // Zero Position, SP = 0
            turretSP = 0.0;
            cmdUpdate( propCtl(turretSP, turretFB) );
            prvState = state;
            break;
        default: // mtr off
            cmdUpdate( 0.0 );
            prvState = state;
            System.out.println("Bad Turret state - " + state);
            break;
        }
    }

    // Smartdashboard shtuff
    private static void sdbUpdate(){
        SmartDashboard.putNumber("Turret Spd", turretPct);
        SmartDashboard.putNumber("Turret SP", turretSP);
        SmartDashboard.putNumber("Turret FB", turretFB);
        SmartDashboard.putBoolean("Turret CCW ES", IO.turretCCWes.get());
        SmartDashboard.putBoolean("Turret CW ES", IO.turretCWes.get());
    }

    // Send commands to turret motor
    private static void cmdUpdate(double spd){
        if( Math.abs(turretFB) > 135.0 ) spd = 0.0;
        if( IO.turretCCWes.get() && spd < 0 ) spd = 0;
        if( IO.turretCWes.get() && spd > 0 ) spd = 0;
        turret.set(spd);
    }

    // Scale turret pot
    private static void turretPotUpd(){
        turretFB = BotMath.Span(turretPot.getAverageVoltage(),
                            0.0, 5.0, -135.0, 135.0, false, false);
    }

    //Returns proportional response.  Poorman's P Loop
    public static double propCtl(double sp, double fb ){
        double err = fb - sp;
        if(Math.abs(err) > 3.0){
            err = BotMath.Span(err, -180, 180, 0.7, -0.7, true, false);
            return Math.abs(err) > 0.2 ? err : err > 0.0 ? -0.2 : 0.2;
        }
        return 0.0;
    }

    //Returns if motor is off.
    public static boolean get(){
        return turret.get() < 0.1;
    }

    public static boolean isAtSpd(){
        return true;
    }
}
