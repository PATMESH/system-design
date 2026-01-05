import java.util.List;
import java.util.Random;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LeastResponseTime {

    private final Map<String, Double> responseTimes = new ConcurrentHashMap<>();

    public LeastResponseTime(List<String> servers) {
        servers.forEach(s -> responseTimes.put(s, 0.0));
    }

    public String getNextServer() {
        return responseTimes.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow();
    }

    public void updateResponseTime(String server, double responseTime) {
        responseTimes.put(server, responseTime);
    }

    public static double simulateResponseTime(String server) {
        // Simulating response time with random delay
        Random random = new Random();
        double delay = 0.1 + (1.0 - 0.1) * random.nextDouble();
        try {
            Thread.sleep((long) (delay * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return delay;
    }

    public static void main(String[] args) {
        List<String> servers = List.of("Server1", "Server2", "Server3");
        LeastResponseTime leastResponseTimeLB = new LeastResponseTime(servers);

        for (int i = 0; i < 6; i++) {
            String server = leastResponseTimeLB.getNextServer();
            System.out.println("Request " + (i + 1) + " -> " + server);
            double responseTime = simulateResponseTime(server);
            leastResponseTimeLB.updateResponseTime(server, responseTime);
            System.out.println("Response Time: " + String.format("%.2f", responseTime) + "s");
        }
    }
}