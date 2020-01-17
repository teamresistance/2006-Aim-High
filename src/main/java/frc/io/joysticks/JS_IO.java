package frc.io.joysticks;
/*
Original Author: Joey & Anthony
Rewite Author: Jim Hofmann
History:
J&A - 11/6/2019 - Original Release
JCH - 11/6/2019 - Original rework

TODO: Exception for bad or unattached devices.
      Auto config based on attached devices and position?
      Add enum for jsID & BtnID?  Button(eLJS, eBtn6) or Button(eGP, eBtnA)

Desc: Reads joystick (gamePad) values.  Can be used for different stick configurations
    based on feedback from Smartdashboard.  Various feedbacks from a joystick are
    implemented in classes, Button, Axis & Pov.

    This version is using named joysticks to istantiate axis, buttons & axis
*/

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.io.js_btns.Axis;
import frc.io.js_btns.Button;
import frc.io.js_btns.Pov;

//Declares all joysticks, buttons, axis & pov's.
public class JS_IO{
    public static int jsConfig = 0;     //0=Joysticks, 1=left Joystick only, 2=gamePad only
                                        //3=Mixed LJS & GP, 4=Nintendo Pad
    // Declare all possible Joysticks
    public static Joystick leftJoystick = new Joystick(0);      // Left JS
    public static Joystick rightJoystick = new Joystick(1);     // Right JS
    public static Joystick coJoystick = new Joystick(2);        // Co-Dvr JS
    public static Joystick gamePad = new Joystick(3);           // Normal mode only (not Dual Trigger mode)
    public static Joystick neoPad = new Joystick(4);            // Nintendo style gamepad
    public static Joystick arJS[] = {leftJoystick,rightJoystick, coJoystick,
                                     gamePad, neoPad};
    // Declare all stick control
    public static Axis rotateBot;     // Mecanum drive rotate 
    public static Axis xDriveBot;     // Left/Right Drive
    public static Axis yDriveBot;     // Forward/Reverse Drive

    // Drive buttons
    public static Button gyroResetBtn;
    public static Button holdAngle90Btn;
    public static Button holdAngle30Btn;
    public static Button fieldOrientedBtn;
    public static Button robotOrientedBtn;
    // Hatch
    public static Button forkBtn;       // Start fork action
    // Climb
    public static Button lowClimbBtn;
    public static Button highClimbBtn;
    public static Button fixFrontClimbBtn;
    public static Button fixRearClimbBtn;
    public static Button resetClimbBtn;
    // Snorfler
    public static Button snorflerBtn;   // Start snofle action
    // Shaft
    public static Button liftUpBtn;     // raise the lifter
    // Slide
    public static Button slideResetRightBtn;
    public static Button slideResetLeftBtn;

    // Overrides
    public static Pov ovrSnorfPov;    //Override snorfler 0=suck, 180=spit
    // Test-Snorfler backRolloer stall override
    public static Button runBtn;

    // Constructor
    public JS_IO(){
        init();
    }

    public static void init(){
        SmartDashboard.putNumber("JS_Config", 0);
        configJS();
    }

    public static void update() {   //Chk for Joystick configuration
        if(jsConfig != SmartDashboard.getNumber("JS_Config", 0)){
            jsConfig = (int)SmartDashboard.getNumber("JS_Config", 0);
            configJS();
        }
    }

    public static void configJS() {   //Default Joystick else as gamepad
        jsConfig = (int)SmartDashboard.getNumber("JS_Config", 0);

        switch( jsConfig ){
            case 0:     // Normal 3 joystick config
                Norm3JS();
            break;

            case 1:     // Gamepad only
                A_GP();
            break;

            case 2:     // Left joystick only
                A_JS();
            break;

            case 3:     // 1 Joystick & Gamepad
                JS_GP();

            case 4:     // Nintendo Gamepad
                NeoGP();
            break;

            default:    // Bad assignment
                CaseDefault();
            break;

        }
    }

    //================ Controller actions ================

    // ----------- Normal 3 Joysticks -------------
    private static void Norm3JS(){
        // All stick axisesssss
        xDriveBot = new Axis(rightJoystick, 0);
        yDriveBot = new Axis(rightJoystick, 1);
        rotateBot = new Axis(leftJoystick, 1);
        // Drive buttons
        gyroResetBtn = new Button(leftJoystick, 6);
        holdAngle90Btn = new Button(leftJoystick, 7);
        holdAngle30Btn = new Button(leftJoystick, 9);
        fieldOrientedBtn = new Button(leftJoystick, 8);
        robotOrientedBtn = new Button(leftJoystick, 10);
        // Hatch
        forkBtn = new Button(coJoystick, 1);
        // Climb
        lowClimbBtn = new Button(coJoystick, 12);
        highClimbBtn = new Button(coJoystick, 10);
        fixFrontClimbBtn = new Button(coJoystick, 7);
        fixRearClimbBtn = new Button(coJoystick, 8);
        resetClimbBtn = new Button(coJoystick, 2);
        // Snorfler
        snorflerBtn = new Button(coJoystick, 4);
        // Shaft
        liftUpBtn = new Button(coJoystick, 11);
        // Slide --- NOTE: deleted! ----
        slideResetRightBtn = new Button(coJoystick, 5);
        slideResetLeftBtn = new Button(coJoystick, 6);

        // Overrides snorfler, 0=spit, 180=suck
        ovrSnorfPov = new Pov(coJoystick, 0);     //0=suck, 180=spit
        // Test rear wheel
        runBtn = new Button(coJoystick, 9);
    }

    // ----- gamePad only --------
    private static void A_GP(){
        // All stick axisesssss
        xDriveBot = new Axis(gamePad, 4);
        yDriveBot = new Axis(gamePad, 5);
        rotateBot = new Axis(gamePad, 0);

        // Drive buttons
        gyroResetBtn = new Button(gamePad, 8);          // Back button
        //holdAngle90Btn = new Button(gamePad, 2);        // B Button
        holdAngle30Btn = new Button(gamePad, 1);        // A Button
        fieldOrientedBtn = new Button(gamePad, 4);   // Y Button
        robotOrientedBtn = new Button(gamePad, 3);   // X Button
        // Hatch
        forkBtn = new Button(gamePad, 5);            //Start Fork action Button
        // Climb
        lowClimbBtn = new Button(null, 100);
        highClimbBtn = new Button(null, 100);
        fixFrontClimbBtn = new Button(null, 100);
        fixRearClimbBtn = new Button(null, 100);
        resetClimbBtn = new Button(null, 100);
        // Snorfler
        snorflerBtn = new Button(gamePad, 2);           // Left Button
        // Shaft
        liftUpBtn = new Button(gamePad, 3);              // Right Button
        // Slide --- NOTE: deleted! ----
        slideResetRightBtn = new Button(null, 100);
        slideResetLeftBtn = new Button(null, 100);

        // Overrides
        ovrSnorfPov = new Pov(gamePad, 0);
        // Test
        runBtn = new Button(null, 100);
    }

    // ------------ One Joystick only -----------
    private static void A_JS(){
        // All stick axisesssss
        xDriveBot = new Axis(leftJoystick, 0);    // then drive fwd/bkwd
        yDriveBot = new Axis(leftJoystick, 1);
        rotateBot = new Axis(leftJoystick, 2);    //Rotate twist

        // Drive buttons
        gyroResetBtn = new Button(leftJoystick, 6);
        holdAngle90Btn = new Button(leftJoystick, 7);
        holdAngle30Btn = new Button(leftJoystick, 9);
        fieldOrientedBtn = new Button(leftJoystick, 8);
        robotOrientedBtn = new Button(leftJoystick, 10);
        // Hatch
        forkBtn = new Button(null, 100);
        // Climb
        lowClimbBtn = new Button(null, 100);
        highClimbBtn = new Button(null, 100);
        fixFrontClimbBtn = new Button(null, 100);
        fixRearClimbBtn = new Button(null, 100);
        resetClimbBtn = new Button(null, 100);
        // Snorfler
        snorflerBtn = new Button(null, 100);
        // Shaft
        liftUpBtn = new Button(null, 100);
        // Slide --- NOTE: deleted! ----
        slideResetRightBtn = new Button(null, 100);
        slideResetLeftBtn = new Button(null, 100);

        // Overrides
        ovrSnorfPov = new Pov(leftJoystick, 0);
        // Test
        runBtn = new Button(null, 100);
    }

    // ----- Mixed Left Joystick & gamePad only --------
    private static void JS_GP(){
        // All stick axisesssss
        xDriveBot = new Axis(leftJoystick, 0);    // then drive fwd/bkwd
        yDriveBot = new Axis(leftJoystick, 1);
        rotateBot = new Axis(leftJoystick, 2);    //Rotate

        // Drive buttons
        gyroResetBtn = new Button(leftJoystick, 6);
        holdAngle90Btn = new Button(leftJoystick, 7);
        holdAngle30Btn = new Button(leftJoystick, 9);
        fieldOrientedBtn = new Button(leftJoystick, 8);
        robotOrientedBtn = new Button(leftJoystick, 10);
        // Hatch
        forkBtn = new Button(gamePad, 7);
        // Climb
        lowClimbBtn = new Button(null, 100);
        highClimbBtn = new Button(null, 100);
        fixFrontClimbBtn = new Button(null, 100);
        fixRearClimbBtn = new Button(null, 100);
        resetClimbBtn = new Button(null, 100);
        // Snorfler
        snorflerBtn = new Button(gamePad, 5);
        // Shaft
        liftUpBtn = new Button(gamePad, 6);
        // Slide --- NOTE: deleted! ----
        slideResetRightBtn = new Button(null, 100);
        slideResetLeftBtn = new Button(null, 100);

        // Overrides
        ovrSnorfPov = new Pov(neoPad, 0);
        // Test
        runBtn = new Button(null, 100);
    }

    // ----- Nintendo gamePad only --------
    private static void NeoGP(){
        // All stick axisesssss
        rotateBot = new Axis(neoPad, 1);
        xDriveBot = new Axis(neoPad, 0);
        yDriveBot = new Axis(null, 100);

        // Drive buttons
        gyroResetBtn = new Button(neoPad, 3);
        holdAngle90Btn = new Button(neoPad, 7);
        holdAngle30Btn = new Button(neoPad, 1);
        fieldOrientedBtn = new Button(neoPad, 4);
        robotOrientedBtn = new Button(neoPad, 3);
        // Hatch
        forkBtn = new Button(neoPad, 2);
        // Climb
        lowClimbBtn = new Button(null, 100);
        highClimbBtn = new Button(null, 100);
        fixFrontClimbBtn = new Button(null, 100);
        fixRearClimbBtn = new Button(null, 100);
        resetClimbBtn = new Button(null, 100);
        // Snorfler
        snorflerBtn = new Button(neoPad, 3);
        // Shaft
        liftUpBtn = new Button(neoPad, 4);
        // Slide
        slideResetRightBtn = new Button(neoPad, 5);
        slideResetLeftBtn = new Button(neoPad, 6);

        // Overrides
        ovrSnorfPov = new Pov(null, -1);   //no neoPad.getPOV();
        // Test
        runBtn = new Button(null, -1);
    }

    // ----------- Case Default -----------------
    private static void CaseDefault(){
        // All stick axisesssss
        rotateBot = new Axis(null, 100);
        xDriveBot = new Axis(null, 100);
        yDriveBot = new Axis(null, 100);

        // Drive buttons
        gyroResetBtn = new Button(null, 100);
        holdAngle90Btn = new Button(null, 100);
        holdAngle30Btn = new Button(null, 100);
        fieldOrientedBtn = new Button(null, 100);
        robotOrientedBtn = new Button(null, 100);
        // Hatch
        forkBtn = new Button(null, 100);
        // Climb
        lowClimbBtn = new Button(null, 100);
        highClimbBtn = new Button(null, 100);
        fixFrontClimbBtn = new Button(null, 100);
        fixRearClimbBtn = new Button(null, 100);
        resetClimbBtn = new Button(null, 100);
        // Snorfler
        snorflerBtn = new Button(null, 100);
        // Shaft
        liftUpBtn = new Button(null, 100);
        // Slide
        slideResetRightBtn = new Button(null, 100);
        slideResetLeftBtn = new Button(null, 100);

        // Overrides
        ovrSnorfPov =  new Pov(null, -1);
        // Test
        runBtn = new Button(null, 100);
    }
}