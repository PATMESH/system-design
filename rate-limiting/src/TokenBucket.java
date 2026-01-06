
public class TokenBucket {
    private final long capacity;        // Maximum number of tokens the bucket can hold
    private final double fillRate;      // Rate at which tokens are added to the bucket (tokens per second)
    private double availableTokens;              // Current number of tokens in the bucket
    private long lastRefillNanos; // Last time we refilled the bucket

    public TokenBucket(long capacity, double fillRate) {
        this.capacity = capacity;
        this.fillRate = fillRate;
        this.availableTokens = capacity;  // Start with a full bucket
        this.lastRefillNanos = System.nanoTime();
    }

    public synchronized boolean allowRequest(int requestedTokens) {
        if (requestedTokens <= 0 || requestedTokens > capacity) {
            return false;
        }

        refill();  // First, add any new tokens based on elapsed time

        if (this.availableTokens < requestedTokens) {
            return false;  // Not enough tokens, deny the request
        }

        this.availableTokens -= requestedTokens;  // Consume the tokens
        return true;  // Allow the request
    }

    private void refill() {
        long now = System.nanoTime();
        // Calculate how many tokens to add based on the time elapsed
        double tokensToAdd = (now - lastRefillNanos) * fillRate / 1000000000.0;
        this.availableTokens = Math.min(capacity, this.availableTokens + tokensToAdd);  // Add tokens, but don't exceed capacity
        this.lastRefillNanos = now;
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucket tokenBucket = new TokenBucket(5, 5);
        for(int i=0;i<7;i++){
            System.out.println(tokenBucket.allowRequest(1)? "Allowed": "Rate limited");
        }
        Thread.sleep(999);
        for(int i=0;i<7;i++){
            System.out.println(tokenBucket.allowRequest(1)? "Allowed": "Rate limited");
        }
    }
}