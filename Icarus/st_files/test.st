PROGRAM Test        
        (* 
        this is our test ST program
        we will extend this pice of
        code to show all what we can
        do
        *)
        VAR
                timer      : TON;
                blub       : BOOL : = FALSE;
                bluber     : BOOL : = TRUE;
                ratatazonk : BOOL : = TRUE;
                blah       : SINT : = 3;
                test       : INT  : = 133;
                test2      : INT  : = 4432;
        END_VAR
        
        FOR hihihi := 0 TO 30 BY 1 DO
                FOR asdf:=0 TO 100 BY 1 DO
                        PRINT("inner_loop="asdf);
                END_FOR
                IF blah = 3
                        THEN
                        PRINT("outter_loop_if_true="hihihi);
                        ELSE
                        PRINT("outter_loop_else="asdf);
                END_IF
        END_FOR

        (* this is a comment *)

        timer.PT:=T#2S;

        IF timer THEN
                PRINT("timer went through");
                timer.IN:=TRUE;
                ELSE
                PRINT("timer is running, or dead");
        END_IF

        PRINT("between="blub);
        PRINT("between="test);
        PRINT("between="test2);

        blah := 4;

        PRINT("blub: " blub ", blah: " blah);
        
        CASE blah OF
                3:
                PRINT("case_3="blah);
                4:
                PRINT("case_4="blah);
                5:
                PRINT("case_5="blah);
                ELSE
                PRINT("case_else="blah);
        END_CASE

        amazing();
        amazing(a:=TRUE, b:=TRUE, c:=TRUE, d:=TRUE, e:=TRUE);
        amazing();

END_PROGRAM
