package frc.util;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

import frc.io.hdw_io.IO;
import frc.io.hdw_io.NavX_Save;

public class Pidf implements PIDOutput {
    private double setpt = 0.0;
    private double result = 0.0;

    PIDController myPID;

    // Constructor
    public Pidf( double kP, double kI, double kD, double kFF, double kTolerance,
                 double setpt, boolean isContinuous ){
        this.setpt = setpt;
        myPID = new PIDController(kP, kI, kD, kFF, IO.ahrs, this);
        myPID.setInputRange(-180.0f,  180.0f);
        myPID.setOutputRange(-1.0, 1.0);
        myPID.setAbsoluteTolerance(kTolerance);
        myPID.setContinuous(isContinuous);
    }

    @Override
    public void pidWrite(double output) {
        result = output;
    }

    public double get(){
        return result;
    }

    public void setSetpt( double sp ){ myPID.setSetpoint(sp); }
    public void setkP( double kp ){ myPID.setP(kp); }
    public void setkI( double ki ){ myPID.setI(ki); }
    public void setkD( double kd ){ myPID.setD(kd); }
    public void setkF( double kf ){ myPID.setF(kf); }
}
