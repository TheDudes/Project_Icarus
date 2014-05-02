PROGRAM PLC_PRG
VAR
	WaitTimer 	: TON;
	First		: BOOL := TRUE;
END_VAR

PowerActor := TRUE;
PowerSensor := TRUE;

IF (First = TRUE)
THEN
	WaitTimer.PT := T#1S;
	WaitTimer.IN := TRUE;
	WaitTimer();
	IF (WaitTimer.Q = TRUE)
	THEN
		First := FALSE;
	END_IF
ELSE
	BeltInManager();
	SliderManager();
	DrillingManager();
	BeltOutManager();
END_IF

END_PROGRAM
