import java.time.Instant;

public class SlidingWindowCounter {
    private final long windowSizeInSeconds;   // Size of the sliding window in seconds
    private final long maxRequestsPerWindow;  // Maximum number of requests allowed in the window
    private long currentWindowStart;          // Start time of the current window
    private long previousWindowCount;         // Number of requests in the previous window
    private long currentWindowCount;          // Number of requests in the current window

    public SlidingWindowCounter(long windowSizeInSeconds, long maxRequestsPerWindow) {
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.currentWindowStart = Instant.now().getEpochSecond();
        this.previousWindowCount = 0;
        this.currentWindowCount = 0;
    }

    public synchronized boolean allowRequest() {
        long now = Instant.now().getEpochSecond();
        long timePassedInWindow = now - currentWindowStart;

        // Check if we've moved to a new window
        if (timePassedInWindow >= windowSizeInSeconds) {
            previousWindowCount = currentWindowCount;
            currentWindowCount = 0;
            currentWindowStart = now;
            timePassedInWindow = 0;
        }

        // Calculate the weighted count of requests
        double weightedCount = previousWindowCount * ((windowSizeInSeconds - timePassedInWindow) / (double) windowSizeInSeconds)
                + currentWindowCount;

        if (weightedCount < maxRequestsPerWindow) {
            currentWindowCount++;  // Increment the count for this window
            return true;           // Allow the request
        }
        return false;  // We've exceeded the limit, deny the request
    }

    public static void main(String[] args) throws InterruptedException {
        // Allow 5 requests per 10 seconds
        SlidingWindowCounter limiter = new SlidingWindowCounter(10, 5);

        System.out.println("Sending 5 requests quickly:");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + ": " +
                    (limiter.allowRequest() ? "Allowed" : "Rate limited"));
        }

        System.out.println("\nSending one extra request (should be limited):");
        System.out.println(limiter.allowRequest() ? "Allowed" : "Rate limited");

        System.out.println("\nSleeping for 6 seconds...");
        Thread.sleep(6000);

        System.out.println("\nSending requests after partial window shift:");
        for (int i = 1; i <= 3; i++) {
            System.out.println("Request " + i + ": " +
                    (limiter.allowRequest() ? "Allowed" : "Rate limited"));
        }

        System.out.println("\nSleeping until window fully rolls over...");
        Thread.sleep(5000);

        System.out.println("\nSending requests after full window:");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + ": " +
                    (limiter.allowRequest() ? "Allowed" : "Rate limited"));
        }
    }

}