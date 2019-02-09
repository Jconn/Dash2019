// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

package org.usfirst.frc4680.Dash2019.subsystems;

import org.usfirst.frc4680.Dash2019.commands.*;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS


/**
 *
 */
public class DriveTrain extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    private double kPgain = 0;
    private double kDgain = 0;

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private VictorSPX leftDriveVictorA;
    private WPI_TalonSRX leftDriveTalonB;
    private SpeedControllerGroup leftSpeedControllerGroup;
    private WPI_TalonSRX rightDriveTalonA;
    private VictorSPX rightDriveVictorB;
    private SpeedControllerGroup rightSpeedControllerGroup;
    private DifferentialDrive differentialDrive1;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private ShuffleboardTab pid_tab =  Shuffleboard.getTab("pid tuning");
    private NetworkTableEntry pValue = pid_tab.add("kP",0.0).getEntry();
    private NetworkTableEntry dValue = pid_tab.add("kD",0.0).getEntry();

    private ADXRS450_Gyro gyro;

    //TODO calibrate this constant
    private static final double inchesPerEncCount = (25.133 / 4096);
    
    public int countL;
    public int countR;

    
    public DriveTrain() {
        gyro = new ADXRS450_Gyro(Port.kOnboardCS1);
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        leftDriveVictorA = new VictorSPX(0);
        
        leftDriveTalonB = new WPI_TalonSRX(1);
        leftDriveTalonB.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        leftDriveTalonB.getSensorCollection().setQuadraturePosition(0, 10);
        
        
        
        leftSpeedControllerGroup = new SpeedControllerGroup( leftDriveTalonB  );
        addChild("LeftSpeedControllerGroup",leftSpeedControllerGroup);

        leftDriveVictorA.follow(leftDriveTalonB);
        
        rightDriveTalonA = new WPI_TalonSRX(2);
        rightDriveTalonA.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        rightDriveTalonA.getSensorCollection().setQuadraturePosition(0, 10);        
        
        rightDriveVictorB = new VictorSPX(3);
        
        
        
        rightSpeedControllerGroup = new SpeedControllerGroup(rightDriveTalonA );
        addChild("RightSpeedControllerGroup",rightSpeedControllerGroup);
        
        rightDriveVictorB.follow(rightDriveTalonA);
        
        differentialDrive1 = new DifferentialDrive(leftSpeedControllerGroup, rightSpeedControllerGroup);
        addChild("Differential Drive 1",differentialDrive1);
        differentialDrive1.setSafetyEnabled(true);
        differentialDrive1.setExpiration(0.1);
        differentialDrive1.setMaxOutput(1.0);

        

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
    }

    @Override
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND


        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
         setDefaultCommand(new TeleopDrive());
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop
        SmartDashboard.putNumber("Heading", this.getHeading());
        kPgain = pValue.getDouble(0);
        if(kPgain > .5)
        {
            kPgain = .5;
        }
        if(kPgain < 0)
        {
            kPgain = 0;
        }

        kDgain = dValue.getDouble(0);
        if(kDgain > .5)
        {
            kDgain = .5;
        }
        if(kDgain < 0)
        {
            kDgain = 0;
        }

        SmartDashboard.putNumber("distance (in)", getDistance());
        SmartDashboard.putNumber("Right Encoder", getRightEncoderCount());
        SmartDashboard.putNumber("Left Encoder", getLeftEncoderCount());
    }

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void drive(double speed, double turn) {

        differentialDrive1.arcadeDrive(speed, turn, true);
        
    }

    public double getHeading() {
        return gyro.getAngle();
    }

    public double getAngularRate() {
        return gyro.getRate();
    }

    public void resetPosition() {
		gyro.calibrate();
        rightDriveTalonA.getSensorCollection().setQuadraturePosition(0, 10);        
    }
    
    public void directionDrive(double speed, double targetHeading)
    {
		double heading = getHeading();
		double currentAngularRate = getAngularRate();
		double angle_error = DriveTrain.angleDelta(targetHeading, heading);
		double yawCommand = - angle_error * kPgain - (currentAngularRate) * kDgain;
		
		SmartDashboard.putNumber("angle_error", angle_error);
		SmartDashboard.putNumber("commanded heading", targetHeading);

        differentialDrive1.arcadeDrive(speed, yawCommand);
    }

	public void stop() {
		differentialDrive1.stopMotor();	
	}

    static public double angleDelta(double src, double dest) {
		double delta = (dest - src) % 360.0;
		if(Math.abs(delta) > 180) {
			delta = delta - (Math.signum(delta) * 360);
		}
		return delta;
    }

    public double getDistance(){
        countR = getRightEncoderCount();
        countL = getLeftEncoderCount();
        
        double dist = (countL + countR)/2 * inchesPerEncCount;
        return dist;
    }

    public int getRightEncoderCount() { return rightDriveTalonA.getSensorCollection().getQuadraturePosition(); }
    public int getLeftEncoderCount() { return -leftDriveTalonB.getSensorCollection().getQuadraturePosition(); }

    public double getSpeed() {
        int countsPer100ms = rightDriveTalonA.getSensorCollection().getQuadratureVelocity();
        return countsPer100ms * 10 * inchesPerEncCount; // convert to inches/second
    }
}

