package frc.io.hdw_io;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;

import com.kauailabs.navx.frc.AHRS;

public class IO {

    // Turret
    public static Victor lifter = new Victor(0);
    public static Victor turret = new Victor(1);
    public static Victor shooter = new Victor(2);

    public static Encoder shooterRPM = new Encoder(0, 1);
    public static AnalogInput turretPot = new AnalogInput(0);    //Figure out how to chg to pot interface

    // navX
    // public static NavX navX = new NavX();
    public static AHRS ahrs;

}
