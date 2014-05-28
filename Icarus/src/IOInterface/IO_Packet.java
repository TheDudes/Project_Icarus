public class IO_Packet{

    private int geraeteId;
    private int PIN;
    private int namespaceId;
    private int count;
    private byte rwflag;
    private byte value;

    public IO_Packet(int gId, int pin, int nId, int count, byte rwflag, byte value){
        geraeteId = gId;
        PIN = pin;
        namespaceId = nId;
        this.count = count;
        this.rwflag = rwflag;
        this.value = value;
    }

    public int getGeraeteId(){
        return geraeteId;
    }

    public int getPin(){
        return PIN;
    }

    public int getNamespaceId(){
        return namespaceId;
    }

    public int getCount(){
        return count;
    }

    public byte getRWFlag(){
        return rwflag;
    }

    public byte getValue(){
        return value;
    }
}
