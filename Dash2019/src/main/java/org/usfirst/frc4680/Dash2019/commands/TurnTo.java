// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc4680.Dash2019.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc4680.Dash2019.Robot;
import org.usfirst.frc4680.Dash2019.subsystems.DriveTrain;

public class TurnTo extends Command {
    /**
     *
     */

    private static final double tolerance = 1.0;
    private double targetHeading;

    public TurnTo(double angle) {
        requires(Robot.driveTrain);
        targetHeading = angle;
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
        Robot.driveTrain.directionDrive(0, targetHeading);
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        double heading = Robot.driveTrain.getHeading();
        double difference = Math.abs(DriveTrain.angleDelta(heading, targetHeading));

        return difference < tolerance;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        Robot.driveTrain.stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        Robot.driveTrain.stop();
    }
}