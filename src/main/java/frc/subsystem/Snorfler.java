package frc.subsystem;
/*
Author: Joey & Anthony
History: 
JCH - 11/14/2019 - Total rewite to cleanup state machine
J&E - 2/2019 - Original Release

TODO: - Chged Timers.  Need to be retested.
      - Don't use state onDlyTmr or prvState. Delete?

Desc.
The Snorfler handles the Cargo (Ball).  There are 3 devices.  The snorfler, sucks in and 
spits out the ball.  The arm lowers the snorfler to the floor to snorfle (suck in) the ball.
And the slide, extends the snorfler (and hatch) to deliever the cargo (and hatch).  It also
needs to be out to clear the bumper when dropping the snorfler to the floor.

There are critical interlocks betwwen the arm and the lifter.  The arm CANNOT go
down if the lifter is up.  The lifter cannot go up if the arm is down.
There is also an interlock between the arm and the slide.  The arm cannot go down until
the slide is out.  The slide cannot come in until the arm is up.

Sequence:
(0)Default or Hold, the motor is set to 0.1 (small hold), arm is up and slide is in.
When button is pressed and the cargo(ball) is NOT in, then start retrieve cargo(1).
(1)To retrieve cargo set motor to 0.7 (suck), lower the arm and put out the slide.  Interaction is
handled in the command method.  If the button is released without the ball, the system returns
to Default.  If we have the ball for 200 mS move to tuck it in(2).
(2)Return to default position with a hold ball power.  Then return to default.
(0)Default or Hold the motor is set to 0.1 (small hold), arm is up and slide is in.
When button is pressed and there IS a ball then move to place the cargo (ball)(11).
(11)Prep to Deliever, extend the slide.
(12)When the slide indicates extended, set motor speed to -0.5 (spit) to deliever the cargo.
When the button is released return to Default(0)
*/

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import frc.io.hdw_io.IO;
import frc.io.hdw_io.InvertibleDigitalInput;
import frc.io.hdw_io.ISolenoid;
import frc.io.joysticks1.JS_IO;
import frc.util.timers.OnDly;
import frc.util.timers.OnOffDly;

public class Snorfler {
    private static VictorSPX snorfMtr = IO.snorflerMotor;   //or new VictorSPX(11)
    private static ISolenoid armDnSV = IO.armDnSV;          //or InvertibleSolenoid(0, 2)
    private static InvertibleDigitalInput hasBall = IO.snorflerBanner;  //or new InvertibleDigitalInput(3, false)

    private static double snorfOvrPct = 0.7;     //Positve suck, negative spit

    private static int state;
    private static int prvState;
    public static boolean slideOutReq;     //slideOut request, monitored by slide.  Shared with Fork

    private static OnDly onDlyTmr = new OnDly();
    private static OnOffDly armDnFBDly = new OnOffDly(250);  //Setup timer for 250 mS for feedback

    //Constructor
    public Snorfler() {
        init();
    }

    public static void init() {
        cmdUpdate(0.0, false, false, 200);
        state = 0;
    }

    private static void snorfDeter(){
        //no action needed
    }

    public static void update() {
        sdbUpdate();
        snorfDeter();
        //------------- Main State Machine --------------
        // cmd update( snorf mtr, arm, slide)
        switch(state){
        case 0: // Default placement, mtr=0.0, arm=up, slide=in, timer=200 mS
            cmdUpdate( 0.1, false, false, 200 );
            prvState = state;
            if(JS_IO.snorflerBtn.onButtonPressed()){
                state = hasBall.get() ? 11 : 1;
            }
            break;
            //----------  Retrieve Cargo  -----------
        case 1: //This is the grab state, mtr=0.7, arm=dn, slide=out, tmr=200
            cmdUpdate( 0.7, true, true, 200 );
            prvState = state;
            if( onDlyTmr.get(hasBall.get()) ) state = 2; //Hold cargo for 200 mSec
            if(JS_IO.snorflerBtn.onButtonReleased()) state = 0;
            break;
        case 2: // Raise arm with ball & retract slide.
                // arm must be up before retracting slide, handled in cmdUpdate
                // mtr=0.3, arm=up, slide=in, tmr=200
            cmdUpdate( 0.3, false, false, 200 );
            prvState = state;
            if(JS_IO.snorflerBtn.onButtonReleased()) state = 0;
            break;
        //----------  Deliever Cargo  -----------
        case 11: // Prep to Eject the Cargo, mtr=0.3, arm=up, slide=out, tmr=200
            cmdUpdate( 0.1, false, true, 200 );
            prvState = state;
            if(Slide.isOut()) state = 12;
            break;
        case 12: // Eject the Cargo, mtr=-0.5, arm=up, slide=out, tmr=200
            cmdUpdate( -0.5, false, true, 200 );
            prvState = state;
            if(JS_IO.snorflerBtn.onButtonReleased()) state = 0; //Return to beginning
            break;
        }
    }

    // Smartdashboard shtuff
    private static void sdbUpdate(){
        SmartDashboard.putBoolean("Baller", hasBall.get());
        SmartDashboard.putNumber("Snorf Ovr", JS_IO.ovrSnorfPov.get());
        SmartDashboard.putNumber("Snorfler States", state);
        SmartDashboard.putBoolean("Test - ", Slide.isOut());
    }

    // Send commands to snorfler motor
    private static void cmdUpdate(double snorfPct, boolean armDnCmd, boolean slideOutCmd, long onDly){
        snorfMtr.set(ControlMode.PercentOutput,                     //Issue snofler motor cmd
                     (JS_IO.ovrSnorfPov.equals(0) ? -snorfOvrPct :  //chk override else normal
                     (JS_IO.ovrSnorfPov.equals(180) ? snorfOvrPct : snorfPct )));

        // double tmp = (JS_IO.ovrSnorfPov.equals(-1) ? 0.5 :                    //if no override
        //              (JS_IO.ovrSnorfPov.equals(0) ? snorfMtrPct : -snorfMtrPct)); // else override
        // System.out.println("Pov - " + tmp);
        // snorfMtr.set(ControlMode.PercentOutput, tmp);   //Issue snofler motor cmd

        armDnSV.set(Slide.isOut() ? armDnCmd : false);  //slide must be out for arm to be down.
        armDnFBDly.get(armDnSV.get());                  //Update on/off FB delay, 100 mS

        //Slide out request.  Monitored in Slide.  Shared with Fork
        slideOutReq = !isArmDn() || armDnCmd ? slideOutCmd : true;

        onDlyTmr.setTm(onDly);  // Set on delay time
    }

    //arm is monitored by Lifter.  Cmd is delayed by 250 mS on & off to give time for the action.
    public static boolean get(){
        return armDnFBDly.get();
    }

    public static boolean isArmDn(){
        return get();
    }
}
