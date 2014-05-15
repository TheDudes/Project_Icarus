package parser;


public class
Variable()
{
        private int    id;
        private String type;
        private String name;
        private Object value;
        private TYPES  types;
        private String device;
        private byte   pin;
        private byte   abilities;

        public
        Variable(id, type, name, str_value)
        {
                this.id    = id;
                this.type  = type;
                this.name  = name;
                this.types = new TYPES();
                this.value = types.get_type(type, str_value);
        }

        public
        Variable(id, type, name)
        {
                this.id    = id;
                this.type  = type;
                this.name  = name;
                this.types = new TYPES();
                this.value = types.get_type(type);
        }

        public String
        get_value()
        {
                return value.toString();
        }

        public void
        set_value(String str_value)
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
        
}
