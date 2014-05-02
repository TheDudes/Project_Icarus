PROGRAM SliderManager
VAR
	First	: BOOL := TRUE;
	StepL	: DINT := 0;
	StepR	: DINT := 0;
END_VAR

IF (First)
THEN
(*
	SliderLeft.pMotorBackward := ADR(MotorSlider1Back);
	SliderLeft.pMotorForward := ADR(MotorSlider1Fwd);
	SliderLeft.pSwitchBack := ADR(Slider1Rear);
	SliderLeft.pSwitchFront := ADR(Slider1Front);
	
	SliderRight.pMotorBackward := ADR(MotorSlider2Back);
	SliderRight.pMotorForward := ADR(MotorSlider2Fwd);
	SliderRight.pSwitchBack := ADR(Slider2Rear);
	SliderRight.pSwitchFront := ADR(Slider2Front);
*)	
	First := FALSE;
ELSE
	SliderLeft();
	SliderRight();
	
	CASE (StepL) OF
		0 :
			SliderLeftFree := TRUE;
			IF (PartAtSliderL = TRUE) AND
			   (DrillingLeftFree = TRUE) AND
			   (Slider1Rear = TRUE)
			THEN
				SliderLeftFree := FALSE;
				SliderLeft.StartForward := TRUE;
				StepL := StepL + 1;
			ELSIF (Slider1Rear = FALSE)
			THEN
				//Bring slider to base pos
				StepL := 100; 
			END_IF		
		1 : 
			IF (SliderLeft.Busy = TRUE) AND
			   (SliderLeft.Done = TRUE)
			THEN
				SliderLeft.StartForward := FALSE;
				PartAtDrillingL := TRUE;
				StepL := StepL + 1;
			END_IF
		2 :
			SliderLeft.StartBackward := TRUE;
			StepL := StepL + 1;
		3 :
			IF (SliderLeft.Busy = TRUE) AND
			   (SliderLeft.Done = TRUE)
			THEN
				SliderLeft.StartBackward := FALSE;
				StepL := StepL + 1;
			END_IF	
		4 :
			//Handshake to drilling left
			IF (DrillingLeftFree = FALSE)
			THEN
				PartAtDrillingL := FALSE;
				StepL := 0;
			END_IF
		100 : 
			SliderLeft.StartBackward := TRUE;
			IF (SliderLeft.Busy = TRUE) AND
			   (SliderLeft.Done = TRUE)
			THEN
				SliderLeft.StartBackward := FALSE;
				StepL := 0;
			END_IF
	END_CASE
	
	CASE (StepR) OF
		0 :
			SliderRightFree := TRUE;
			IF (PartAtSliderR = TRUE) AND
			   (BeltOutFree = TRUE) AND
			   (Slider2Rear = TRUE)
			THEN
				SliderRightFree := FALSE;
				SliderRight.StartForward := TRUE;
				StepR := StepR + 1;
			ELSIF (Slider2Rear = FALSE)
			THEN
				//Bring slider to base pos
				StepR := 100; 
			END_IF	
		1 : 
			IF (SliderRight.Busy = TRUE) AND
			   (SliderRight.Done = TRUE)
			THEN
				SliderRight.StartForward := FALSE;
				PartAtBeltOut := TRUE;
				StepR := StepR + 1;
			END_IF
		2 :
			SliderRight.StartBackward := TRUE;
			StepR := StepR + 1;
		3 :
			IF (SliderRight.Busy = TRUE) AND
			   (SliderRight.Done = TRUE)
			THEN
				SliderRight.StartBackward := FALSE;
				StepR := StepR + 1;
			END_IF	
		4 :
			//Handshake to belt out
			IF (BeltOutFree = FALSE)
			THEN
				PartAtBeltOut := FALSE;
				StepR := 0;
			END_IF
		100 : 
			SliderRight.StartBackward := TRUE;
			IF (SliderRight.Busy = TRUE) AND
			   (SliderRight.Done = TRUE)
			THEN
				SliderRight.StartBackward := FALSE;
				StepR := 0;
			END_IF
	END_CASE

	
END_IF

END_PROGRAM
