package IOInterface;
import logger.*;
import parser.*;
import java.net.*;
import java.io.IOException;

public class ASynchronous_IO_Worker implements Runnable{
    private Logger logger;
    private InfoCollector infoColl;
    private ServerSocket sSocket;
    private boolean alive = true;
    private IO_Packet incomingPacket;
    private int serverPort;
    private InetAddress serverAddress;
    private String logKey = " [ASynchronous_IO_Worker]: ";

    /**
    *
    * @param logWriter Logger used to write logs via the .log function
    * @param infoColl InfoCollector used to pass the data we get from the network
    * @param sSocket ServerSocket that we use to establish a connection to the other IOManager, gets initiated in the class that initiates this one
    */
    ASynchronous_IO_Worker(Logger logWriter, InfoCollector infoColl, ServerSocket sSocket){
        this.logger = logWriter;
        this.infoColl = infoColl;
        this.sSocket = sSocket;
        serverPort = sSocket.getLocalPort();
        serverAddress = sSocket.getInetAddress();
        logger.log(1, logKey, "contructor done\n");
    }

    @Override
    /**
    * creates a Socket if a connection is incoming, then reads the stuff we get and passes it to the InfoCollector
    */
    public void run(){
        while(alive){
            logger.log(1, logKey, "worker is now running in the while(alive) loop\n");
            try(
                Socket cSocket = sSocket.accept();
            ){
                logger.log(2, logKey, "accepted an incoming connection\n");
                if(!alive){
                    logger.log(1, logKey, "server got the dead signal, shutting down now\n");
                    continue;
                }
                try(
                    PacketReader reader = new PacketReader(cSocket.getInputStream());
                ){
                    logger.log(4, logKey, "created the inputStream\n");
                    incomingPacket = reader.readPacket();
                    System.out.println("call some fancy method of tux and put the stuff in");
                    infoColl.update_device(new Integer(incomingPacket.getGeraeteId()).toString(), incomingPacket.getValue());
                }
                catch(IOException e){
                    logger.log(0, logKey, "caught an exception: " , e.getMessage(), "\n");
                }
            }
            catch(IOException e){
                logger.log(0, logKey, "caught an exception while accepting a new connection: " , e.getMessage(), "\n");
            }
        }
        try{
            sSocket.close();
        }
        catch(IOException e){
            logger.log(0, logKey, "caught an exception when trying to close the serverSocket: " , e.getMessage(), "\n");
        }
    }

    /**
    * sets the boolean to false so the run method shuts down, however we need to get past the accept() after we change the boolean value
    * so we create a connection to the server and close it again
    */
    public void kill(){
        logger.log(2, logKey, "called the kill method\n");
        alive = false;
        logger.log(2, logKey, "set the alive flag to false\n");
        try{
            Socket closeSocket = new Socket(serverAddress, serverPort);
            closeSocket.close();
        }
        catch(IOException e){
            logger.log(0, logKey, "caught an exception while establishing the last connection to the server: " , e.getMessage(), "\n");
        }
    }

}
