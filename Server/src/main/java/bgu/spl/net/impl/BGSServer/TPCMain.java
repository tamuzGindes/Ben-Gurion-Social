package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.MessageEncoderDecoderImpl;
import bgu.spl.net.impl.newsfeed.NewsFeed;
import bgu.spl.net.impl.rci.BGSData;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.srv.Server;

import java.util.function.Supplier;

public class TPCMain {
    public static void main(String[] args) {
        BGSData data = new BGSData();
        Server.threadPerClient(
                Integer.parseInt(args[0]),
                () -> new BidiMessagingProtocolImpl(data),
                MessageEncoderDecoderImpl::new
        ).serve();
    }
}
