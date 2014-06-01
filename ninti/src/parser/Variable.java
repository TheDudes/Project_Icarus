package parser;

import Ninti.*;

public class
Variable
{
        private String  context;
        private int     id;
        private String  type;
        private String  name;
        private Object  value;
        private TYPES   types;
        private String  device;
        private byte    pin;
        private byte    abilities;
        private boolean to_poll;

        public
        Variable(String context, int id, String type, String name, String str_value)
        throws UnsignedException
        {
                this.context = context;
                this.id      = id;
                this.type    = type;
                this.name    = name;
                this.types   = new TYPES();
                this.value   = types.get_type(type, str_value);
        }

        public
        Variable(String context, int id, String type, String name)
        throws UnsignedException
        {
                this.context = context;
                this.id      = id;
                this.type    = type;
                this.name    = name;
                this.types   = new TYPES();
                this.value   = types.get_type(type);
        }

        public String
        get_value()
        {
                return value.toString();
        }

        public void
        set_value(String str_value)
        throws UnsignedException
        {
                this.value = types.get_type(type, str_value);
        }

        public void
        set_device_pin_abilities(String device, byte pin, byte abilities)
        {
                this.device    = device;
                this.pin       = pin;
                this.abilities = abilities;
        }

        public IO_Package
        get_IOPackage()
        {
                return new IO_Package(device, pin, ((Boolean)value).booleanValue() ? (byte)1 : (byte)0, abilities, to_poll);
        }
        
}
