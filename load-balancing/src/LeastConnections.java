import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LeastConnections {
    private Map<String, AtomicInteger> serverConnections;

    public LeastConnections(List<String> servers) {
        serverConnections = new HashMap<>();
        for (String server : servers) {
            serverConnections.put(server, new AtomicInteger(0));
        }
    }

    public String getNextServer() {
        String server = serverConnections.entrySet().stream()
                .min(Comparator.comparingInt(entry -> entry.getValue().get()))
                .map(Map.Entry::getKey)
                .orElse(null);

        if (server != null) {
            serverConnections.get(server).incrementAndGet();
        }
        return server;
    }

    public void releaseConnection(String server) {
        AtomicInteger connection = serverConnections.get(server);
        if (connection != null) {
            connection.updateAndGet(v -> v > 0 ? v - 1 : 0);
        }
    }

    public static void main(String[] args) {
        List<String> servers = List.of("Server1", "Server2", "Server3");
        LeastConnections leastConnectionsLB = new LeastConnections(servers);

        for(int t = 0; t < 10; t++) {
            new Thread(() -> {
                for (int i = 0; i < 6; i++) {
                    String server = leastConnectionsLB.getNextServer();
                    System.out.println(server);
                    leastConnectionsLB.releaseConnection(server);
                }
            }).start();
        }
    }
}