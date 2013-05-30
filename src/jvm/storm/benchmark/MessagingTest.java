package storm.benchmark;

import java.util.Map;

import backtype.storm.messaging.IContext;
import backtype.storm.messaging.IConnection;
import backtype.storm.messaging.TransportFactory;
import backtype.storm.messaging.netty.StormClientHandler;
import backtype.storm.utils.Utils;
import backtype.storm.Config;

public class MessagingTest {
    private static final int PORT = 6800; 
    private static final int TASK = 1; 
    private static final int MSG_COUNT = 100000;
    private static final String STORM_ID  = "abc";
    private static final int BUFFER_SIZE = 102400;
    private static final int REPEATS = 1000;

    private static void batch_bench(IConnection client, IConnection server) {
        long startTime = System.currentTimeMillis();

        for (int ind = 1; ind <= MSG_COUNT; ind ++) {
            String req_msg = new Integer(ind).toString();
            client.send(TASK, req_msg.getBytes());
        }

        for (int ind = 1; ind <= MSG_COUNT; ind ++) {
            server.recv(0);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime-startTime) + "ms"); 
    }

    private static void one_by_one_bench(IConnection client, IConnection server) {
        long startTime = System.currentTimeMillis();

        for (int ind = 1; ind <= MSG_COUNT; ind ++) {
            String req_msg = new Integer(ind).toString();
            client.send(TASK, req_msg.getBytes());
            server.recv(0);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime-startTime) + "ms"); 
    }

    private static void benchmark(Map conf, String plugin_name) {
        IContext context = TransportFactory.makeContext(conf);
        IConnection server = context.bind(null, PORT);
        IConnection client = context.connect(null, "localhost", PORT);
        System.out.println("("+plugin_name+") "+REPEATS+" messages of payload size "+ BUFFER_SIZE + " sent one by one");
        one_by_one_bench(client, server);
        System.out.println("("+plugin_name+") "+MSG_COUNT+" short msgs in batches" );
        batch_bench(client,server);
        client.close();
        server.close();
        context.term();
    }

    @SuppressWarnings("unchecked")
    public static void benchmark_netty() {
        Map conf = new Config();
        conf.put(Config.STORM_MESSAGING_TRANSPORT, "backtype.storm.messaging.netty.Context");
        conf.put(Config.STORM_MESSAGING_NETTY_BUFFER_SIZE, BUFFER_SIZE);
        conf.put(Config.STORM_MESSAGING_NETTY_MAX_RETRIES, 10);
        conf.put(Config.STORM_MESSAGING_NETTY_MIN_SLEEP_MS, 1000);
        conf.put(Config.STORM_MESSAGING_NETTY_MAX_SLEEP_MS, 5000);

        benchmark(conf, "Netty");
    }

    @SuppressWarnings("unchecked")
    public static void benchmark_zmq() {
        Map conf = Utils.readDefaultConfig();
        conf.put(Config.STORM_MESSAGING_TRANSPORT, "backtype.storm.messaging.zmq");
        conf.put("topology.executor.receive.buffer.size", BUFFER_SIZE);
        conf.put("topology.executor.send.buffer.size", BUFFER_SIZE);
        conf.put("topology.receiver.buffer.size", BUFFER_SIZE);
        conf.put("topology.transfer.buffer.size", BUFFER_SIZE);

        benchmark(conf, "ZMQ");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        benchmark_netty();

        benchmark_zmq();
    }
}
