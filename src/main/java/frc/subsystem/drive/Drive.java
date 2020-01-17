package frc.subsystem.drive;
// History:
// 11/6/2019 jch - added scaleFactor to sdb.
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.subsystem.drive.DriveMath.DriveType;
import frc.io.hdw_io.IO;
import frc.io.joysticks.JS_IO;

import frc.util.Time;
import frc.util.TurnTo;
import frc.util.Updatable;

public class Drive implements Updatable {

	private TalonSRX[] driveMotors = IO.driveMotors;
	private DriveMath drive;
	private AHRS navX = IO.ahrs;

	private boolean grabAngleOnce = false;
	private double holdAngle = 0;

	private double kP = .002;
	private double kI;
	private double kD = 0.03;
	private double kF;
	private double maxI;

	private double scaleFactor = 0.7;	// Scale Driver Sensitivity
	private double scaledX = 0.0;
	private double scaledY = 0.0;
	private double scaledRotate = 0.0;

	private int state;
	private int prvState;

	private boolean isFieldOriented = true;

	private boolean once = true;
	private double time;

	public Drive() {
		init();
	}

	public void init() {
		drive.setPIDValues(kP, kI, kD, kF, maxI);
		isFieldOriented = false;
		state = 0;
		time = 0;
		SmartDashboard.putNumber("P",.002);
		SmartDashboard.putNumber("I",0);
		SmartDashboard.putNumber("D",.03);
		SmartDashboard.putNumber("F",.002);
		SmartDashboard.putNumber("maxI",0.1);
		SmartDashboard.putNumber("scaleFactor",0.7);
	}

	// I am the Determinator
	private void determ(){
		if(JS_IO.fieldOrientedBtn.onButtonPressed()){ isFieldOriented = true; }
		if(JS_IO.robotOrientedBtn.onButtonPressed()){ isFieldOriented = false; }

		// Hold down 30 or 90 btn to hold angle else std drive
		state = JS_IO.holdAngle30Btn.get() ? 30 :
				JS_IO.holdAngle90Btn.get() ? 90 : 0;

		// Reset the Gyro
		if(JS_IO.gyroResetBtn.onButtonPressed()){
			once = true;		// ??
			IO.ahrs.reset();
		}
	}

	// Update drive system
	public void update() {
		sdbUpdate();
		determ();
		scaleInput();

		//-------------------------------------------------------------------------
		//----------------------------- Main Controller ---------------------------
		//-------------------------------------------------------------------------

		switch (state) {
			case 0:		// Normal Robot Oriented
				grabAngleOnce = true;
				drive.drive(DriveType.STICK_FIELD, scaledX, scaledY, scaledRotate, 0);
			break;
		
			case 10:	// Normal Field Oriented
				if(isFieldOriented){
					if(scaledRotate == 0 && once){
						time = Time.getTime();
						once = false;
					}
					if (scaledRotate == 0 && (Time.getTime() - time) >= 0.25) {
						if (grabAngleOnce) {
							grabAngleOnce = false;
							holdAngle = gyro.getNormalizedAngle();
						}
						drive.drive(DriveType.ROTATE_PID, scaledX, scaledY, scaledRotate, holdAngle);
					} else if(scaledRotate == 0){
						grabAngleOnce = true;
						drive.drive(DriveType.STICK_FIELD, scaledX, scaledY, scaledRotate, gyro.getNormalizedAngle());
					} else {
						grabAngleOnce = true;
						once = true;
						drive.drive(DriveType.STICK_FIELD, scaledX, scaledY, scaledRotate, gyro.getNormalizedAngle());
					}
				}				
				break;

			case 30:	// Hold to nearest 30 degree heading
				if(isFieldOriented){
					drive.drive(DriveType.ROTATE_PID, scaledX, scaledY, 0,
								TurnTo.align(gyro.getNormalizedAngle(),false));
					grabAngleOnce = true;
				}else{
					drive.setRobotPID90(false);
					drive.drive(DriveType.ROBOT_PID, scaledX, scaledY, 0, 0);
					grabAngleOnce = true;
				}
				break;

			case 90:	// Hold to nearest 90 degree heading
				if(isFieldOriented){
					drive.drive(DriveType.ROTATE_PID, scaledX, scaledY, 0,
								TurnTo.align(gyro.getNormalizedAngle(),true));
					grabAngleOnce = true;
				}else{
					//if(Time.getTime() - time >= .5){
						drive.setRobotPID90(true);
						drive.drive(DriveType.ROBOT_PID, scaledX, scaledY, 0, 0);
					//}else{
					//	drive.drive(DriveType.ROTATE_PID, scaledX, scaledY, 0, TurnTo.align(gyro.getNormalizedAngle()));
					//}
					grabAngleOnce = true;
				}
				break;

			case 40:	// Hold present degrees, heading
			
			break;

			default:
				break;
		}




		//if(JoystickIO.holdAngle.onButtonPressed()){
		//	time = Time.getTime();
		//}
		SmartDashboard.putNumber("drive time", time);
		if(JS_IO.holdAngle90Btn.get()){
			if(isFieldOriented){
				drive.drive(DriveType.ROTATE_PID, scaledX, scaledY, 0,
				            TurnTo.align(gyro.getNormalizedAngle(),true));
				grabAngleOnce = true;
			}else{
				//if(Time.getTime() - time >= .5){
					drive.setRobotPID90(true);
					drive.drive(DriveType.ROBOT_PID, scaledX, scaledY, 0, 0);
				//}else{
				//	drive.drive(DriveType.ROTATE_PID, scaledX, scaledY, 0, TurnTo.align(gyro.getNormalizedAngle()));
				//}
				grabAngleOnce = true;
			}

		} else if(JS_IO.holdAngle30Btn.get()){
			if(isFieldOriented){
				drive.drive(DriveType.ROTATE_PID, scaledX, scaledY, 0,
				            TurnTo.align(gyro.getNormalizedAngle(),false));
				grabAngleOnce = true;
			}else{
				drive.setRobotPID90(false);
				drive.drive(DriveType.ROBOT_PID, scaledX, scaledY, 0, 0);
				grabAngleOnce = true;
			}

		}else {
			if(isFieldOriented){
				if(scaledRotate == 0 && once){
					time = Time.getTime();
					once = false;
				}
				if (scaledRotate == 0 && (Time.getTime() - time) >= 0.25) {
					SmartDashboard.putNumber("inside", 1);
					SmartDashboard.putBoolean("Is Holding Angle", true);
					if (grabAngleOnce) {
						grabAngleOnce = false;
						holdAngle = gyro.getNormalizedAngle();
					}
					drive.drive(DriveType.ROTATE_PID, scaledX, scaledY, scaledRotate, holdAngle);
				} else if(scaledRotate == 0){
					SmartDashboard.putNumber("inside", 2);
					grabAngleOnce = true;
					drive.drive(DriveType.STICK_FIELD, scaledX, scaledY, scaledRotate, gyro.getNormalizedAngle());
				} else {
					SmartDashboard.putNumber("inside", 3);
					grabAngleOnce = true;
					once = true;
					drive.drive(DriveType.STICK_FIELD, scaledX, scaledY, scaledRotate, gyro.getNormalizedAngle());
				}
			} else{
				grabAngleOnce = true;
				drive.drive(DriveType.STICK_FIELD, scaledX, scaledY, scaledRotate, 0);
			}
		}
	}

	private void scaleInput(){
		//Scale the drive sensitivity down
		scaledX = (scaleFactor) * (JS_IO.xDriveBot.get());
		scaledY = (-scaleFactor) * (JS_IO.yDriveBot.get());
		scaledRotate = (scaleFactor) * (JS_IO.rotateBot.get());
		
		// Add deadband to Scaled Rotate
		if(scaledRotate < 0.2 && scaledRotate > -0.2){
			 scaledRotate = 0;
		}
	}

	// Handle Smartdashboard
	private void sdbUpdate(){
		scaleFactor = SmartDashboard.getNumber("scaleFactor",0.7);

		drive.setPIDValues(SmartDashboard.getNumber("P", .002), SmartDashboard.getNumber("I",0),
						   SmartDashboard.getNumber("D", .03), SmartDashboard.getNumber("F", 0),
						   SmartDashboard.getNumber("maxI", .1));

		//Degub Info
		SmartDashboard.putNumber("Gyro", gyro.getNormalizedAngle());
		SmartDashboard.putBoolean("robot",isFieldOriented);
		SmartDashboard.putNumber("scaled rotate",scaledRotate);
		SmartDashboard.putNumber("Angle Being Held", holdAngle);
		SmartDashboard.putBoolean("Grab Angle Once", grabAngleOnce);

		SmartDashboard.putNumber("drive time", time);
		SmartDashboard.putNumber("inside", 1);
		SmartDashboard.putBoolean("Is Holding Angle", true);
}

	// Drive command(?) This drive is not used.  All use DriveMath.drive.
	public void driveWTF(DriveType driveState, double x, double y, double rotation, double angle) {
		drive.drive(driveState, x, y, rotation, angle);
	}

}
