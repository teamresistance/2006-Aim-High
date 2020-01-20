package frc.io.hdw_io;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.joysticks.JS_IO;

 /* temp to fill with latest faults */
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;

import com.kauailabs.navx.frc.AHRS;

public class IO {

    // Turret
    public static Victor lifter = new Victor(0);
    public static Victor turret = new Victor(1);
    public static TalonSRX shooter = new TalonSRX(12);

    public static Encoder shooterRPM = new Encoder(0, 1);
    public static AnalogInput turretPot = new AnalogInput(0);    //Figure out how to chg to pot interface

    // navX
    // public static NavX navX = new NavX();
    public static AHRS ahrs;

    // PDP
    public static PowerDistributionPanel pdp = new PowerDistributionPanel(21);

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
