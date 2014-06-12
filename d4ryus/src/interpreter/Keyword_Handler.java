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
 * file: Icarus/src/interpreter/Keyword_Handler.java
 * vim: foldmethod=syntax:
 */

package interpreter;

import logger.*;
import parser.*;
import linc.*;

import java.util.Stack;

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * class which holds all the keyword related Functions, these are used
 * to handle found Keywords, for example push and pop stacks.
 * <p>
 * @version 0.8
 */
public class Keyword_Handler
{
    final private String                log_key = " [Interpreter-Keyword_Handler]: ";
    final private InfoCollector         container;
    final private Engine                engine;
    final private Logger                log;
    final private Interpreter           interpreter;
    final private Offset_Handler        offset;

    final private Stack<Container_LOOP> loop_stack;
    final private Stack<String>         context_stack;
    final private Stack<Boolean>        if_stack;
    final private Stack<Integer>        if_position_stack;

    /**
     * @param container used InfoCollector object
     * @param log used LogWriter object
     * @param config used Config_Reader
     * @param interpreter used Interpreter, need for recursive calls (CASE, ...)
     */
    public Keyword_Handler(InfoCollector container, Logger log, Config_Reader config, Interpreter interpreter)
    {
        log.log(2, log_key, "init Keyword_Handler...\n");

        this.container   = container;
        this.log         = log;
        this.interpreter = interpreter;

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
     * function to Handle found PROGRAM
     * @param INDEX current Position
     * @param code current working code
     * @return INDEX after handling Keyword
     */
    public int found_PROGRAM(int INDEX, String code)
    {
        log.log(4, log_key, "call   found_PROGRAM, INDEX = ", new Integer(INDEX).toString(), "\n");

        int var = offset.get_VAR(INDEX, code);
        context_stack.push(code.substring(INDEX + 7, var));
        INDEX   = container.get_end_var(var) + 6;

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

        int semicolon_position = offset.get_semicolon(INDEX + 6, code);
        String print_string    = code.substring(INDEX + 6, semicolon_position - 1);

        int comma_position     = offset.get_comma(print_string);
        String text            = print_string.substring(0, comma_position);
        String values          = print_string.substring(comma_position + 1,print_string.length());

        values = container.replace_vars(values, context_stack.peek());

        log.log(1, " [PRINT]: ", text, values, "\n");
        INDEX = semicolon_position;

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

        INDEX = container.get_end_var(INDEX) + 6;

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

        INDEX = container.get_end_case(INDEX);

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

        log.log(4, log_key, "return found_END_FUNCTION, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }

    /**
     * function if no keyword was found
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
        String function_name      = "";

        for(int i = 0; i < condition.length(); i++)
        {
            if(condition.charAt(i) == '(')
            {
                //int [2] = container functioncall (function name, condition.substring (index +1, condition.length() )
                //index = int [1];
            }
            else if (condition.charAt(i) == ':')
            {
                container.set_value(condition, context_stack.peek());
                break;
            }
            else
            {
                function_name += condition.charAt(i);
            }
        }


        INDEX = semicolon_position;

        log.log(4, log_key, "return found_nothing, INDEX = ", new Integer(INDEX).toString(), "\n");
        return INDEX;
    }
}
