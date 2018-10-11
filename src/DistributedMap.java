import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Util;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedMap extends ReceiverAdapter implements SimpleStringMap {


    private Map<String, String> stringMap;
    private ProtocolStack protocolStack;
    private JChannel channel;

    public DistributedMap() throws  Exception{
        this.stringMap = new ConcurrentHashMap<>();
        System.setProperty("java.net.preferIPv4Stack", "true");

        channel = new JChannel(false);

        initProtocolStack();
        channel.setReceiver(this);
        channel.connect("DMap");
        channel.getState(null, 10000);
    }

    public boolean containsKey(String key){

        return this.stringMap.keySet().contains(key);

    }

    public synchronized String put(String key, String value){
        if(this.stringMap.keySet().contains(key) && this.stringMap.get(key).equals(value)) return null;
        this.stringMap.put(key, value);
        updateDistr(new MapHandler(key, value));
        return key;

    }

    public String get(String key){

        if(this.stringMap.keySet().contains(key)) return this.stringMap.get(key);
        return null;
    }


    public synchronized String remove(String key) {
        if(! this.stringMap.keySet().contains(key)) return null;
        this.stringMap.remove(key);
        updateDistr(new MapHandler(key));
        return key;
    }

    private void initProtocolStack() throws Exception {
        this.protocolStack = new ProtocolStack();
        this.channel.setProtocolStack(protocolStack);
        protocolStack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName("230.0.0.102")))
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE_TRANSFER());

        protocolStack.init();
    }

    @Override
    public void receive(Message msg) {
        MapHandler handler = (MapHandler) msg.getObject();
        if(handler.getOperationType() == MapHandler.OperationType.REMOVE){
            stringMap.remove(handler.getKey());
        } else {
            stringMap.put(handler.getKey(), handler.getValue());
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (stringMap) {
            Util.objectToStream(stringMap, new DataOutputStream(output));
        }
    }

    @Override
    public  void setState(InputStream input) throws Exception {
        Map<String, String> map = (Map<String, String>) Util.objectFromStream(new DataInputStream(input));
        synchronized (stringMap) {
            stringMap.clear();
            stringMap.putAll(map);
        }
    }

    private void updateDistr(MapHandler mapHandler){
        Message msg = new Message(null, null, mapHandler);
        try {
            channel.send(msg);
        } catch (Exception e) {
            System.out.println("Update Map failed"  + e.getMessage() );
        }
    }
    @Override
    public void viewAccepted(View view) {
        if(view instanceof MergeView) {
            ViewHandler viewHandler = new ViewHandler(channel, (MergeView) view);
            viewHandler.start();
        }
    }

}
