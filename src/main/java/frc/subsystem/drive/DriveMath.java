package frc.subsystem.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.hdw_io.NavX;
import frc.util.TurnTo;

public class DriveMath {
	private NavX gyro;
	private TalonSRX[] talons = new TalonSRX[8];

	// PID Constants -- TODO read from file
	private double kP;
	private double kI;
	private double kD;
	private double kF;
	private double maxI;

	private double kPRobot;
	private double kIRobot;
	private double kDRobot;
	private double kFRobot;
	private double maxIRobot;

	// Other variables
	private double integral;
	private double prevError;

	private double lfTotal;
	private double lbTotal;
	private double rfTotal;
	private double rbTotal;
	private boolean robotpid90;

	public enum DriveType {
		STICK_FIELD, ROTATE_PID, ROBOT_PID
	}

	public DriveMath(TalonSRX[] talons, NavX gyro) {
		this.talons = talons;
		this.gyro = gyro;
		SmartDashboard.putNumber("kP", 0.015);
	}

	public void init() {
		// TODO read kP, kI, kD, kF from file
		this.prevError = 0.0;
		this.integral = 0.0;
		robotpid90 = true;
	}

	public void drive(DriveType driveState, double x, double y, double rotation, double targetAngle) {
		kP = SmartDashboard.getNumber("kP", 0.015);
		double gyroAngle = gyro.getAngle();
		switch (driveState) {
		case STICK_FIELD:
			powerDrive(x, y, rotation, targetAngle);
			break;
		case ROTATE_PID:
			gyroAngle = gyro.getNormalizedAngle();		//Why here, done at top?
			targetAngle = ((targetAngle % 360) + 360) % 360;	//Normalize angle.
			double error = targetAngle - gyroAngle;
			if (Math.abs(error) > 180) { // if going around the other way is closer
				if (error > 0) { // if positive
					error = error - 360;
				} else { // if negative
					error = error + 360;
				}
			}

			if (kI != 0) {
				double potentialIGain = (integral + error) * kI;
				if (potentialIGain < maxI) {
					if (potentialIGain > -maxI) {
						integral += error;
					} else {
						integral = -maxI; // -1 / kI
					}
				} else {
					integral = maxI; // 1 / kI
				}
			} else {
				integral = 0;
			}

			if (Math.abs(error) < 3.0) {		// 3 deg Deadband?
				error = 0;
			}

			double result = (kP * error) + (kI * integral) + (kD * (error - prevError));  //PID
			if (result > 0) {
				result += kF;		// Apply Feed Fwd, with the sign of PID
			} else {
				result -= kF;
			}
			prevError = error;		// save for D

			if (result > 1) {		// -1.0 to 1.0 limits
				result = 1;
			} else if (result < -1) {
				result = -1;
			}

			powerDrive(x, y, result+rotation, targetAngle);
			break;

		case ROBOT_PID:
			gyroAngle = gyro.getNormalizedAngle() - TurnTo.align(gyro.getNormalizedAngle(),robotpid90);
			targetAngle = ((targetAngle % 360) + 360) % 360;
			gyroAngle = ((gyroAngle % 360) + 360) % 360;
			double errorRobot = targetAngle - gyroAngle;
			if (Math.abs(errorRobot) > 180) { // if going around the other way is closer
				if (errorRobot > 0) { // if positive
					errorRobot = errorRobot - 360;
				} else { // if negative
					errorRobot = errorRobot + 360;
				}
			}

			if (kI != 0) {
				double potentialIGainRobot = (integral + errorRobot) * kI;
				if (potentialIGainRobot < maxI) {
					if (potentialIGainRobot > -maxI) {
						integral += errorRobot;
					} else {
						integral = -maxI; // -1 / kI
					}
				} else {
					integral = maxI; // 1 / kI
				}
			} else {
				integral = 0;
			}

			if (Math.abs(errorRobot) < 3.0) {
				errorRobot = 0;

			}

			double resultRobot = (kP * errorRobot) + (kI * integral) + (kD * (errorRobot - prevError));
			if (resultRobot > 0) {
				resultRobot += kF;
			} else {
				resultRobot -= kF;
			}
			prevError = errorRobot;

			if (resultRobot > 1) {
				resultRobot = 1;
			} else if (resultRobot < -1) {
				resultRobot = -1;
			}

			powerDrive(x, y, resultRobot, targetAngle);

			break;
		}

	}

	public void setPIDValues(double kP, double kI, double kD, double kF, double maxI) {
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
		this.kF = kF;
		this.maxI = maxI;
	}
	public void setRobotPID(double kPR, double kIR, double kDR, double kFR, double maxIR){
		kPRobot = kPR;
		kIRobot = kIR;
		kDRobot = kDR;
		kFRobot = kFR;
		maxIRobot = maxIR;
	}

	public double[] rotateVector(double x, double y, double angle) {
		double cosA = Math.cos(angle * (Math.PI / 180.0));	//cos(angleRadians)?
		double sinA = Math.sin(angle * (Math.PI / 180.0));	//sin(angleRadians)?
		double[] out = new double[2];
		out[0] = x * cosA - y * sinA;
		out[1] = x * sinA + y * cosA;
		return out;
	}

	public void powerDrive(double x, double y, double rotation, double angle) {
		double[] rotated = rotateVector(x, y, angle);
		x = rotated[0];
		y = rotated[1];
		lfTotal = x + y + rotation;
		rfTotal = -x + y - rotation;
		lbTotal = -x + y + rotation;
		rbTotal = x + y - rotation;
		talons[0].set(ControlMode.PercentOutput, lfTotal);
		talons[2].set(ControlMode.PercentOutput, lbTotal);
		talons[4].set(ControlMode.PercentOutput, rfTotal);
		talons[6].set(ControlMode.PercentOutput, rbTotal);
		talons[1].set(ControlMode.Follower, talons[0].getDeviceID());
		talons[3].set(ControlMode.Follower, talons[2].getDeviceID());
		talons[5].set(ControlMode.Follower, talons[4].getDeviceID());
		talons[7].set(ControlMode.Follower, talons[6].getDeviceID());
	}

	public void setRobotPID90(boolean rbtpid90){
		robotpid90 = rbtpid90;
	}

	private void calcPID(){

	}

	private void calcP(){

	}

	private void calcI(){

	}

	private void calcD(){

	}
}