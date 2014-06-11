PROGRAM Test

VAR
    blub : BOOL := FALSE;
    bluber : BOOL := TRUE;
    ratatazonk : BOOL := TRUE;
    blah : SINT := 3;
    test : INT := 133;
    test2 : INT := 4432;
END_VAR

FOR hihihi := 0 TO 50 BY 1 DO
    FOR asdf:=0 TO 1000 BY 1 DO
        PRINT(inner_loop=, asdf);
    END_FOR 
    IF blah = 3 THEN
       PRINT(outter_loop_if_true=, hihihi);
    ELSE
       PRINT(outter_loop_else=, hohoho);
    END_IF
END_FOR

PRINT(between=,blub);
PRINT(between=, test);
PRINT(between=,test2);

blah := 4;

CASE blah OF
    3:
        PRINT(case_3=, blah);
    4:
        PRINT(case_4=, blah);
    5:
        PRINT(case_5=, blah);
    ELSE
        PRINT(case_else=, blah);
END_CASE

END_PROGRAM

VAR_CONFIG
	Test.blub AT %QX32.2 : BOOL;
	Test.blah AT %IB33 : BYTE;
	Test.ratatazonk AT %IX34.7 : BOOL;
END_VAR_CONFIG

