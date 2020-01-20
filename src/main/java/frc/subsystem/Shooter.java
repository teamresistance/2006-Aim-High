package frc.subsystem;
/*
Author: Team 86
History: 
jch - 1/2020 - Original Release

TODO: - Need to add pid control with encoder

Desc.
The shooter spins a wheel to shoot a 7" ball at approx. 35 fps.  It starts when a button is pressed
and stop on the press of another button.
Presently, the speed is fixed, set thru the sdb.
Future, Control the speed of the shooter to a setpoint thru a pid loop.  Best guess is approx.
3300 rpm.  This requires an encoder on the wheel for feedback.
Future future, change the victor to a TalonSRX and move the pid to the Talon.

Sequence:
(0) Default, the motor is set to 0.0, off.
When button is pressed the shooter is set to 0.0.
(1) Normal control, presently fixed value (0.7), future pid loop encoder.
(2) When a ball first enters the shooter the additional load causes the shooter to slow down.  The
first ball may come out hot but other balls will come out short until the pid can compensate.  One
method is to bump the speed up until back to setpoint (or presently, just some time period).
(3) Once the shooter is upto speed idle it when not shooting.
*/

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.io.hdw_io.IO;
import frc.io.joysticks.JS_IO;
import frc.util.timers.OnDly;
import frc.util.timers.OnOffDly;

public class Shooter {
    private static TalonSRX shooter = IO.shooter;

    private static double shooterPct = 0.7;

    private static int state;
    private static int prvState;

    //Constructor
    public Shooter() {
        init();
    }

    public static void init() {
        SmartDashboard.putNumber("Shooter Spd", shooterPct);
        cmdUpdate(0.0);
        state = 0;
    }

    // I am the determinator
    private static void determ(){
        state =  JS_IO.shooterRun.get() ? 1 : 0;
        if(JS_IO.shooterRun.get()) state = 1;
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
        case 1: //Shoot at default speed
            cmdUpdate( shooterPct );
            prvState = state;
            if(!JS_IO.shooterRun.get()) state = 3;
            break;
        case 2: // Shooter slow, bump to compensate
            cmdUpdate( 100.0 );
            prvState = state;
            break;
        case 3: // Shooter idle after shooting once
            cmdUpdate( 0.3 );
            prvState = state;
            break;
        default: // Default, mtr=0.0
            cmdUpdate( 0.0 );
            prvState = state;
            System.out.println("Bad Shooter state - " + state);
            break;
        }
    }

    // Smartdashboard shtuff
    private static void sdbUpdate(){
        shooterPct = SmartDashboard.getNumber("Shoot Spd", 0.7);
    }

    // Send commands to shooter motor
    private static void cmdUpdate(double spd){
        shooter.set(ControlMode.PercentOutput, spd);
    }

    //Returns if motor is off.
    public static boolean get(){
        return shooter.getMotorOutputPercent() < 0.1;
    }

    // TODO: Need to add code once we have the Talon encoder reading.
    public static boolean isAtSpd(){
        return true;
    }
}
