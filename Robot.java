/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  XboxController driver = new XboxController(0);

  
  TalonSRX driveLeft1 = new TalonSRX(1);
  VictorSPX driveLeft2 = new VictorSPX(3);

  TalonSRX driveRight1 = new TalonSRX(2);
  VictorSPX driveRight2 = new VictorSPX(4);

  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry ta = table.getEntry("ta");
  NetworkTableEntry tv = table.getEntry("tv");


  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Target Seen", (NetworkTableInstance.getDefault().getTable("limelight").getEntry("<tv>").getDouble(0)));
 
    
    //read values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    Double v = tv.getDouble(0.0);
    double area = ta.getDouble(0.0);

    //post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    Double v = tv.getDouble(0.0);

    double area = ta.getDouble(0.0);
    Double Kp = 0.005;
    Double KpDistance = 0.075;
    Double area_error = 3 - area;
    Double driving_adjust = 0.05;
    Double min_command = 0.05;

    

    if (!driver.getAButton()){
      driveLeft1.set(ControlMode.PercentOutput, driver.getY(Hand.kLeft));
      driveLeft2.set(ControlMode.PercentOutput, driver.getY(Hand.kLeft));

      driveRight1.set(ControlMode.PercentOutput, -driver.getY(Hand.kRight));
      driveRight2.set(ControlMode.PercentOutput, -driver.getY(Hand.kRight));

    }
    else  {
      Double heading_error = -x;
      Double steering_adjust = 0.075;
      if (x > 1.0)
      {
              steering_adjust = Kp*heading_error - min_command;
      }
      else if (x < 1.0)
      {
              steering_adjust = Kp*heading_error + min_command;
      }


      if (area > .25){
        driving_adjust = KpDistance * area_error + min_command;
      }
      else if (area < .25){
        driving_adjust = KpDistance * area_error - min_command;

      }

      if (v == 1){
      driveLeft1.set(ControlMode.PercentOutput, steering_adjust - driving_adjust);
      driveLeft2.set(ControlMode.PercentOutput, steering_adjust - driving_adjust);

      driveRight1.set(ControlMode.PercentOutput, steering_adjust + driving_adjust);
      driveRight2.set(ControlMode.PercentOutput, steering_adjust + driving_adjust);

      }

      else if (v == 0){
        driveLeft1.set(ControlMode.PercentOutput, 0);
        driveLeft2.set(ControlMode.PercentOutput, 0);
  
        driveRight1.set(ControlMode.PercentOutput, 0);
        driveRight2.set(ControlMode.PercentOutput, 0);
      }
    }



  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

}
