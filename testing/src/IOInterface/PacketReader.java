package IOInterface;

import java.io.*;
import logger.*;
import Icarus.Main;

/**
 *
 */
public class PacketReader extends BufferedInputStream {

    private Logger logger = null;
    private String key = " [PacketReader]: ";

    /**
     * standart constuctor call of the parent constructor
     */
    public PacketReader(InputStream in, Logger logger) {
        super(in);
        this.logger = logger;
    }

    public PacketReader(InputStream in) {
        super(in);
    }

    /**
     * reads a standart IO_Packet from the inputstream, transforms the geraeteId
     * and the pin to an integer again, same for namespace and count. calls the
     * read method of the parent class to retrieve information and transforms
     * some of it then
     *
     * @return an IO_Packet with all the information from Datapackage we
     * got/read.
     * @throws IOException
     */
    public IO_Packet readPacket() throws IOException {
        //array where we store the stuff we get from the parent class read in
        byte readBuffer[] = new byte[8];
        super.read(readBuffer, 0, 8);
        //byteArray to keep the information for the geraeteId, will be transformed to an int later on
        byte geraeteId[] = new byte[2];
        geraeteId[0] = readBuffer[0];
        geraeteId[1] = readBuffer[1];
        //byteArray to hold the information for the pin in, will be transformed into an actual in later on
        byte pin[] = new byte[2];
        pin[0] = readBuffer[2];
        pin[1] = readBuffer[3];
        byte namespace = readBuffer[4];
        byte pcount = readBuffer[5];
        byte rwflag = readBuffer[6];
        byte value = readBuffer[7];
        //creates a new IO_Packet that we can return, the conversions from a byteArray to an int take place here
        IO_Packet returnPacket = new IO_Packet(getIntFromByte(geraeteId), getIntFromByte(pin), (int) namespace, (int) pcount, rwflag, value);
        return returnPacket;
    }

    /**
     * reads a standart IO_Packet from the inputstream, transforms the geraeteId
     * and the pin to an integer again, same for namespace and count. calls the
     * read method of the parent class to retrieve information and transforms
     * some of it then
     *
     * @return an IO_Packet with all the information from Datapackage we
     * got/read.
     * @throws IOException
     */
    public IO_Packet readPacketMod() throws IOException {
        //array where we store the stuff we get from the parent class read in
        byte readBuffer[] = new byte[8];
        super.read(readBuffer, 0, 8);
        //byteArray to keep the information for the geraeteId, will be transformed to an int later on
        byte geraeteId[] = new byte[2];
        geraeteId[0] = readBuffer[0];
        geraeteId[1] = readBuffer[1];
        //byteArray to hold the information for the pin in, will be transformed into an actual in later on
        byte pin[] = new byte[2];
        pin[0] = readBuffer[2];
        pin[1] = readBuffer[3];
        byte namespace = readBuffer[4];
        byte pcount = readBuffer[5];
        byte rwflag = readBuffer[6];
        byte value = readBuffer[7];
        //creates a new IO_Packet that we can return, the conversions from a byteArray to an int take place here
        IO_Packet returnPacket = new IO_Packet(getIntFromBytes(geraeteId), getIntFromBytes(pin), (int) namespace, (int) pcount, rwflag, value);
        return returnPacket;
    }

    /**
     * transforms a byteArray with 2 fields into an integer, the first entry has
     * to be the integer/255, the second entry the integer%255 Be advised: due
     * to the fact that if the first byte is 0 we do not know if it should be 0
     * or 256 we wont be able to get the full scope of the integer, we can only
     * get 0-65279, everything higher will be 0 - 255 again which is wrong - so
     * don't use a higher number than 65279
     *
     * @param toTransform the byteArray we want to transform, first entry is the
     * number/255, the second is number%255
     * @return the integer value we got from the byteArray
     */
    private static int getIntFromBytes(byte[] toTransform) {
        int mod = toTransform[1];
        int times = toTransform[0];
        if (mod < 0) {
            mod = 256 + mod;
        }
        if (times < 0) {
            times = 256 + mod;
        }
        int value = times * 255 + mod;
        return value;
    }

    /**
     * transforms a 2 byte Array into an integer, for this method the byteArray
     * should be formatted in a specific Format this means: the second
     * field(which displays the magnitude 0-7 from 2) is stored in the second
     * byte via the Integer.byteValue() the first field displays the magnitudes
     * 8-15 the way we get those numbers is by looking which bits are 1 and
     * which 0 and then assigning them the proper magnitude this means that if
     * the number is higher than 128 the bit for the value 32768 is set which
     * means we add this number to the int rinse and repeat for the smaller
     * magnitudes
     *
     * @param toTransform a byteArray that should be 2 byte big and which
     * content we shall transform back to an integer
     * @return an integer derived from the byteArray
     */
    private static int getIntFromByte(byte[] toTransform) {
        int first = toTransform[1];
        int second = toTransform[0];
        if (first < 0) {
            first = 256 + first;
        }
        if (second < 0) {
            second = 256 + second;
        }
        int toReturn = first;
        if (second - 128 >= 0) {
            toReturn += 32768;
            second -= 128;
        }
        if (second - 64 >= 0) {
            toReturn += 16384;
            second -= 64;
        }
        if (second - 32 >= 0) {
            toReturn += 8192;
            second -= 32;
        }
        if (second - 16 >= 0) {
            toReturn += 4096;
            second -= 16;
        }
        if (second - 8 >= 0) {
            toReturn += 2048;
            second -= 8;
        }
        if (second - 4 >= 0) {
            toReturn += 1024;
            second -= 4;
        }
        if (second - 2 >= 0) {
            toReturn += 512;
            second -= 2;
        }
        if (second - 1 >= 0) {
            toReturn += 256;
            second -= 1;
        }
        return toReturn;
    }

    @Override
    /**
     * simply calls the close method of the parent class
     */
    public void close() throws IOException {
        super.close();
    }

}
