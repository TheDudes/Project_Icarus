package parser;

public class
MappedByte
{
        private String    byte_address;
        private boolean[] bits;
        private byte      dbyte;
        private char      abilities;

        public
        MappedByte(String byte_address)
        {
                this.byte_address = byte_address;
                bits = new boolean[8];
        }

        public void
        set_bit(String bit, boolean value)
        {
                int bit_num = Integer.parseInt(bit);
                bits[bit_num] = value;
                byte tmp = 0b0000_0000;

                switch(bit_num){
                case 7:
                        if (bits[0]){
                                dbyte = (byte)(dbyte & 0b1000_0000);
                        }
                        break;
                case 6:
                        if (bits[1]){
                                dbyte = (byte)(dbyte & 0b0100_0000);
                        }
                        break;
                case 5:
                        if (bits[2]){
                                dbyte = (byte)(dbyte & 0b0010_0000);
                        }
                        break;
                case 4:
                        if (bits[3]){
                                dbyte = (byte)(dbyte & 0b0001_0000);
                        }
                        break;
                case 3:
                        if (bits[4]){
                                dbyte = (byte)(dbyte & 0b0000_1000);
                        }
                        break;
                case 2:
                        if (bits[5]){
                                dbyte = (byte)(dbyte & 0b0000_0100);
                        }
                        break;
                case 1:
                        if (bits[6]){
                                dbyte = (byte)(dbyte & 0b0000_0010);
                        }
                        break;
                case 0:
                        if (bits[7]){
                                dbyte = (byte)(dbyte & 0b0000_0001);
                        }
                        break;
                }
        }

        public void
        set_byte(byte new_value)
        {
                dbyte = new_value;
        }
        
        public byte
        get_byte()
        {
                return dbyte;
        }

        public String
        get_address()
        {
                return byte_address;
        }

        public void
        set_abilities(char abilities)
        {
                this.abilities = abilities;
        }

        public char
        get_abilities()
        {
                return abilities;
        }
}
