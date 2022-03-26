package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.MessageEncoderDecoderImpl;
import bgu.spl.net.impl.newsfeed.NewsFeed;
import bgu.spl.net.impl.rci.BGSData;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.ConnectionsImp;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
            BGSData data = new BGSData();
            int numOfThread = Integer.parseInt(args[1]);
            int port = Integer.parseInt(args[0]);
            Server.reactor(
                    numOfThread,
                    port, //port
                    () -> new BidiMessagingProtocolImpl(data), //protocol factory
                    MessageEncoderDecoderImpl::new //message encoder decoder factory
            ).serve();
    }
}