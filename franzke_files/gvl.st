VAR_GLOBAL
			
	SliderLeft			: SliderPointerFB;
	PartAtSliderL		: BOOL := FALSE;
	SliderLeftFree		: BOOL := TRUE;
	
	SliderRight			: SliderPointerFB;
	PartAtSliderR		: BOOL := FALSE;
	SliderRightFree		: BOOL := TRUE;
	
	DrillingLeft 		: DrillingMachineFB;
	PartAtDrillingL		: BOOL := FALSE;
	DrillingLeftFree	: BOOL := TRUE;
	
	DrillingRight		: DrillingMachineFB;
	PartAtDrillingR		: BOOL := FALSE;
	DrillingRightFree	: BOOL := TRUE;
	
	PartAtBeltOut		: BOOL := FALSE;
	BeltOutFree			: BOOL := TRUE;
	
	FaultQuitt			: BOOL;
	Fault				: BOOL;
	Warning				: BOOL;
	Info				: BOOL;
	
	ErrorNum			: DINT;
	ErrorButton			: BOOL;
	
END_VAR
