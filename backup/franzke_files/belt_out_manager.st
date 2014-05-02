PROGRAM BeltOutManager
VAR
	Step 		: DINT := 0;
	WaitTimer 	: TON; 
	First		: BOOL := TRUE;
END_VAR

IF (First = TRUE)
THEN
	WaitTimer.PT := T#2S;
	WaitTimer.IN := FALSE; //Insure that there is a rising edge later
	WaitTimer();
	First := FALSE;
ELSE
	CASE (Step) OF
		0 :
			IF (PartAtBeltOut = TRUE)
			THEN
				WaitTimer.IN := FALSE;
				WaitTimer();
				BeltOutFree := FALSE;
				MotorBeltOut := TRUE;
				Step := Step + 1;
			END_IF
			
		1 :
			WaitTimer.IN := TRUE;
			WaitTimer();
			IF (WaitTimer.Q = TRUE)
			THEN
				WaitTimer.IN := FALSE;
				WaitTimer();
				MotorBeltOut := FALSE;
				Step := Step + 1;
			END_IF
			
		2 :
			IF (SensorBeltOut = TRUE)
			THEN
				MotorBeltOut := TRUE;
				Step := Step + 1;
			END_IF
			
		3 :
			WaitTimer.IN := TRUE;
			WaitTimer();
			IF (WaitTimer.Q = TRUE)
			THEN
				MotorBeltOut := FALSE;
				BeltOutFree := TRUE;
				Step := 0;
			END_IF
			
	END_CASE
	
END_IF

END_PROGRAM