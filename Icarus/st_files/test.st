PROGRAM deineMudda

VAR
    blub : BOOL := FALSE;
    blah : INT := 3;
    test : INT := 133;
    test2 : INT := 4432;
END_VAR

FOR hihihi := 0 TO 10 BY 2 DO
 (* FOR asdf := 0 TO 5 DO
        PRINT(asdf);
    END_FOR *)
    IF blah = 3 THEN
       PRINT(hihihi);
    ELSE
       PRINT(hohoho);
    END_IF
END_FOR

PRINT(blub);
PRINT(test);
PRINT(test2);


CASE blah OF
    3:
        PRINT(worked);
    4:
        PRINT(fucked_up);
    5:
        PRINT(more_fuckedup);
    ELSE
        PRINT(pretty fucked up);
END_CASE

END_PROGRAM
