PROGRAM BeltInManager
VAR
	Step		: DINT := 0;
	Trigger		: F_TRIG;
	WaitTimer 	: TON;
END_VAR

Trigger.CLK := SensorLoading;
Trigger();
WaitTimer.PT := T#1S;

CASE (Step) OF
	0 :
		IF (Trigger.Q = TRUE)
		THEN
			MotorBeltIn := TRUE;
			Step := Step + 1;
		END_IF
		
	1 : 
		IF (SensorSlider1 = FALSE)
		THEN
			MotorBeltIn := FALSE;
			WaitTimer.IN := FALSE;
			WaitTimer();
			Step := Step + 1;
		END_IF
		
	2 :
		IF (SliderLeftFree = TRUE)
		THEN
			MotorBeltIn := TRUE;
			WaitTimer.IN := TRUE;
			WaitTimer();
			Step := Step + 1;
		END_IF
		
	3 :
		WaitTimer();
		IF (WaitTimer.Q = TRUE)
		THEN
			MotorBeltIn := FALSE;
			PartAtSliderL := TRUE;
			Step := Step + 1;
		END_IF
		
	4 :
		//Handshake to slider and reset of PartAt flag
		IF (SliderLeftFree = FALSE)
		THEN
			PartAtSliderL := FALSE;
			Step := 0;
		END_IF
		
END_CASE

END_PROGRAM
