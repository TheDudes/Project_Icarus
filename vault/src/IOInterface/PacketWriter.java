package IOInterface;
import java.io.*;

/* --fixme-- */
/* Javadoc: Missing comment for public declaration */
public class PacketWriter extends BufferedOutputStream{

    /**
    * standart constructor call of the parent constuctor
    */
    /* --fixme-- */
    /* Javadoc: Missing tag for parameter out */
    public PacketWriter(OutputStream out){
        super(out);
    }

    /**
    * creates a 8 field byte array that will be filled with the information from the IO_Packet and then the array will be sent
    * via the send method of the parent class
    * this Method uses the getBytesFromInt Method that extends the values of the in into the second byte - aka the first byte there has the value 2^9 and so on
    * @param packet an IO_Packet that contains all the information we want to send over. Some information will be transformed so we are able to send it
    */
    /* --fixme-- */
    /* Javadoc: Missing tag for declared exception IOException */
    public void write(IO_Packet packet) throws IOException{
        //stores the integers we transform into a two byte array(some information CAN be lost, see description of the getBytes method
        byte buffer[] = new byte[2];
        //byteArray used to prepare the stuff we write
        byte writeBuffer[] = new byte[8];
        buffer = getBytesFromInt(packet.getGeraeteId());
        writeBuffer[0] = buffer[0];
        writeBuffer[1] = buffer[1];
        buffer = getBytesFromInt(packet.getPin());
        writeBuffer[2] = buffer[0];
        writeBuffer[3] = buffer[1];
        //the next two lines transform an int to a byte which means information CAN be lost but the NamespaceId and count are only 3 bit long which means they
        //should ALWAYS fit into a byte
        writeBuffer[4] = (byte)packet.getNamespaceId();
        writeBuffer[5] = (byte)packet.getCount();
        writeBuffer[6] = packet.getRWFlag();
        writeBuffer[7] = packet.getValue();
        super.write(writeBuffer, 0, 8);
    }

    /**
    * creates a 8 field byte array that will be filled with the information from the IO_Packet and then the array will be sent
    * via the send method of the parent class
    * @param packet an IO_Packet that contains all the information we want to send over. Some information will be transformed so we are able to send it
    */
    /* --fixme-- */
    /* Javadoc: Missing tag for declared exception IOException */
    public void writeMod(IO_Packet packet) throws IOException{
        //stores the integers we transform into a two byte array(some information CAN be lost, see description of the getBytes method
        byte buffer[] = new byte[2];
        buffer = getBytes(packet.getPin());
        //byteArray used to prepare the stuff we write
        byte writeBuffer[] = new byte[8];
        buffer = getBytes(packet.getGeraeteId());
        writeBuffer[0] = buffer[0];
        writeBuffer[1] = buffer[1];
        writeBuffer[2] = buffer[0];
        writeBuffer[3] = buffer[1];
        //the next two lines transform an int to a byte which means information CAN be lost but the NamespaceId and count are only 3 bit long which means they
        //should ALWAYS fit into a byte
        writeBuffer[4] = (byte)packet.getNamespaceId();
        writeBuffer[5] = (byte)packet.getCount();
        writeBuffer[6] = packet.getRWFlag();
        writeBuffer[7] = packet.getValue();
        super.write(writeBuffer, 0, 8);
    }


    /**
     * transforms an integer into a byteArray with 2 fields, the first one is the
     * integer/255 the second one the integer%255
     * If you want to get the integer again be advised that due to the fact that if
     * the first byte is 0 we do not know if it should be 0 or 256 we wont be able to
     * get the full scope of the integer, we can only get 0-65279, everything higher will be
     * 0 - 255 again which is wrong - so don't use a higher number than 65279
     * @param toTransform the integer we want to transform into a byteArray with 2 fields
     * @return a byteArray with two fields, the first is the int/255 the second is the int%255
     */
    /* --fixme-- */
    /* The method getBytes(int) from the type PacketWriter can be declared as static */
    private byte[] getBytes(int toTransform){
        /* --fixme-- */
        /* The expression of type int is boxed into Integer */
        Integer mod = toTransform % 255;
        /* --fixme-- */
        /* The expression of type int is boxed into Integer */
        Integer times = toTransform / 255;
        byte[] byteArray = new byte[2];
        byteArray[0] = times.byteValue();
        byteArray[1] = mod.byteValue();
        return byteArray;
    }

    /**
    * transforms an integer into a byteArray with 2 fields
    * if the integer is bigger than 65536 it cant be displayed in the 2 bytes so do use less than that
    * the method is as follows: the small numbers can be stored in a byte with the Integer.byteValue() method from java so
    * the only thing we still need is the 2^8 till 2^15 value bits which are stored in the second byte
    * we just check if the integer is bigger than the highest magnitude of 2 we have left, if it is then we make that certain bit a 1 by using the OR operation
    * if it is not we just leave the 0 standing
    * @param toTransform the integer we want to be stored in a 2 byte array (should be smaller than 65536)
    * @return a two byte Array in which the integer (or part of it) is stored in. if you want to know how the int is encrypted see method description above
    */
    /* --fixme-- */
    /* The method getBytesFromInt(int) from the type PacketWriter can be declared as static */
    private byte[] getBytesFromInt(int toTransform){
        byte toReturn[] = new byte[2];
        /* --fixme-- */
        /* The expression of type int is boxed into Integer */
        Integer first = toTransform;
        toReturn[1] = first.byteValue();
        int second = 0b00000000;
        if(toTransform - 32768 >= 0){
            second = second | 0b10000000;
            toTransform = toTransform - 32768;
        }
        if(toTransform - 16384 >= 0){
            second = second | 0b01000000;
            toTransform = toTransform - 16384;
        }
        if(toTransform - 8192 >= 0){
            second = second | 0b00100000;
            toTransform = toTransform - 8192;
        }
        if(toTransform - 4096 >= 0){
            second = second | 0b00010000;
            toTransform = toTransform - 4096;
        }
        if(toTransform - 2048 >= 0){
            second = second | 0b00001000;
            toTransform = toTransform - 2048;
        }
        if(toTransform - 1024 >= 0){
            second = second | 0b00000100;
            toTransform = toTransform - 1024;
        }
        if(toTransform - 512 >= 0){
            second = second | 0b00000010;
            toTransform = toTransform - 512;
        }
        if(toTransform - 256 >= 0){
            second = second | 0b00000001;
            toTransform = toTransform - 256;
        }
        /* --fixme-- */
        /* The expression of type int is boxed into Integer */
        Integer transformer = second;
        toReturn[0] = transformer.byteValue();
        System.out.println(toReturn[0]);
        return toReturn;
    }

    /**
    *
    * just calls the flush method of the parent class
    */
    /* --fixme-- */
    /* The method flush() of type PacketWriter should be tagged with @Override since it actually overrides a superclass method */
    public void flush() throws IOException{
        super.flush();
    }

    /**
    * just calls the close method of the parent class
    */
    /* --fixme-- */
    /* The method close() of type PacketWriter should be tagged with @Override since it actually overrides a superclass method */
    public void close() throws IOException{
        super.close();
    }

}
