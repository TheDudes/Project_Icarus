import java.util.HashMap;
import java.util.Map;

/*
 * Class to dynamicly add and hold values and their name..
 * @author linc
 * @author vault 
 * @author Ninti
 * @author d4ryus 
 */
public class Container {

    private Map<String, Long>    container_long    = new HashMap<>();
    private Map<String, Boolean> container_boolean = new HashMap<>();
    private Map<String, Double>  container_double  = new HashMap<>();
    private Map<String, String>  container_string  = new HashMap<>();

    /*
     * will insert Values into the HashMap's from given structure text string.
     * For Example:
     * String: "var1,var2:INT:=5;"
     * ---> container_long.put(var1, 5);
     *      container_long.put(var2, 5);
     */
    public void add(String var_line) {
        String[] names = new String[1];
        String name = "";
        String type = "";
        String value = "";
        boolean value_given = false;
        boolean single =  false;
        int first_colon = 0;
        int last_colon = 0;

        first_colon = var_line.indexOf(':');
        last_colon  = var_line.lastIndexOf(':');

        if(first_colon == last_colon) {
            type += var_line.substring(first_colon + 1, var_line.length() - 1 );
        } else {
            type += var_line.substring(first_colon + 1, last_colon);
            value += var_line.substring(last_colon + 2, var_line.length() - 1);
        }

        if(var_line.indexOf(',') > -1) {
            name += var_line.substring(0, first_colon);
            names = new String[name.split(",").length];
            names = name.split(",");
            single = false;
        } else {
            name += var_line.substring(0, first_colon);
            single = true;
        }

        if(single) {
            insert(type, name, value);
        } else {
            for(int i = 0; i < names.length; i++) {
                insert(type, names[i], value);
            }
        }
    }

    public void insert(String type, String name, String value) {

    Boolean value_given = true;

    if(value.equals("")) {
        value_given = false;
    }

    switch(type) {

    case "BOOL":
        if(value_given) { 
            container_boolean.put(name, new Boolean(value));
        } else {
            container_boolean.put(name, new Boolean(false));
        }
        break;
    case "INT":
        if(value_given) {
            container_long.put(name, new Long(value));
        } else {
            container_long.put(name, new Long(0));
        }
        break;
    case "SINT":
    case "DINT":
    case "LINT":
    case "UINT":
    case "USINT":
    case "UDINT":
    case "ULINT":
    case "REAL":
        if(value_given) {
            container_double.put(name, new Double(value));
        } else {
            container_double.put(name, new Double(0.0));
        }
        break;
    case "LREAL":
    case "TIME":
    case "DATE":
    case "TIME_OF_DAY":
    case "TOD":
    case "DATE_AND_TIME":
    case "DT":
    case "STRING":
        if(value_given) {
            container_string.put(name, new String(value));
        } else {
            container_string.put(name, new String(""));
        }
        break;
    case "WSTRING":
    case "WORD":
    case "DWORD":
    case "LWORD":
    default:
    }
    }

    /*
     * searches given string for matches inside the Container, if a match is found
     * the name will be replaces with its value. 
     * For Example:
     * Container contains variable with name 'var' and its value is '10',
     * the given string looks like: 'var > 10',
     * then the returned string will be: '10 > 10'
     */
    public String replace(String code) {

        int cursor = 0;
        int cutter = 0;
        code += " ";
        String final_code = "";
        String tmp;

        for( ; cursor < code.length() ; cursor ++) {
            cutter = cursor;
            for( ; cutter < code.length() ; cutter++) {

                tmp = code.substring(cursor, cutter);

                if ( container_long.containsKey(tmp) )
                {
                    final_code += container_long.get(tmp);
                    cursor = cutter - 1;
                    break;
                } else if ( container_boolean.containsKey(tmp) )
                {
                    final_code += get_boolean_value( tmp );
                    cursor = cutter - 1;
                    break;
                } else if ( container_double.containsKey(tmp) )
                {
                    final_code += get_double_value( tmp );
                    cursor = cutter - 1;
                    break;
                } else if ( container_string.containsKey(tmp) )
                {
                    final_code += get_string_value( tmp );
                    cursor = cutter - 1;
                    break;
                }
            }
            if (cutter == code.length()) {
                final_code += code.charAt(cursor);
            }
        }
    return final_code;
    }

    private long get_long_value(String key) {
        return container_long.get(key).longValue();
    }

    private boolean get_boolean_value(String key) {
        return container_boolean.get(key).booleanValue();
    }

    private double get_double_value(String key) {
        return container_double.get(key).doubleValue();
    }

    private String get_string_value(String key) {
        return container_string.get(key);
    }

    public boolean contains(String key) {
        if      (container_long.containsKey(key))    return true;
        else if (container_boolean.containsKey(key)) return true;
        else if (container_double.containsKey(key))  return true;
        else if (container_string.containsKey(key))  return true;
        else return false;
    }
}
