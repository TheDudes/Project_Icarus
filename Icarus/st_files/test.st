PROGRAM deineMudda

VAR
    blub : BOOL := FALSE;
    blah : INT := 3;
    test : INT := 133;
    testint : INT := 4432;
    (*testuling :ULINT := 1234;*)
    testulint : ULINT := 18446744073709551614;
END_VAR

FOR hihihi := 0 TO 100 BY 1 DO
    FOR asdf:=0 TO 5 BY 1 DO
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
PRINT(between=,testint);

blah := 4;

CASE blah OF
    3:
        PRINT(case_3=, blah);
    4:
        PRINT(case_4=, blah);
    (*5:
        PRINT(case_5=, blah);*)
    ELSE
        PRINT(case_else=, blah);
END_CASE

END_PROGRAM
