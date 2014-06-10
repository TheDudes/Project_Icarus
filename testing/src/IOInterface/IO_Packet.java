package IOInterface;
/**
* a class that stores stuff we want to exchange with the other group in as per our definition of what
* a packet should look like, see the constructor for details which things are stored as which type
*/
public class IO_Packet{

    private int geraeteId;
    private int PIN;
    private int namespaceId;
    private int count;
    private byte rwflag;
    private byte value;

    /**
    * @param gId the Id of the device we want to read/do stuff with
    * @param pin the pin of the device we want to read/write something, see getPIN
    * @param nId the namespaceId of the device
    * @param count the number of pins we want to read as an integer
    * @param rwflag a flag that represents if we want to write/read/poll the pin(s)
    * @param value the value we want to set, if we want to write something this is 00000000
    */
    public IO_Packet(int gId, int pin, int nId, int count, byte rwflag, byte value){
        geraeteId = gId;
        PIN = pin;
        namespaceId = nId;
        this.count = count;
        this.rwflag = rwflag;
        this.value = value;
    }

    /**
    * returns the deviceId of the device we want to read or that was read
    */
    public int getGeraeteId(){
        return geraeteId;
    }

    /**
    * returns the pin of the device we want to read/was read
    * if we read several pins this is the first PIN
    */
    public int getPin(){
        return PIN;
    }

    /**
    * returns the namespaceId of the packet as an integer
    */
    public int getNamespaceId(){
        return namespaceId;
    }

    /**
    * returns the number of bits we read/should read as an int
    */
    public int getCount(){
        return count;
    }

    /**
    * returns the rwflag that got passed on as a byte
    */
    public byte getRWFlag(){
        return rwflag;
    }

    /**
    * returns the value we got passed as a byte
    */
    public byte getValue(){
        return value;
    }
}
