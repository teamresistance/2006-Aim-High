package frc.io.hdw_io;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Compressor;
// import edu.wpi.first.wpilibj.PowerDistributionPanel;
// import edu.wpi.cscore.UsbCamera;
// import edu.wpi.first.cameraserver.CameraServer;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class IO {

    // drive
    public static TalonSRX frontLeft = new TalonSRX(1);
    public static TalonSRX frontLeftInv = new TalonSRX(2);
    public static TalonSRX backLeft = new TalonSRX(3);
    public static TalonSRX backLeftInv = new TalonSRX(4);
    public static TalonSRX frontRight = new TalonSRX(5);
    public static TalonSRX frontRightInv = new TalonSRX(6);
    public static TalonSRX backRight = new TalonSRX(7);
    public static TalonSRX backRightInv = new TalonSRX(8);
    public static TalonSRX[] driveMotors = { frontLeft, frontLeftInv, 
        backLeft, backLeftInv, frontRight, frontRightInv, backRight, backRightInv };
    // navX
    // public static NavX navX = new NavX();
    public static AHRS ahrs;
    // camera
    // public static UsbCamera camOne = CameraServer.getInstance().startAutomaticCapture(0);
    // public static UsbCamera camTwo = CameraServer.getInstance().startAutomaticCapture(1);
    // air
    public static Compressor compressor = new Compressor(0);
    public static Relay compressorRelay = new Relay(0);
    // fork
    public static ISolenoid forkClsdSV = new InvertibleSolenoid(0, 0);  //true=fork closed
    public static ISolenoid slideOutSV = new InvertibleSolenoid(0, 1);  //Used by fork & snorfler
    public static InvertibleDigitalInput hasHatch = new InvertibleDigitalInput(0, false);
    // snorfler
    public static ISolenoid armDnSV = new InvertibleSolenoid(0, 2);
    public static InvertibleDigitalInput snorflerBanner = new InvertibleDigitalInput(3, false);
    public static VictorSPX snorflerMotor = new VictorSPX(11);
    // climb
    public static TalonSRX frontClimb = new TalonSRX(9);
    public static TalonSRX backClimb = new TalonSRX(10);
    public static Relay backWheelClimb = new Relay(1);
    public static InvertibleDigitalInput rearClimbLimit = new InvertibleDigitalInput(1, true); 
    public static InvertibleDigitalInput frontClimbLimit = new InvertibleDigitalInput(4, true); //see if this is true or false
    // slider
    public static Relay slipMotor = new Relay(2);
    // NOTE: THIS WAS REMOVED! - These may or may not be backwards
    // public static InvertibleDigitalInput leftLimit = new InvertibleDigitalInput(2,true);
    // public static InvertibleDigitalInput rightLimit = new InvertibleDigitalInput(1, true);
    // Lifter (Old name Shaft)
    public static ISolenoid liftUpSV = new InvertibleSolenoid(0, 3);

    //public static PowerDistributionPanel pdp = new PowerDistributionPanel(21);
   
    static{
        frontRight.setInverted(true);
        frontRightInv.setInverted(true);
        backRight.setInverted(true);
        backRightInv.setInverted(true);

        // This would insure paired motors are in sync.  Cmds would ONLY be sent to master.
        //This would need to be test cautiously due to motors mechanically linked.
        // frontRight.setInverted(true);       //Reverse the right side
        // backRight.setInverted(true);

        // frontRightInv.follow(frontRight);   //then sync boost motors to their masters.
        // frontRightInv.setInverted(InvertType.OpposeMaster);
        // backRightInv.follow(backRight);
        // backRightInv.setInverted(InvertType.OpposeMaster);
        // frontLeftInv.follow(frontLeft);
        // frontLeftInv.setInverted(InvertType.OpposeMaster);
        // backLeftInv.follow(backLeft);
        // backLeftInv.setInverted(InvertType.OpposeMaster);

        for(TalonSRX motor : driveMotors){
            motor.configNeutralDeadband(.15);
        }
        frontClimb.setNeutralMode(NeutralMode.Brake);
        backClimb.setNeutralMode(NeutralMode.Brake);
    }
}
