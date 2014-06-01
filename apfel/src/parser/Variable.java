package parser;

import Ninti.*;

public class
Variable
{
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

        public
        Variable(String context, int id, String type, String name, String str_value, String context_type, String var_type)
        {
                this.context = context;
                this.id      = id;
                this.type    = type;
                this.name    = name;
                this.types   = new TYPES();
                try { this.value   = types.get_type(type, str_value); }
                catch (Exception e) { System.exit(1); }
                this.context_type = context_type;
                this.var_type = var_type;
                this.default_value = this.value;
        }

        public
        Variable(String context, int id, String type, String name, String context_type, String var_type)
        {
                this.context = context;
                this.id      = id;
                this.type    = type;
                this.name    = name;
                this.types   = new TYPES();
                try { this.value   = types.get_type(type); }
                catch (Exception e) { System.exit(1); }
                this.context_type = context_type;
                this.var_type = var_type;
                this.default_value = this.value;
        }

        public String
        get_value()
        {
                return value.toString();
        }

        public void
        set_value(String str_value)
        {
                try {
                        this.value = types.get_type(type, str_value);
                } catch (UnsignedException e) {
                        System.out.println("asdfasfdsadfasdfsadf");
                }
        }
 
        public void
        set_device_pin_abilities(String device, byte pin, byte abilities)
        {
                this.device    = device;
                //this.pin       = pin;
                this.abilities = abilities;
        }
        
        public void
        set_mapped_byte(MappedByte mbyte)
        {
                this.mbyte = mbyte;
        }
        
        public void
        set_mapped_byte(MappedByte mbyte, String pin)
        {
                this.mbyte = mbyte;
                this.pin = pin;
        }

        public IO_Package
        get_IOPackage()
        {
                return new IO_Package(mbyte.get_address(), pin, mbyte.get_byte(), mbyte.get_abilities(), false);
        }
}
