/**
 * Copyright (c) 2014, HAW-Landshut
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * file: /src/interpreter/Keyword_Handler.java
 * vim: foldmethod=syntax:
 */

package interpreter;

import logger.*;
import parser.*;

import Icarus.Main;

import config.*;

import java.util.Stack;
import java.util.concurrent.*;

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * class which holds all the keyword related Functions, these are used
 * to handle found Keywords, for example push and pop stacks.
 * <p>
 * @version 0.85
 */
public class Keyword_Handler
{
    final private String                log_key = " [Keyword_Handler]: ";
    final private ParserContainer       container;
    final private Engine                engine;
    final private Logger                log;
    final private Interpreter           interpreter;
    final private Offset_Handler        offset;

    final private Stack<Container_LOOP> loop_stack;
    final private Stack<String>         context_stack;
    final private Stack<Boolean>        if_stack;
    final private Stack<Integer>        if_position_stack;

          private long                  start_time;
    final private long                  clock_time;
    final private long                  status_time;
          private long                  count_time;
          private long                  count_fail;
          private long                  count_good;
          private long                  count_both;
    final private boolean               show_PRINT;
    final private boolean               show_time;

    /**
     * @param container used ParserContainer object
     * @param log used LogWriter object
     * @param config used Config_Reader
     * @param interpreter used Interpreter, need for recursive calls (CASE, ...)
     */
    public Keyword_Handler(ParserContainer container, Logger log, Config_Reader config, Interpreter interpreter)
    {
        log.log(2, log_key, "init Keyword_Handler...\n");

        this.container   = container;
        this.log         = log;
        this.interpreter = interpreter;

        clock_time  = set_clock_time(config.get_string("new_takt_frequenzy"));
        status_time = evaluate_time(config.get_string("status_time"));
        show_PRINT  = config.get_boolean("show_PRINT");
        show_time   = config.get_boolean("show_time");

        engine = new Engine(log, config);
        offset = new Offset_Handler(log);

        log.log(2, log_key, "init stacks...\n");
        context_stack     = new Stack<>();
        loop_stack        = new Stack<>();
        if_stack          = new Stack<>();
        if_position_stack = new Stack<>();
        log.log(2, log_key, "init stacks done.\n");

        log.log(2, log_key, "init Keyword_Handler done.\n");
    }

    /**
     * this functions returns the calculated status_time from the given string
     * which parsed by the config reader
     * @param config_value string from config file
     * @return calculated status_time in ms
     */
    private long evaluate_time(String config_value)
    {
        String value = "0";
        String identifier = "";
        long time = 0;

        /* loop to split string into value and identifier */
        for (int i = 0; i < config_value.length(); i++)
        {
            if(    (config_value.charAt(i) >= '0')
                && (config_value.charAt(i) <= '9') )
            {
                value += config_value.charAt(i);
                continue;
            }
            identifier = config_value.substring(i, config_value.length());
            time       = (long)Long.parseLong(value);
            break;
        }

        if( identifier.length() == 0)
        {
            time *= 1000;
            log.log(3, log_key, "calculated time: ", new Long(time).toString(), "ms\n");
        }
        else if(identifier.equals("ms") || identifier.equals("MS") || identifier.equals("milliseconds") )
        {
            log.log(3, log_key, "calculated time: ", new Long(time).toString(), "ms\n");
        }
        else if(identifier.equals("S")  || identifier.equals("s")  || identifier.equals("seconds") )
        {
            time *= 1000;
            log.log(3, log_key, "calculated time: ", new Long(time).toString(), "ms\n");
        }
        else if(identifier.equals("M")  || identifier.equals("m")  || identifier.equals("minutes") )
        {
            time *= 1000 * 60;
            log.log(3, log_key, "calculated time: ", new Long(time).toString(), "ms\n");
        }
        else if(identifier.equals("H")  || identifier.equals("h")  || identifier.equals("hours") )
        {
            time *= 1000 * 60 * 60;
            log.log(3, log_key, "calculated time: ", new Long(time).toString(), "ms\n");
        }
        else
        {
            log.log(0, log_key,
                "\n\n" +
                "ERROR: value from config file is not valid.\n" +
                "DETAILED ERROR:\n" +
                "   value 'status_time' is not valid!\n" +
                "   'status_time' is set to: '" + config_value + "'\n" +
                "   should be be set to something like:\n" +
                "       -50s  or 50seconds  for 50  seconds\n" +
                "       -15m  or 15minutes  for 15  minutes\n" +
                "       -3h   or 3hours     for 3   hours\n" +
                "       -0 if no status messages should be printed\n" +
                "   as expected the size can vary.\n" +
                "note: if no identifier (s/m/h/...) is specified, value is read in seconds.\n\n"
            );
            Main.exit();
        }
        return time;
    }

    /**
     * this functions returns the calculated clock_time from the given string
     * which parsed by the config reader
     * @param config_value string from config file
     * @return calculated clock_time in ms
     */
    private long set_clock_time(String config_value)
    {
        String value = "0";
        String identifier = "";
        long time = 0;

        /* loop to split string into value and identifier */
        for (int i = 0; i < config_value.length(); i++)
        {
            if(    (config_value.charAt(i) >= '0')
                && (config_value.charAt(i) <= '9') )
            {
                value += config_value.charAt(i);
                continue;
            }
            identifier = config_value.substring(i, config_value.length());
            time       = (long)Long.parseLong(value);
            break;
        }

        if(identifier.equals("ms") || (identifier.length() == 0))
        {
            log.log(3, log_key, "calculated clock time: ", new Long(time).toString(), "ms\n");
        }
        else if(identifier.equals("Hz")  || identifier.equals("hz") )
        {
            time = (long)((1000 / time) + 0.5);
            log.log(3, log_key, "calculated clock time: ", new Long(time).toString(), "ms\n");
        }
        else
        {
            log.log(0, log_key,
                "\n\n" +
                "ERROR: value from config file is not valid.\n" +
                "DETAILED ERROR:\n" +
                "   value 'takt_frequenzy' is not valid!\n" +
                "   'takt_frequenzy' is set to: '" + config_value + "'\n" +
                "   should be be set to something like:\n" +
                "       -100ms          for 100 milliseconds\n"     +
                "       -50hz  or 50Hz  for 50 Hertz\n" +
                "       -0 if interpreter should evaluate without frequenzy.\n" +
                "   as expected the size can vary.\n" +
                "note: if no identifier (ms/hz/Hz) is specified, value is read as ms.\n\n"
            );
            Main.exit();
        }
        return time;
    }

    /**
     * function to Handle found PROGRAM
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_PROGRAM(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_PROGRAM, INDEX = ", new Integer(INDEX).toString(), "\n");

        start_time = System.currentTimeMillis();

        int var = offset.get_VAR(INDEX, code);
        context_stack.push(code.substring(INDEX + 7, var));
        INDEX = container.get_end_var(var);

        log.log(4, log_key, "return found_PROGRAM, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found IF
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_IF(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_IF, INDEX = ", new Integer(INDEX).toString(), "\n");

        int    then_position = offset.get_THEN(INDEX, code);
        String condition     = code.substring(INDEX + 2, then_position);

        if_position_stack.push(new Integer(INDEX));
        if_stack.push((Boolean)engine.eval(container.replace_vars(condition, context_stack.peek()), true));

        if (if_stack.peek().booleanValue())
            INDEX = then_position + 3;
        else
            INDEX = offset.get_next_keyword(INDEX + 2, code) - 1;

        log.log(4, log_key, "return found_IF, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found END_IF
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_END_IF(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_END_IF, INDEX = ", new Integer(INDEX).toString(), "\n");

        if_stack.pop();
        if_position_stack.pop();

        INDEX += 5;

        log.log(4, log_key, "return found_END_IF, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found ELSE
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_ELSE(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_ELSE, INDEX = ", new Integer(INDEX).toString(), "\n");

        if(if_stack.peek().booleanValue())
            INDEX = container.get_end_if(if_position_stack.peek().intValue()) - 1;
        else
            INDEX += 3;

        log.log(4, log_key, "return found_ELSE, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found ELSIF
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_ELSIF(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_ELSIF, INDEX = ", new Integer(INDEX).toString(), "\n");

        if(if_stack.peek().booleanValue())
        {
            INDEX = container.get_end_if(if_position_stack.pop().intValue()) + 5;
        }
        else
        {
            int    then_position = offset.get_THEN(INDEX + 5, code);
            String condition     = code.substring(INDEX + 7, then_position);
            if_stack.pop();
            if_stack.push((Boolean)engine.eval(container.replace_vars(condition, context_stack.peek()), true));
            if (if_stack.peek().booleanValue())
            {
                INDEX = then_position + 3;
            }
            else
            {
                INDEX = offset.get_next_keyword(INDEX, code) - 1;
            }
        }

        log.log(4, log_key, "return found_ELSIF, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found WHILE
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_WHILE(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_WHILE, INDEX = ", new Integer(INDEX).toString(), "\n");

        Container_LOOP obj = new Container_LOOP();
        obj.INDEX          = INDEX;
        obj.do_index       = offset.get_DO(INDEX, code);
        obj.end_index      = container.get_end_while(INDEX) + 8;
        obj.condition      = engine.convert_condition(code.substring(INDEX + 5, obj.do_index));

        loop_stack.push(obj);

        if(((Boolean)engine.eval(container.replace_vars(obj.condition, context_stack.peek()), true)).booleanValue())
            INDEX = obj.do_index + 1;
        else
            INDEX = loop_stack.pop().end_index;

        log.log(4, log_key, "return found_WHILE, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found END_WHILE
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     * @throws Exception --fixme--
     */
    public int found_END_WHILE(int INDEX, String code) throws Exception
    {
        log.log(4, log_key, "call   found_END_WHILE, INDEX = ", new Integer(INDEX).toString(), "\n");

        if(((Boolean)engine.eval(container.replace_vars(loop_stack.peek().condition, context_stack.peek()), false)).booleanValue())
            INDEX = loop_stack.peek().do_index + 1;
        else
            INDEX = loop_stack.pop().end_index;

        log.log(4, log_key, "return found_END_WHILE, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found FOR
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     * @throws Exception --fixme--
     */
    public int found_FOR(int INDEX, String code) throws Exception
    {
        log.log(4, log_key, "call   found_FOR, INDEX = ", new Integer(INDEX).toString(), "\n");

        Container_LOOP obj = new Container_LOOP();
        obj.INDEX          = INDEX;
        obj.do_index       = offset.get_DO(INDEX, code);
        obj.end_index      = container.get_end_for(INDEX) + 6;

        int to_position    = offset.get_TO(INDEX, code);
        int by_position    = offset.get_BY(to_position + 2, code);
        String condition   = code.substring(INDEX + 3, to_position);
        int colon;

        if (condition.contains(":="))
        {
            colon          = offset.get_colon(condition);
            obj.name_given = true;
            obj.name       = condition.substring(0, colon);
            obj.count      = Integer.parseInt(condition.substring(colon + 2, condition.length()));
            container.add_var(condition + ";", context_stack.peek());
        }
        else
        {
            int count = Integer.parseInt(container.replace_vars(condition, context_stack.peek()));
            obj.count = count;
            if (condition.equals(Integer.toString(count)))
            {
                obj.name_given = false;
            }
            else
            {
                obj.name_given = true;
                obj.name       = condition;
            }
        }

        if (by_position == -1)
        {
            obj.limit   = Integer.parseInt(container.replace_vars(code.substring(to_position + 2, obj.do_index), context_stack.peek()));
            obj.by      = 1;
        }
        else
        {
            obj.limit   = Integer.parseInt(container.replace_vars(code.substring(to_position + 2, by_position), context_stack.peek()));
            obj.by      = Integer.parseInt(container.replace_vars(code.substring(by_position + 2, obj.do_index), context_stack.peek()));
        }
        loop_stack.push(obj);
        if (loop_stack.peek().count >= loop_stack.peek().limit)
            INDEX = loop_stack.pop().end_index;
        else
            INDEX = loop_stack.peek().do_index + 1;

        log.log(4, log_key, "return found_FOR, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found END_FOR
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     * @throws Exception --fixme--
     */
    public int found_END_FOR(int INDEX, String code) throws Exception
    {
        log.log(4, log_key, "call   found_END_FOR, INDEX = ", new Integer(INDEX).toString(), "\n");

        loop_stack.peek().count += loop_stack.peek().by;

        if (loop_stack.peek().name_given)
            container.set_value(loop_stack.peek().name + ":=" + loop_stack.peek().count + ";", context_stack.peek());

        if (loop_stack.peek().count >= loop_stack.peek().limit)
            INDEX = loop_stack.pop().end_index;
        else
            INDEX = loop_stack.peek().do_index + 1;

        log.log(4, log_key, "return found_END_FOR, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found REPEAT
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_REPEAT(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_REPEAT, INDEX = ", new Integer(INDEX).toString(), "\n");

        Container_LOOP obj = new Container_LOOP();
        obj.INDEX          = INDEX;
        obj.do_index       = INDEX + 5;
        obj.end_index      = container.get_end_repeat(INDEX) + 9;
        loop_stack.push(obj);
        INDEX += 5;

        log.log(4, log_key, "return found_REPEAT, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found UNTIL
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_UNTIL(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_UNTIL, INDEX = ", new Integer(INDEX).toString(), "\n");

        String condition = code.substring(INDEX + 6, loop_stack.peek().end_index - 9);

        if(((Boolean)engine.eval(container.replace_vars(condition, context_stack.peek()), true)).booleanValue())
            INDEX = loop_stack.peek().do_index;
        else
            INDEX = loop_stack.pop().end_index;

        log.log(4, log_key, "return found_UNTIL, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found BREAK
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_BREAK(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_BREAK, INDEX = ", new Integer(INDEX).toString(), "\n");

        INDEX = loop_stack.pop().end_index;

        log.log(4, log_key, "return found_BREAK, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found PRINT
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_PRINT(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_PRINT, INDEX = ", new Integer(INDEX).toString(), "\n");

        String final_string = "";
        String eval = null;
        boolean flag = false;

        /* for loop to concat and evaluate the PRINT string */
        for(int i = INDEX + 6; i < code.length(); i++)
        {
            if(    (code.charAt(i)     == ')')
                && (code.charAt(i + 1) == ';') )
            {
                INDEX = i + 1;
                if(!flag)
                {
                    if(eval != null)
                    {
                        final_string += engine.eval(
                                            container.replace_vars(
                                            eval, context_stack.peek() ), true);
                        eval = null;
                    }
                }
                break;
            }

            if(code.charAt(i) == '"')
            {
                if(flag)
                {
                    flag = false;
                }
                else
                {
                    if(eval != null)
                    {
                        final_string += engine.eval(
                                            container.replace_vars(
                                            eval, context_stack.peek() ), true);
                        eval = null;
                    }
                    flag = true;
                }
                continue;
            }

            if(    (code.charAt(i)     == '\\')
                && (code.charAt(i + 1) == '"') )
            {
                i += 1;
                continue;
            }

            if(flag)
            {
                final_string += code.charAt(i);
            }
            else
            {
                if(eval == null)
                    eval = "" + code.charAt(i);
                else
                    eval += code.charAt(i);
            }
        }

        if(show_PRINT)
            System.out.println(final_string + "\n");

        log.log(1, " [PRINT]: ", final_string, "\n");

        log.log(4, log_key, "return found_PRINT, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found VAR
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_VAR(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_VAR, INDEX = ", new Integer(INDEX).toString(), "\n");

        INDEX = container.get_end_var(INDEX);

        log.log(4, log_key, "return found_VAR, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found END_PROGRAM
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_END_PROGRAM(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_END_PROGRAM, INDEX = ", new Integer(INDEX).toString(), "\n");

        log.log(4, " [interpreter-end]: ", "end of Program Reached, Breaking for Loop, poping context_stack\n");
        context_stack.pop();
        INDEX += 10;

        long time_it_took = System.currentTimeMillis() - start_time;

        if(show_time)
        {
            log.log(0, log_key, "time: ",
                new Long(time_it_took).toString(), "ms\n");
        }

        if(status_time != 0)
        {
            count_time += time_it_took;
            count_both++;
            if(count_time >= status_time)
            {
                String avg_time = new Double((double)count_time/(double)count_both).toString();

                if(avg_time.length() - avg_time.indexOf('.') > 2)
                    avg_time = avg_time.substring(0, avg_time.indexOf('.') + 3);

                if(clock_time != 0)
                {
                    log.log(0, log_key, new Long(count_both).toString(),
                                        " rounds done. avg time: ",
                                        avg_time,
                                        "ms, ",
                                        new Long(count_good).toString(),
                                        " rounds went in takt, ",
                                        new Long(count_fail).toString(),
                                        " rounds went not in takt.\n"
                    );
                    count_fail = 0;
                    count_good = 0;
                }
                else
                {
                    log.log(0, log_key, new Long(count_both).toString(),
                                        " rounds done. avg time: ",
                                        avg_time,
                                        "ms\n"
                    );
                }
                count_time = 0;
                count_both = 0;
            }
        }

        if(clock_time != 0)
        {
            if(time_it_took > clock_time)
            {
                count_fail++;
                log.log(2, log_key, "couldn't keep up, took me:  ",
                                        new Long(time_it_took).toString(), "ms, ",
                                    "max time: ",
                                        new Long(clock_time).toString(), "ms, ",
                                    "trying better next time!\n");
            }
            else
            {
                count_good++;
                long sleep_time = clock_time - time_it_took;
                log.log(2, log_key, "in takt! time: ",
                                        new Long(time_it_took).toString(), "ms, sleep: ",
                                        new Long(sleep_time).toString(), "ms... \n");
                try
                {
                    TimeUnit.MILLISECONDS.sleep(sleep_time);
                }
                catch(InterruptedException e)
                {
                    log.log(0, log_key,
                        "\n\n",
                        "ERROR: interrupted on taktfrequenzy sleep!\n",
                        "DETAILED ERROR:\n",
                        "   everything went good, i went faster as i should,\n",
                        "   so i went for a short nap, but suddenly i got interrupted :(\n\n"
                    );
                    Main.exit();
                }
            }
        }

        log.log(4, log_key, "return found_END_PROGRAM, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found CASE
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     * @throws Exception --fixme--
     */
    public int found_CASE(int INDEX, String code) throws Exception
    {
        log.log(4, log_key, "call   found_CASE, INDEX = ", new Integer(INDEX).toString(), "\n");

        String    condition = code.substring(INDEX + 4, offset.get_OF(INDEX, code));
        int       value     = Integer.parseInt(engine.convert_condition(container.replace_vars(condition, context_stack.peek())));
        Integer[] recursive_positions = new Integer[2];
        recursive_positions = container.get_case_coordinates(INDEX, value);

        log.log(3, log_key, "CASE-recursive call   interpret: ",
                                  recursive_positions[0].toString(), " <--> ",
                                  recursive_positions[1].toString(), "\n");
        interpreter.interpret(code, recursive_positions[0].intValue(), recursive_positions[1].intValue());
        log.log(3, log_key, "CASE-recursive return interpret: ",
                                  recursive_positions[0].toString(), " <--> ",
                                  recursive_positions[1].toString(), "\n");

        INDEX = container.get_end_case(INDEX) + 7;

        log.log(4, log_key, "return found_CASE, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found FUNCTION
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_FUNCTION(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_FUNCTION, INDEX = ", new Integer(INDEX).toString(), "\n");

        int var_pos = offset.get_VAR(INDEX, code);
        context_stack.push(code.substring(INDEX + 8, var_pos));

        INDEX = container.get_end_var(var_pos);

        log.log(4, log_key, "return found_FUNCTION, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function to Handle found END_FUNCTION
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_END_FUNCTION(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_END_FUNCTION, INDEX = ", new Integer(INDEX).toString(), "\n");

        context_stack.pop();

        log.log(4, log_key, "return found_END_FUNCTION, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function if no keyword was found. will figure out if its a function call,
     * a set_value call, or a timer.
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     * @throws Exception --fixme--
     */
    public int found_nothing(int INDEX, String code) throws Exception
    {
        log.log(4, log_key, "call   found_nothing, INDEX = ", new Integer(INDEX).toString(), "\n");

        int    semicolon_position = offset.get_semicolon(INDEX, code);
        String condition          = code.substring(INDEX, semicolon_position + 1);
        String identifier_name    = "";
        int    jump_index         = 0;

        int i;
        int ii;
        for(i = 0; i < condition.length(); i++)
        {
            if(condition.charAt(i) == '(')
            {
                /* just a simple function call */
                jump_index = container.call_function_or_program(identifier_name, condition.substring(i + 1, condition.length()-2));
                log.log(4, log_key, "recursive function call , INDEX = ", new Integer(INDEX).toString(), "\n");
                interpreter.interpret(code, jump_index, code.length());
                log.log(4, log_key, "return from recursive function call , INDEX = ", new Integer(INDEX).toString(), "\n");
                container.reset_function(identifier_name);
                break;
            }
            if (    (condition.charAt(i)     == ':')
                 && (condition.charAt(i + 1) == '=') )
            {
                /* check now if function call with return value, or just normal set value */
                identifier_name = "";
                for (ii = i; ii < condition.length(); ii++)
                {
                    if(condition.charAt(i) == '(')
                    {
                         /* function call with return value */
                        jump_index = container.call_function_or_program(identifier_name, condition.substring(ii + 1, condition.length()-2));
                        log.log(4, log_key, "recursive function call with return value, INDEX = ", new Integer(INDEX).toString(), "\n");
                        interpreter.interpret(code, jump_index, code.length());
                        log.log(4, log_key, "return from recursive function call with return value, INDEX = ", new Integer(INDEX).toString(), "\n");
                        /* set return value b4 reset_function */
                        container.reset_function(identifier_name);
                        break;
                    }
                    identifier_name += condition.charAt(ii);
                }
                if(ii == condition.length())
                {
                    /* just set value */
                    log.log(4, log_key, "set_value call , INDEX = ", new Integer(INDEX).toString(), "\n");
                    container.set_value(condition, context_stack.peek());
                    break;
                }
                break;
            }
            if (    (condition.charAt(i)     == '.')
                 && (condition.charAt(i + 1) == 'P')
                 && (condition.charAt(i + 2) == 'T') )
            {
                log.log(4, log_key, "found set time of timer , INDEX = ", new Integer(INDEX).toString(), "\n");
                STTimer timer = container.get_timer(context_stack.peek(), identifier_name);
                timer.set_time(evaluate_time(condition.substring(i + 7, condition.length() - 1)));
                break;
            }
            if (    (condition.charAt(i)     == '.')
                 && (condition.charAt(i + 1) == 'I')
                 && (condition.charAt(i + 2) == 'N') )
            {
                log.log(4, log_key, "found start timer, INDEX = ", new Integer(INDEX).toString(), "\n");
                STTimer timer = container.get_timer(context_stack.peek(), identifier_name);
                if(((Boolean)engine.eval(container.replace_vars(condition.substring(i + 5, condition.length() - 1),context_stack.peek() ), true )).booleanValue())
                {
                    timer.set_running();
                    log.log(4, log_key, "started timer, INDEX = ", new Integer(INDEX).toString(), "\n");
                }
                break;
            }
            identifier_name += condition.charAt(i);
        }

        if(i == condition.length())
        {
            /* not a function call nor a set value */
            log.log(0, log_key,
                "\n\n",
                "ERROR: syntax error\n",
                "DETAILED ERROR:\n",
                "   could not evaluate: '", identifier_name, "'\n\n"
            );
            Main.exit();
        }

        INDEX = semicolon_position;

        log.log(4, log_key, "return found_nothing, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }
}
