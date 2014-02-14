PROGRAM DrillingManager
VAR
	First	: BOOL := TRUE;
	StepL 	: DINT := 0;
	StepR	: DINT := 0;
END_VAR

IF (First)
THEN
(*
	DrillingLeft.pMotorBelt := ADR(MotorBeltMilling);
	DrillingLeft.pMotorMachine := ADR(MotorMilling);
	DrillingLeft.pSensorMachine := ADR(SensorMilling);
	
	DrillingRight.pMotorBelt := ADR(MotorBeltDrilling);
	DrillingRight.pMotorMachine := ADR(MotorDrilling);
	DrillingRight.pSensorMachine := ADR(SensorDrilling);
*)	
	First := FALSE;
ELSE
	DrillingLeft();
	DrillingRight();
	
	CASE (StepL) OF
		0 :
			DrillingLeftFree := TRUE;
			IF (PartAtDrillingL = TRUE) AND
			   (DrillingRightFree = TRUE)
			THEN
				DrillingLeftFree := FALSE;
				DrillingLeft.Start := TRUE;
				StepL := StepL + 1;
			END_IF		
		1 : 
			IF (DrillingLeft.Busy = TRUE) AND
			   (DrillingLeft.Done = TRUE)
			THEN
				DrillingLeft.Start := FALSE;
				PartAtDrillingR := TRUE;
				StepL := StepL + 1;
			END_IF
		2 :
			//Handshake to drilling right
			IF (DrillingRightFree = FALSE)
			THEN
				PartAtDrillingR := FALSE;
				StepL := 0;
			END_IF
	END_CASE
	
	CASE (StepR) OF
		0 :
			DrillingRightFree := TRUE;
			IF (PartAtDrillingR = TRUE) AND
			   (SliderRightFree = TRUE)
			THEN
				DrillingRightFree := FALSE;
				DrillingRight.Start := TRUE;
				StepR := StepR + 1; 
			END_IF	
		1 : 
			IF (DrillingRight.Busy = TRUE) AND
			   (DrillingRight.Done = TRUE)
			THEN
				DrillingRight.Start := FALSE;
				PartAtSliderR := TRUE;
				StepR := StepR + 1;
			END_IF
		2 :
			//Handshake to slider right
			IF (SliderRightFree = FALSE)
			THEN
				PartAtSliderR := FALSE;
				StepR := 0;
			END_IF
	END_CASE

	
END_IF

END_PROGRAM