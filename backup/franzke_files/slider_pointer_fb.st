FUNCTION_BLOCK SliderPointerFB
VAR_INPUT
	StartForward 			: BOOL := FALSE;
	StartBackward 			: BOOL := FALSE;
	pSwitchFront 	AT %I*	: BOOL;
	pSwitchBack 	AT %I*	: BOOL;
END_VAR
VAR_OUTPUT
	Busy					: BOOL := FALSE;
	Done 					: BOOL := FALSE;
	pMotorForward 	AT %Q*	: BOOL;
	pMotorBackward 	AT %Q*	: BOOL;
END_VAR
VAR
	Step 					: INT := 0;
END_VAR

CASE (Step) OF
	0 :
		IF 	(StartForward = TRUE)
		THEN
			Busy := TRUE;
			Done := FALSE;
			Step := Step + 1;
		END_IF
		
		IF 	(StartBackward = TRUE)
		THEN
			Busy := TRUE;
			Done := FALSE;
			Step := Step + 100;
		END_IF
		
	1 :
		pMotorForward := TRUE;
		
		IF 	(pSwitchFront = TRUE) AND
		 	(pSwitchBack = FALSE)
		THEN
			pMotorForward := FALSE;
			Step := 200; (* Handshake *)
		END_IF

	100 :
		pMotorBackward := TRUE;
		
		IF 	(pSwitchFront = FALSE) AND
		 	(pSwitchBack = TRUE)
		THEN
			pMotorBackward := FALSE;
			Step := 200; (* Handshake *)
		END_IF
		
	200 :
		Done := TRUE;
		
		IF	(StartForward = FALSE) AND
			(StartBackward = FALSE)
		THEN
			Busy := FALSE;
			Done := FALSE;
			Step := 0;
		END_IF

END_CASE

END_FUNCTION_BLOCK