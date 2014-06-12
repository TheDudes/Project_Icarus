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
    FOR asdf:=0 TO 100 BY 1 DO
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

amazing();
amazing(a:=TRUE, b:=TRUE, c:=TRUE, d:=TRUE, e:=TRUE);
amazing();
END_PROGRAM


FUNCTION amazing
VAR_INPUT
    a:BOOL:=FALSE;
    b:BOOL:=FALSE;
    c:BOOL:=FALSE;
    d:BOOL:=FALSE;
    e:BOOL:=FALSE;
END_VAR_INPUT
    PRINT(thisisa=, a);
    PRINT(thisisb=, b);
    PRINT(thisisc=, c);
    PRINT(thisisd=, d);
    PRINT(thisise=, e);
END_FUNCTION

VAR_CONFIG
	Test.blub AT %QX32.2 : BOOL;
	Test.blah AT %IB33 : BYTE;
	Test.ratatazonk AT %IX34.7 : BOOL;
END_VAR_CONFIG
