package IOInterface;

import Icarus.Main;
import logger.*;
import parser.*;
import config.*;
import java.net.*;
import java.io.IOException;

public class ASynchronous_IO_Worker implements Runnable {

    private Logger logger;
    private ParserContainer infoColl;
    private ServerSocket sSocket;
    private boolean alive = true;
    private IO_Packet incomingPacket;
    private int serverPort;
    private InetAddress serverAddress;
    private String logKey = " [ASynchronous_IO_Worker]: ";

    /**
     *
     * @param logWriter Logger used to write logs via the .log function
     * @param infoColl ParserContainer used to pass the data we get from the
     * network
     * @param sSocket ServerSocket that we use to establish a connection to the
     * other IOManager, gets initiated in the class that initiates this one
     */
    ASynchronous_IO_Worker(Logger logWriter, ParserContainer infoColl, Config_Reader confReader) throws IOException {
        this.logger = logWriter;
        this.infoColl = infoColl;
        ServerSocket aSyncSocket = new ServerSocket(confReader.get_int("async_port", 1, 65536));
        sSocket = aSyncSocket;
        serverPort = sSocket.getLocalPort();
        serverAddress = sSocket.getInetAddress();
        logger.log(1, logKey, "constructor done\n");
    }

    @Override
    /**
     * creates a Socket if a connection is incoming, then reads the stuff we get
     * and passes it to the ParserContainer
     */
    public void run() {
        while (alive) {
            logger.log(2, logKey, "worker is now running in the while(alive) loop\n");
            try (
                    Socket cSocket = sSocket.accept();) {
                logger.log(2, logKey, "accepted an incoming connection\n");
                if (!alive) {
                    logger.log(1, logKey, "server got the dead signal, shutting down now\n");
                    break;
                }
                try (
                        PacketReader reader = new PacketReader(cSocket.getInputStream());) {
                    logger.log(4, logKey, "created the inputStream\n");
                    incomingPacket = reader.readPacket();

                    infoColl.update_device(new Integer(incomingPacket.getGeraeteId()).toString(), incomingPacket.getValue());
                } catch (IOException e) {
                    logger.log(0, logKey,
                            "\n\n",
                            "ERROR: \n",
                            "    An Exception occured inside of ASynchronous_IO_Worker\n",
                            "    while trying to read a Packet\n",
                            "DETAILED ERROR: \n    ",
                            e.getMessage(), "\n\n"
                    );
                    Main.exit();
                }
            } catch (IOException e) {
                logger.log(0, logKey,
                        "\n\n",
                        "ERROR: \n",
                        "    An Exception occured inside of ASynchronous_IO_Worker\n",
                        "    while trying to accept a incoming Connection\n",
                        "DETAILED ERROR: \n    ",
                        e.getMessage(), "\n\n"
                );
                Main.exit();
            }
        }
        try {
            sSocket.close();
        } catch (IOException e) {
            logger.log(0, logKey,
                    "\n\n",
                    "ERROR: \n",
                    "    An Exception occured inside of ASynchronous_IO_Worker\n",
                    "    while trying to read a Packet\n",
                    "DETAILED ERROR: \n    ",
                    e.getMessage(), "\n\n"
            );
            Main.exit();
        }
    }

    /**
     * sets the boolean to false so the run method shuts down, however we need
     * to get past the accept() after we change the boolean value so we create a
     * connection to the server and close it again
     */
    public void kill() {
        logger.log(1, logKey, "called the kill method\n");
        alive = false;
        logger.log(1, logKey, "set the alive flag to false\n");
        try {
            Socket closeSocket = new Socket(serverAddress, serverPort);
            closeSocket.close();
        } catch (IOException e) {
            logger.log(0, logKey,
                    "\n\n",
                    "ERROR: \n",
                    "    An Exception occured inside of ASynchronous_IO_Worker\n",
                    "    while connecting the closeSocket for Thread Shut Down\n",
                    "DETAILED ERROR: \n    ",
                    e.getMessage(), "\n\n"
            );
            Main.exit();
        }
    }

}
