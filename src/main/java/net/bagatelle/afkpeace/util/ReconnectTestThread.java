package net.bagatelle.afkpeace.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import net.bagatelle.afkpeace.constants.ReconnectionConstants;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ServerAddress;

public class ReconnectTestThread extends Thread {

    private int canReconnect;

    private ServerAddress serverAddress;

    public ReconnectTestThread(ServerInfo serverInfo) {
        super();
        this.canReconnect = 0;
        this.serverAddress = ServerAddress.parse(serverInfo.address);
    }

    // Tries to connect to the server using a socket as many times as is set, and returns if it could
    public void run() {
        for (int i = 0; i <= ReconnectionConstants.maxReconnectTries; i++) {
            try {
                Socket connectionAttempt = new Socket(serverAddress.getAddress(), serverAddress.getPort());
                connectionAttempt.close();
                synchronized(this) {canReconnect = 1;}
                break;
            } catch (UnknownHostException e) {
            } catch (IOException e) {
            }
        }
        if(canReconnect != 1) {
            canReconnect = 2;
        }
        return;
    }

    public int getCanReconnect() {
        synchronized(this) {return canReconnect;}
    }

}