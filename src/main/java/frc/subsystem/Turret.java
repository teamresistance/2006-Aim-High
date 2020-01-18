package frc.subsystem;
/*
Author: Team 86
History: 
jch - 1/2020 - Original Release

TODO: - Need to add Limelight control.

Desc.
The turret rotates the turret to track a target using CV Limelight.  Can be manually controlled.
A pot is used to limit rotation from -135 to 135 with 0 being forward.

Sequence:
(0)Default, the motor is set to 0.0, off.
(1)JS used to manually rotate.
(2)JS chgs positional setpoint, for testing.
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
        SmartDashboard.putNumber("Shooter Spd", turretPct);
        cmdUpdate(0.0);
        state = 0;
    }

    // I am the determinator
    private static void determ(){
        if(JS_IO.shooterStart.get()) state = 1;
        if(JS_IO.shooterStop.get()) state = 0;
    }

    public static void update() {
        sdbUpdate();
        determ();
        //------------- Main State Machine --------------
        // cmd update( shooter speed )
        switch(state){
        case 0: // Default placement, mtr=0.0
            cmdUpdate( 0.0 );
            prvState = state;
            break;
        case 1: //Control with JS
            turretPct = JS_IO.turretRot.get();
            cmdUpdate( turretPct );
            prvState = state;
            break;
        case 2: //Control position SP with JS
            turretPct = JS_IO.turretRot.get();
            cmdUpdate( turretPct );
            prvState = state;
            break;
        case 3: // Zero Position, SP = 0
            turretPct = turretFB < -1.0 ? -0.7 : turretFB > 1.0 ? 0.7 : 0.0;
            cmdUpdate( turretPct );
            prvState = state;
            break;
        }
    }

    // Smartdashboard shtuff
    private static void sdbUpdate(){
        turretPct = SmartDashboard.getNumber("Turret Spd", 0.7);
    }

    // Send commands to turret motor
    private static void cmdUpdate(double spd){
        turretPotUpd();
        if( turretFB > 135.0 ) spd = 0.0;
        turret.set(spd);
    }

    // Scale turret pot
    private static void turretPotUpd(){
        turretFB = BotMath.Span(turretPot.getAverageVoltage(),
                            0.0, 5.0, -135.0, 135.0, false, false);
    }

    //Returns if motor is off.
    public static boolean get(){
        return turret.get() < 0.1;
    }

    public static boolean isAtSpd(){
        return true;
    }
}
