PROGRAM test

VAR
    blub : BOOL := FALSE;
    blah : INT := 3;
    test : INT := 133;
    test2 : INT := 4432;
    bluber : BOOL := FALSE;
    ratatazonk : BOOL := FALSE;
END_VAR

FOR hihihi := 0 TO 100 BY 1 DO
    FOR asdf:=0 TO 50 BY 1 DO
        IF blub THEN
            blub := FALSE;
            PRINT(--debug--, blub);
        ELSE
            blub := TRUE;
            PRINT(--debug--, blub);
        END_IF
        PRINT(inner_loop=, blub);
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
	test.blub AT X32.2;
	test.bluber AT X33.12;
	test.ratatazonk AT X34.7;
END_VAR_CONFIG
