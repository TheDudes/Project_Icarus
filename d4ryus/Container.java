import java.util.HashMap;
import java.util.Map;

/**
 * Class to dynamicly add and hold values and their name..
 * @author linc
 * @author vault 
 * @author Ninti 
 */
public class Container {
    
	private Map<String, Long>	 container_long	   = new HashMap<>();
	private Map<String, Boolean> container_boolean = new HashMap<>();
	private Map<String, Double>	 container_double  = new HashMap<>();
	private Map<String, String>	 container_string  = new HashMap<>();

	/*
	 * will insert Values into the HashMap's from given structure text string.
	 * For Example:
	 * String: "var1,var2:INT:=5;"
	 * ---> container_long.put(var1, 5);
	 *		container_long.put(var2, 5);
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
			value_given = false;
		} else {
			type += var_line.substring(first_colon + 1, last_colon);
			value += var_line.substring(last_colon + 2, var_line.length() - 1);
			value_given = true;
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
		if(type.equals("INT")) {
			if(value_given) {
				if(single){
					put_long(name, Long.parseLong(value));
				} else {
					for(int i = 0 ; i < names.length ; i++) {
						put_long(names[i], Long.parseLong(value));
					}
				}
			} else {
				if(single){
					put_long(name, 0);
				} else {
					for(int i = 0 ; i < names.length ; i++) {
						put_long(names[i], 0);
					}	
				}	
			}
		} else if (type.equals("BOOL")) {	
			if(value_given) {
				if(single){
					if( (value.equals("TRUE")) || (value.equals("1")) ) {
						put_boolean(name, true);
					} else {
						put_boolean(name, false);
					}
				} else {
					if( (value.equals("TRUE")) || (value.equals("1")) ) {
						for(int i = 0 ; i < names.length ; i++) {
							put_boolean(names[i], true);
						}
					} else {
						for(int i = 0 ; i < names.length ; i++) {
							put_boolean(names[i], true);
						}
					}
				}
			} else {
				if(single){
					put_boolean(name, false);
				} else {
					for(int i = 0 ; i < names.length ; i++) {
						put_boolean(names[i], false);
					}	
				}	
			}
		} else if (type.equals("REAL")) {
			if(value_given) {
				if(single){
					put_double(name, Double.parseDouble(value));
				} else {
					for(int i = 0 ; i < names.length ; i++) {
						put_double(names[i], Double.parseDouble(value));
					}
				}
			} else {
				if(single){
					put_double(name, 0.0);
				} else {
					for(int i = 0 ; i < names.length ; i++) {
						put_double(names[i], 0.0);
					}	
				}	
			} 	
		} else if (type.equals("STRING")) {
			if(value_given) {
				if(single){
					put_string(name, value);
				} else {
					for(int i = 0 ; i < names.length ; i++) {
						put_string(names[i], value);
					}
				}
			} else {
				if(single){
					put_string(name, " ");
				} else {
					for(int i = 0 ; i < names.length ; i++) {
						put_string(names[i], " ");
					}	
				}	
			}
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

                
				if ( contains_long( tmp ) )
                {
                    final_code += get_long_value( tmp ) ;
                    cursor = cutter - 1;
                    break;
                } else if ( contains_boolean( tmp ) )
                {
                    final_code += get_boolean_value( tmp );
                    cursor = cutter - 1;
                    break;
                } else if ( contains_double( tmp ) )
                {
                    final_code += get_double_value( tmp );
                    cursor = cutter - 1;
                    break;
                } else if ( contains_string( tmp ) )
                {
                    final_code += get_string_value( tmp );
                    cursor = cutter - 1;
                    break;
                }
            }
            /* 
             * check if inner for loop found a match, if so the cutter value would
             * be 0. and if not, the cutter would be at code.length().
			 * and if no match is found, add char to string and continue outter loop.
             */
            if (cutter == code.length()) {
                final_code += code.charAt(cursor);
            }
        }
    return final_code;    
    }

    public void put_long(String key, long value) {
        container_long.put(key, value);
    }

    public void put_boolean(String key, boolean value) {
        container_boolean.put(key, value);
    }

	public void put_double(String key, double value) {
        container_double.put(key, value);
    }

	public void put_string(String key, String value) {
        container_string.put(key, value);
    }
	
	public void set_long(String key, long value) {
		container_long.put(key, value);
	}
	
	public void set_boolean(String key, boolean value) {
		container_boolean.put(key, value);
	}

	public void set_double(String key, double value) {
		container_double.put(key, value);
	}
	
	public void set_string(String key, String value) {
		container_string.put(key, value);
	}

	public boolean contains_long(String key) {
		return container_long.containsKey(key);
	}
	public boolean contains_boolean(String key) {
		return container_boolean.containsKey(key);
	}

	public boolean contains_double(String key) {
		return container_double.containsKey(key);
	}

	public boolean contains_string(String key) {
		return container_string.containsKey(key);
	}

    public long	get_long_value(String key) {
        return container_long.get(key).longValue();
    }

    public boolean get_boolean_value(String key) {
        return container_boolean.get(key).booleanValue();
    }

    public double get_double_value(String key) {
        return container_double.get(key).doubleValue();
    }

    public String get_string_value(String key) {
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
