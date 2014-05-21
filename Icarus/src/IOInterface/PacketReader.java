import java.io.*;
public class PacketReader extends BufferedInputStream{

    public PacketReader(InputStream in){
     super(in);
    }

    public IO_Packet readPacket() throws IOException{
        byte readBuffer[] = new byte[8];
        super.read(readBuffer, 0, 8);
        byte geraeteId[] = new byte[2];
        geraeteId[0] = readBuffer[0];
        geraeteId[1] = readBuffer[1];
        byte pin[] = new byte[2];
        pin[0] = readBuffer[2];
        pin[1] = readBuffer[3];
        byte namespace = readBuffer[4];
        byte count = readBuffer[5];
        System.out.println(count);
        byte rwflag = readBuffer[6];
        byte value = readBuffer[7];
        IO_Packet returnPacket = new IO_Packet(getIntFromBytes(geraeteId), getIntFromBytes(pin), (int)namespace, (int)count, rwflag, value);
        return returnPacket;
   }


    /**
     * transforms a byteArray with 2 fields into an integer, the first entry has 
     * to be the integer/255, the second entry the integer%255
     * Be advised: due to the fact that if
     * the first byte is 0 we do not know if it should be 0 or 256 we wont be able to
     * get the full scope of the integer, we can only get 0-65279, everything higher will be
     * 0 - 255 again which is wrong - so don't use a higher number than 65279
     * @param toTransform the byteArray we want to transform, first entry is the number/255, the second is number%255
     * @return the integer value we got from the byteArray
     */
    private int getIntFromBytes(byte[] toTransform){
        int mod = toTransform[1];
        int times = toTransform[0];
        if(mod < 0){
            mod = 256 + mod;
        }
        if(times < 0){
            times = 256 + mod;
        }
        int value = times*255 + mod;
        return value;
    }

    public void close() throws IOException{
        super.close();
    }


}
