package IOInterface;
/* --fixme-- */
/* Javadoc: Missing comment for public declaration */
public class IO_Packet{

    private int geraeteId;
    private int PIN;
    private int namespaceId;
    private int count;
    private byte rwflag;
    private byte value;

    /* --fixme-- */
    /* Javadoc: Missing comment for public declaration */
    public IO_Packet(int gId, int pin, int nId, int count, byte rwflag, byte value){
        geraeteId = gId;
        PIN = pin;
        namespaceId = nId;
        this.count = count;
        this.rwflag = rwflag;
        this.value = value;
    }

    /* --fixme-- */
    /* Javadoc: Missing comment for public declaration */
    public int getGeraeteId(){
        return geraeteId;
    }

    /* --fixme-- */
    /* Javadoc: Missing comment for public declaration */
    public int getPin(){
        return PIN;
    }

    /* --fixme-- */
    /* Javadoc: Missing comment for public declaration */
    public int getNamespaceId(){
        return namespaceId;
    }

    /* --fixme-- */
    /* Javadoc: Missing comment for public declaration */
    public int getCount(){
        return count;
    }

    /* --fixme-- */
    /* Javadoc: Missing comment for public declaration */
    public byte getRWFlag(){
        return rwflag;
    }

    /* --fixme-- */
    /* Javadoc: Missing comment for public declaration */
    public byte getValue(){
        return value;
    }
}
