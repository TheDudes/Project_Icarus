VAR_CONFIG
        GVL.SliderLeft.pMotorBackward           AT      %QX32.2 : BOOL;
        GVL.SliderLeft.pMotorForward            AT      %QX32.3 : BOOL;
        GVL.SliderLeft.pSwitchBack              AT      %IX40.1 : BOOL;
        GVL.SliderLeft.pSwitchFront             AT      %IX40.0 : BOOL;

        GVL.SliderRight.pMotorBackward          AT      %QX32.4 : BOOL;
        GVL.SliderRight.pMotorForward           AT      %QX32.5 : BOOL;
        GVL.SliderRight.pSwitchBack             AT      %IX40.3 : BOOL;
        GVL.SliderRight.pSwitchFront            AT      %IX40.2 : BOOL;

        GVL.DrillingRight.pMotorBelt            AT      %QX33.1 : BOOL;
        GVL.DrillingRight.pMotorMachine         AT      %QX33.2 : BOOL;
        GVL.DrillingRight.pSensorMachine        AT      %IX40.7 : BOOL;

        GVL.DrillingLeft.pMotorBelt             AT      %QX32.7 : BOOL;
        GVL.DrillingLeft.pMotorMachine          AT      %QX33.0 : BOOL;
        GVL.DrillingLeft.pSensorMachine         AT      %IX40.5 : BOOL;
END_VAR
