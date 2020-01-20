package frc.io.hdw_io;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.io.joysticks.JS_IO;

import com.revrobotics.ColorSensorV3;

/* temp to fill with latest faults */
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;

import com.kauailabs.navx.frc.AHRS;

public class IO {
    // navX
    // public static NavX navX = new NavX();
    public static AHRS ahrs;

    // PDP
    public static PowerDistributionPanel pdp = new PowerDistributionPanel(21);

    // Turret
    public static Victor lifter = new Victor(0);
    public static Victor turret = new Victor(1);
    public static TalonSRX shooter = new TalonSRX(12);

    public static Encoder shooterRPM = new Encoder(0, 1);
    public static AnalogInput turretPot = new AnalogInput(0);    //Figure out how to chg to pot interface
    public static DigitalInput turretCCWes = new DigitalInput(0);   // CCW End Switch
    public static DigitalInput turretCWes = new DigitalInput(1);    // CC End Switch

    //---------- WoF, Color Sensor -----------------
    /**
    * Change the I2C port below to match the connection of your color sensor
    */
    private static final I2C.Port i2cPort = I2C.Port.kOnboard;

    /**
    * A Rev Color Sensor V3 object is constructed with an I2C port as a 
    * parameter. The device will be automatically initialized with default 
    * parameters.
    */
    public static ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);

    // ----------- Talon pidf control ------------------
    public static Faults _faults = new Faults(); /* temp to fill with latest faults */
    

    public static void init(){
        shooter.setInverted(false);
        shooter.setSensorPhase(false); // <<<<<< Adjust this to correct phasing with motor

    }

    public static void update(){
        SmartDashboard.putNumber("Shooter Pwr", pdp.getCurrent(12));

        //------- Shooter Talon pidf control setup -------------
        /* check our live faults */
        shooter.getFaults(_faults);
        if(JS_IO.ptrShtrDiag.get()) {
            System.out.println("Sensor Vel:" + shooter.getSelectedSensorVelocity());
            System.out.println("Sensor Pos:" + shooter.getSelectedSensorPosition());
            System.out.println("Out %" + shooter.getMotorOutputPercent());
            // System.out.println("Out Of Phase:" + shooter.SensorOutOfPhase);
        }
    }
}
