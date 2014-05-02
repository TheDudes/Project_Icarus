/**
 * convert given string so that the Java Script Engine can Interpret it,
 * will replace following matches: 
 * FROM     | TO
 * ---------+--------------
 * '='      | ' == '
 * 'TRUE'   | ' true '
 * 'FALSE'  | ' false '
 * 'XOR'    | ' ^ '
 * 'AND'    | ' && '
 * 'NOT'    | ' !'
 * '&'      | ' && '
 * '<>'     | ' != '
 * 'MOD'    | ' % '
 * 'OR'     | ' || '
 * 'SIN()'  | 'Math.sin()'
 * 'COS()'  | 'Math.cos()'
 * 'TAN()'  | 'Math.tan()'
 * 'ASIN()' | 'Math.asin()'
 * 'ACOS()' | 'Math.acos()'
 * 'ATAN()' | 'Math.atan()'
 * 'LOG()'  | 'Math.log10()'
 * 'EXP()'  | 'Math.exp()'
 * 'LN()'   | 'Math.log()'
 * 'SQRT()' | 'Math.sqrt()'
 * ---------+---------------
 *
 * @param code will be the full condition as a string
 * @return string with the converted condition
 */
public static String convert_condition(String code) {

    String final_condition = "";
    int spot = 0;

    for( ; spot < code.length(); spot++ ) {
        if ( (code.charAt(spot) == '=') )
        {
            final_condition += " == ";
            continue;
        } else if ( (code.charAt(spot)     == 'T') &&
                    (code.charAt(spot + 1) == 'R') &&
                    (code.charAt(spot + 2) == 'U') &&
                    (code.charAt(spot + 3) == 'E') )
        {
            final_condition += " true ";
            spot += 3;
            continue;
        } else if ( (code.charAt(spot)     == 'F') &&
                    (code.charAt(spot + 1) == 'A') &&
                    (code.charAt(spot + 2) == 'L') &&
                    (code.charAt(spot + 3) == 'S') &&
                    (code.charAt(spot + 4) == 'E') )
        {
            final_condition += " false ";
            spot += 4;
            continue;
        } else if ( (code.charAt(spot)     == 'X') &&
                    (code.charAt(spot + 1) == 'O') &&
                    (code.charAt(spot + 2) == 'R') )
        {
            final_condition += " ^ ";
            spot += 2;
            continue;
        } else if ( (code.charAt(spot)     == 'A') &&
                    (code.charAt(spot + 1) == 'N') &&
                    (code.charAt(spot + 2) == 'D') )
        {
            final_condition += " && ";
            spot += 2;
            continue;
        } else if ( (code.charAt(spot)     == 'N') &&
                    (code.charAt(spot + 1) == 'O') &&
                    (code.charAt(spot + 2) == 'T') )
        {
            final_condition += " !";
            spot += 2;
            continue;
        } else if ( (code.charAt(spot) == '&') )
        {
            final_condition += " && ";
            continue;
        } else if ( (code.charAt(spot)     == '<') &&
                    (code.charAt(spot + 1) == '>') )
        {
            final_condition += " != ";
            spot += 1;
            continue;
        } else if ( (code.charAt(spot)     == 'M') &&
                    (code.charAt(spot + 1) == 'O') &&
                    (code.charAt(spot + 2) == 'D') )
        {
            final_condition += " % ";
            spot += 2;
            continue;
        } else if ( (code.charAt(spot)     == 'O') &&
                    (code.charAt(spot + 1) == 'R') )
        {
            final_condition += " || ";
            spot += 1;
            continue;
        } else if ( (code.charAt(spot)     == 'S') &&
                    (code.charAt(spot + 1) == 'I') &&
                    (code.charAt(spot + 2) == 'N') )
        {
            final_condition += " Math.sin"
            spot += 2;
            continue;
        } else if ( (code.charAt(spot)     == 'T') &&
                    (code.charAt(spot + 1) == 'A') &&
                    (code.charAt(spot + 2) == 'N') )
        {
            final_condition += " Math.tan"
            spot += 2;
            continue;
        } else if ( (code.charAt(spot)     == 'A') &&
                    (code.charAt(spot + 1) == 'S') &&
                    (code.charAt(spot + 2) == 'I') &&
                    (code.charAt(spot + 3) == 'N') )
        {
            final_condition += " Math.asin"
            spot += 3;
            continue;
        } else if ( (code.charAt(spot)     == 'A') &&
                    (code.charAt(spot + 1) == 'C') &&
                    (code.charAt(spot + 2) == 'O') &&
                    (code.charAt(spot + 3) == 'S') )
        {
            final_condition += " Math.acos"
            spot += 3;
            continue;
        } else if ( (code.charAt(spot)     == 'A') &&
                    (code.charAt(spot + 1) == 'T') &&
                    (code.charAt(spot + 2) == 'A') &&
                    (code.charAt(spot + 3) == 'N') )
        {
            final_condition += " Math.atan"
            spot += 3;
            continue;
        } else if ( (code.charAt(spot)     == 'L') &&
                    (code.charAt(spot + 1) == 'O') &&
                    (code.charAt(spot + 2) == 'G') )
        {
            final_condition += " Math.log10"
            spot += 2;
            continue;
        } else if ( (code.charAt(spot)     == 'E') &&
                    (code.charAt(spot + 1) == 'X') &&
                    (code.charAt(spot + 2) == 'P') )
        {
            final_condition += " Math.exp"
            spot += 2;
            continue;
        } else if ( (code.charAt(spot)     == 'L') &&
                    (code.charAt(spot + 1) == 'N') )
        {
            final_condition += " Math.log"
            spot += 1;
            continue;
        } else if ( (code.charAt(spot)     == 'S') &&
                    (code.charAt(spot + 1) == 'Q') &&
                    (code.charAt(spot + 2) == 'R') &&
                    (code.charAt(spot + 3) == 'T') )
        {
            final_condition += " Math.sqrt"
            spot += 3;
            continue;
        } else if ( (code.charAt(spot)     == 'A') &&
                    (code.charAt(spot + 1) == 'B') &&
                    (code.charAt(spot + 2) == 'S') )
        {
            final_condition += " Math.abs";
            spot += 2;
            continue;
        }
        final_condition += code.charAt(spot);
    }
    return final_condition;
}

