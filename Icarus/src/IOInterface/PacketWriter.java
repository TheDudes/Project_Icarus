import java.io.*;

public class PacketWriter extends BufferedOutputStream{

    public PacketWriter(OutputStream out){
        super(out);
    } 

    public void write(IO_Packet packet) throws IOException{
        byte buffer[] = new byte[2];
        byte writeBuffer[] = new byte[8];
        buffer = getBytes(packet.getGeraeteId());
        writeBuffer[0] = buffer[0];
        writeBuffer[1] = buffer[1];
        buffer = getBytes(packet.getPin());
        writeBuffer[2] = buffer[0];
        writeBuffer[3] = buffer[1];
        writeBuffer[4] = (byte)packet.getNamespaceId();
        System.out.println(packet.getCount());
        System.out.println((byte)packet.getCount());
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
    private byte[] getBytes(int toTransform){
        Integer mod = toTransform % 255;
        Integer times = toTransform / 255;
        byte[] byteArray = new byte[2];
        byteArray[0] = times.byteValue();
        byteArray[1] = mod.byteValue();
        return byteArray;
    }

    public void flush() throws IOException{
        super.flush();
    }

    public void close() throws IOException{
        super.close();
    }

}
