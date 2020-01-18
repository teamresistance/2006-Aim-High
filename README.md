# 2006-Aim-High
Poof Shooter

### History

 Date | Controller | IDE | Description
 -----|------------|-----|------------
 Jan 2010 | cRIO | ?? | Original - Lost to history
 Jan 2016 | roboRIO | Eclipse | Re-constituded by Frank
 Jan 2017 | roboRIO | Eclipse | Rework by Frank to TR86 standard
 Jan 2019 | roboRIO | VSCode | Rework by Joey & Anthony to new First standard

### Description
The object of the game was to collect 6" balls and shoot them into a high goal.

The team's solution was to have a bot that could collect a lot of balls, go to goal and unload to the high goal extremely fast using camera targeting.  It could move and shoot.

### Hardware
* Robot Hardware
    * roboRIO (present)
    * Router
    * VRM, Voltage Regulator Module
    * PDP (old), Power Distribution Panel
* Tank Drive
    * (2) Lefthand motors (2 Victor ESC’s - 1 PWM signal, y-Cable)
    * (2) Righthand motors ( 2 Victor ESC’s - 1 PWM signal, y-cable)
    * (3)Joysticks - 2 driver & 1 co-driver
* Shooter.
    * (1) motor (Victor 884 ESC)
    * (1) encoder, measures rpm (??)
    * (1) Co-driver trigger, spin On/Off
* Turret, rotate the shooter to the target using a Limelight camera
    * (1) motor (Victor ESC)
    * (1) Potentiometer
    * (1) Limelight camera
* Lifter, lifts the balls up to the shooter
    * (1) motor (Victor ESC)
    * (1) Banner Sensor?

### Sequence
* Tank Drive

* Shooter
    * Description:
The shooter spins a wheel to shoot a 7" ball at approx. 35 fps.  It starts when a button is pressed
and stop on the press of another button.  </br>
Future, Control the speed of the shooter to a setpoint thru a pid loop.  Best guess is approx.
3300 rpm.  This requires an encoder on the wheel for feedback.
Future future, change the victor to a TalonSRX and move the pid to the Talon.  </br>
  * Sequence:
(0)Default, the motor is set to 0.0, off.  When trigger is released the shooter is set to 0.0. </br>
(1)Normal control when trigger is pressed, presently fixed value (0.7), future pid loop encoder. </br>
(2)When a ball first enters the shooter the additional load causes the shooter to slow down.  The
first ball may come out hot but other balls will come out short until the pid can compensate.  One
method is to bump the speed up until back to setpoint (or presently, just some time period).

* Turret
    * Description:
        The turret rotates the turret to track a target using CV Limelight.  Can be manually controlled.  A pot is used to limit rotation from -135 to 135 with 0 being forward and setpoint control.

    * Sequence:
        *(0)Default, the motor is set to 0.0, off. </br>
        (1)JS used to manually rotate. </br>
        (2)If a POV is pressed switch to setpoint control, for testing. 0/45/90/.../315 </br>
        (3)Chgs SP to 0 then rotates forward.

* Lifter </br>
    * Description
        * The lifter lifts the balls to the shooter.

    * Sequence:
        * (0)Default, the motor is set to 0.0, off, when no buttons pressed. </br>
(1)Balls move up when up button is pressed. </br>
(2)Balls move dn when dn button is pressed.

