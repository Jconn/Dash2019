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

import org.usfirst.frc4680.Dash2019.TalonPIDSubsystem;
import org.usfirst.frc4680.Dash2019.commands.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;


public class Arm extends TalonPIDSubsystem {

    public static final double Kp = 0.02;
    public static final double Ki = 0.0;
    public static final double Kd = 0.0;
    public static final double Kf = 0.2;

    //Zero is with the arm straight horizontal
    public static final double MINIMUM_ANGLE = -30.0;
    public static final double MAXIMUM_ANGLE = 110.0;
    public static final double ANGLE_TOLERANCE = 2.0;
    public static final double STARTINGANGLE = 90.0;
    private static final double degreesPerEncoderCount = (360.0 / 4096) * (12.0/60); 

    private WPI_TalonSRX pivotMotorB;

    

    public Arm() {
       
        m_talon = new PIDSourceTalon(4);
        m_talon.setName("PivotTalonA");
        m_talon.setNeutralMode(NeutralMode.Brake);
 
        // NO ENCODER CONNECTED
        pivotMotorB = new WPI_TalonSRX(5);
        pivotMotorB.follow(m_talon); 
        pivotMotorB.setNeutralMode(NeutralMode.Brake);
        m_talon.setName("PivotTalonB");
               
        
        m_controller = new VerticalArmPIDController(Kp, Ki, Kd, Kf, m_talon, m_talon);
        m_controller.setOutputRange(-0.6, 0.6);
        m_controller.setAbsoluteTolerance(ANGLE_TOLERANCE);
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new ManualArmControl());
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Arm Angle", getAngle());
        SmartDashboard.putBoolean("Arm PID On", m_controller.isEnabled());
        SmartDashboard.putNumber("Arm error", m_controller.getError());
        SmartDashboard.putNumber("Arm Setpoint", m_controller.getSetpoint());
        SmartDashboard.putNumber("motor out", m_talon.get());
    }

    public void setAngle(double angle) {
        angle = Math.max(angle, MINIMUM_ANGLE);
        angle = Math.min(angle, MAXIMUM_ANGLE);
        double counts = (STARTINGANGLE - angle) / degreesPerEncoderCount;
        m_controller.setSetpoint(counts);
    }

    public double getAngle() {
        int quadraturePosition = m_talon.getSensorCollection().getQuadraturePosition();
        return STARTINGANGLE - (quadraturePosition * degreesPerEncoderCount);
    }

    public void moveShoulderSetpoint(double speed) {
            double ang = getAngle();
            ang += (  speed / 5.0 );
            setAngle(ang);
    }


    public class VerticalArmPIDController extends TalonPIDController {
        VerticalArmPIDController(double p, double i, double d, double f, PIDSource src, PIDOutput out) {
            super(p, i, d, f, src, out);
        }

        @Override
        protected double calculateFeedForward() {
            return getF() * Math.cos(Math.toRadians(getAngle()));
        }
    }

}

