
package parser;

import java.util.*;


public class
CaseHandling
{
        private boolean has_else  = false;
        private int[]   else_part = new int[2];

        private String  code;
        private int     offset;
        private int     old_collon;
        private int     end_case_e;

        private HashMap<Integer,Integer[]> cases_map = new HashMap<>();
        private ArrayList<Integer> current_cases_list = new ArrayList<>();
        private ArrayList<Integer> old_cases_list = new ArrayList<>();
        private intStack case_stack = new intStack(100);

        enum states {start, jump_behind_of, find_colon, find_end_case, find_number, from_to, create_case_lookup, failed, end}

        public
        CaseHandling(String code, int offset)
        {
                this.code = code;
                this.offset = offset;

                eval();
        }

        private void
        eval()
        {
                int current_collon = 0;
                String band = "";
                String from = "";
                states state = states.start;
                for(int index = 0; index <= code.length();){
                        switch(state){
                        case start:
                                if(code.charAt(index  ) == 'C' &&
                                   code.charAt(index+1) == 'A' &&
                                   code.charAt(index+2) == 'S' &&
                                   code.charAt(index+3) == 'E')
                                {
                                        index += 4;
                                        state = states.jump_behind_of;
                                } else {
                                        // throw exception, because this is not a case ....
                                }
                                break;
                        case jump_behind_of:
                                if(code.charAt(index  ) == 'O' &&
                                   code.charAt(index+1) == 'F')
                                {
                                        index += 2;
                                        state = states.find_colon;
                                } else {
                                        index += 1;
                                }
                                break;
                        case find_colon:
                                if(code.charAt(index  ) == 'C' &&
                                   code.charAt(index+1) == 'A' &&
                                   code.charAt(index+2) == 'S' &&
                                   code.charAt(index+3) == 'E')
                                {
                                        case_stack.push(index);
                                        index += 4;
                                        state = states.find_end_case;
                                }
                                else if(code.charAt(index  ) == 'E' &&
                                        code.charAt(index+1) == 'L' &&
                                        code.charAt(index+2) == 'S' &&
                                        code.charAt(index+3) == 'E')
                                {
                                        has_else = true;
                                        else_part[0] = index+3;
                                } else if(code.charAt(index  ) == 'E' &&
                                          code.charAt(index+1) == 'N' &&
                                          code.charAt(index+2) == 'D' &&
                                          code.charAt(index+3) == '_' &&
                                          code.charAt(index+4) == 'C' &&
                                          code.charAt(index+5) == 'A' &&
                                          code.charAt(index+6) == 'S' &&
                                          code.charAt(index+7) == 'E')
                                {
                                        else_part[1] = index;
                                        state = states.end;
                                } else {
                                        if(code.charAt(index) == ':')
                                        {
                                                old_collon = current_collon;
                                                current_collon = index;
                                                index -= 1;
                                                state = states.find_number;
                                        }
                                }
                                break;
                        case find_end_case:
                                if(code.charAt(index  ) == 'E' &&
                                   code.charAt(index+1) == 'N' &&
                                   code.charAt(index+2) == 'D' &&
                                   code.charAt(index+3) == '_' &&
                                   code.charAt(index+4) == 'C' &&
                                   code.charAt(index+5) == 'A' &&
                                   code.charAt(index+6) == 'S' &&
                                   code.charAt(index+7) == 'E')
                                {
                                        index += 8;
                                        case_stack.pop();
                                        if(case_stack.empty()) {
                                                state = states.find_colon;
                                        } else {
                                                state = states.find_end_case;
                                        }
                                } else {
                                        state = states.find_end_case;
                                }
                                break;
                        case find_number:
                                if(code.charAt(index) == '0' ||
                                   code.charAt(index) == '1' ||
                                   code.charAt(index) == '2' ||
                                   code.charAt(index) == '3' ||
                                   code.charAt(index) == '4' ||
                                   code.charAt(index) == '5' ||
                                   code.charAt(index) == '6' ||
                                   code.charAt(index) == '7' ||
                                   code.charAt(index) == '8' ||
                                   code.charAt(index) == '9')
                                {
                                        band = code.charAt(index) + band;
                                        index -= 1;
                                } else if(code.charAt(index) == ',') {
                                        current_cases_list.add(new Integer(Integer.parseInt(band))); //, new Integer(current_collon+offset));
                                        band = "";
                                        index -= 1;
                                } else if(code.charAt(index) == '-') {
                                        index -= 1;
                                        state = states.from_to;
                                } else {
                                        current_cases_list.add(new Integer(Integer.parseInt(band))); //, new Integer(current_collon+offset));
                                        band = "";
                                        state = states.create_case_lookup;
                                }
                                break;
                        case from_to:
                                if(code.charAt(index) == '0' ||
                                   code.charAt(index) == '1' ||
                                   code.charAt(index) == '2' ||
                                   code.charAt(index) == '3' ||
                                   code.charAt(index) == '4' ||
                                   code.charAt(index) == '5' ||
                                   code.charAt(index) == '6' ||
                                   code.charAt(index) == '7' ||
                                   code.charAt(index) == '8' ||
                                   code.charAt(index) == '9')
                                {
                                        from = code.charAt(index) + from;
                                        index -= 1;
                                        state = states.from_to;
                                } else if(code.charAt(index) == ',') {
                                        for (int i = Integer.parseInt(from); i <= Integer.parseInt(band); i++)
                                                current_cases_list.add(new Integer(i));
                                        from = "";
                                        band = "";
                                        index -= 1;
                                        state = states.find_number;
                                } else if(code.charAt(index) == '-') {
                                        from = '-' + from;
                                        state = states.failed;
                                        // somethings fucked up
                                } else {
                                        for (int i = Integer.parseInt(from); i <= Integer.parseInt(band); i++)
                                                current_cases_list.add(new Integer(i));
                                        from = "";
                                        band = "";
                                        state = states.create_case_lookup;
                                }
                                break;
                        case create_case_lookup:
                                if (!old_cases_list.isEmpty()) {
                                        Integer end_old_case = new Integer(index+offset);
                                        Integer collon_old_case = new Integer(old_collon);
                                        for (Integer a_case : old_cases_list)
                                                cases_map.put(a_case, new Integer[]{collon_old_case, end_old_case});
                                }
                                old_cases_list = current_cases_list;
                                current_cases_list = new ArrayList<>();
                                index = current_collon + 1;
                                state = states.find_colon;
                                break;
                        case failed:
                                System.out.println("ERROR in CASE: FROM: "+from+" BAND: "+band);
                                System.exit(1);
                                break;
                        case end:
                                end_case_e = else_part[1];
                                break;
                        }
                }


        }

        public int
        get_end_case()
        {
                return end_case_e;
        }

        public Integer[]
        get_case_collon(int a_case)
        {
                Integer[] get_case = cases_map.get(new Integer(a_case));
                if (get_case == null) {
                        return new Integer[]{new Integer(else_part[0]), new Integer(else_part[1])};
                }
                return get_case;
        }

}


