package com.mycompany.app.WebServer.Protocols;

import com.mycompany.app.WebServer.Message;

public class BaseProtocol implements ProtocolInterface {

    @Override
    public int handleMessage(Message message) {
        int responseCode = 500; // by default throw server error

        switch (message.endpoint) {
            case "chat":
                
                break;
        
            default:
                break;
        }

        return responseCode;
    }
}
