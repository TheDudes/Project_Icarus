PROGRAM deineMudda

VAR
    blub : BOOL := FALSE;
    blah : INT := 3;
    test : INT := 133;
    test2 : INT := 4432;
END_VAR

FOR hihihi := 0 TO 10 BY 2 DO
    FOR asdf := 0 TO 5 DO
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
