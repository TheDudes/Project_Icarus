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
 */

package parser;

/**
 * this class stores all the informations a variable kann have
 * and should be aware of.
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */
public class
Variable
{
        /* a lot unused variables here, but who knows what crazy ideas we will have in the future,
         thats why they are here :) */
        private String     var_type;
        private String     context_type;
        private String     context;
        private int        id;
        private String     type;
        private String     name;
        private Object     value;
        private Object     default_value;
        private TYPES      types;
        private String     device;
        private String     pin;
        private byte       abilities;
        private boolean    to_poll;
        private MappedByte mbyte;
        private boolean    has_mapping = false;

        /**
         * with value set
         * <p>
         * @param types the TYPES object to initialize the variables with there values
         * @param context the context the variable is in as string
         * @param id ongoing variable id as int
         * @param type vaiable type as string
         * @param name variable name as string
         * @param str_value variable value as string
         * @param context_type variable context type as string like FUNCTION
         * @param var_type variable type like VAR_INPUT
         */
        public
        Variable(TYPES types, String context, int id, String type, String name, String str_value, String context_type, String var_type)
        {
                this.context = context;
                this.id      = id;
                this.type    = type;
                this.name    = name;
                this.types   = types;
                try { this.value   = types.get_type(type, str_value); }
                catch (Exception e) { System.exit(1); }
                this.context_type = context_type;
                this.var_type = var_type;
                this.default_value = this.value;
        }

        /**
         * without value set
         * <p>
         * @param types the TYPES object to initialize the variables with there values
         * @param context the context the variable is in as string
         * @param id ongoing variable id as int
         * @param type vaiable type as string
         * @param name variable name as string
         * @param context_type variable context type as string like FUNCTION
         * @param var_type variable type like VAR_INPUT
         */
        public
        Variable(TYPES types, String context, int id, String type, String name, String context_type, String var_type)
        {
                this.context = context;
                this.id      = id;
                this.type    = type;
                this.name    = name;
                this.types   = types;
                try { this.value   = types.get_type(type); }
                catch (Exception e) { System.exit(1); }
                this.context_type = context_type;
                this.var_type = var_type;
                this.default_value = this.value;
        }

        /**
         * get_value returns the value of this variable as string
         * @return value as string
         */
        public String
        get_value()
        {
                return value.toString();
        }

        /**
         * get_original_value returns the value which this variable had at first initialization
         * @return original value of this variable
         */
        public String
        get_original_value()
        {
                return default_value.toString();
        }

        /**
         * set_value can set the value of this variable
         * @param str_value new value as string
         */
        public void
        set_value(String str_value)
        {
                this.value = types.get_type(type, str_value);
        }

        /**
         * set_device_pin_abilities can set the device information for this variable
         * @param device byte address as String
         * @param pin pin number as byte
         * @param abilities as byte
         * @deprecated
         */
        public void
        set_device_pin_abilities(String device, byte pin, byte abilities)
        {
                this.device    = device;
                //this.pin       = pin;
                this.abilities = abilities;
        }

        /**
         * set_mapped_byte can set the MappedByte for this variable, importend for device communication
         * @param mbyte MappedByte with the device informations
         */
        public void
        set_mapped_byte(MappedByte mbyte)
        {
                this.mbyte = mbyte;
                this.has_mapping = true;
        }

        /**
         * set_mapped_byte can set the MappedByte for this variable, importend for device communication
         * @param mbyte MappedByte with the device informations
         * @param pin String for the pin
         */
        public void
        set_mapped_byte(MappedByte mbyte, String pin)
        {
                this.mbyte = mbyte;
                this.pin = pin;
                this.has_mapping = true;
        }

        /**
         * get_IOPackage will return a IO_Pacakage for this variable
         * @return IO_Package for the IO guys
         */
        public IO_Package
        get_IOPackage()
        {
                return new IO_Package(mbyte.get_address(), pin, mbyte.get_byte(), mbyte.get_abilities(), false);
        }

        /**
         * get_var_type will return the type of this variable like SINT
         * @return the variable type
         */
        public String
        get_var_type()
        {
                return var_type;
        }

        /**
         * get_id will return the variable id
         * @return the variable id
         */
        public int
        get_id()
        {
                return id;
        }

        /**
         * set_default_value will reset the variable to its initial value
         */
        public void
        set_default_value()
        {
                value = default_value;
        }

        /**
         * has_mapping returns true if it has a device mapping
         * @return true for is mapped and false for is not mapped
         */
        public boolean
        has_mapping()
        {
                return has_mapping;
        }
}
