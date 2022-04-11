package is.swan.mcmarketapi.request;

import is.swan.mcmarketapi.request.Request.Method;

import java.util.concurrent.atomic.AtomicLong;

public class Throttler {

    private final AtomicLong readLastRetry = new AtomicLong(0);
    private final AtomicLong readLastRequest = new AtomicLong(System.currentTimeMillis());

    private final AtomicLong writeLastRetry = new AtomicLong(0);
    private final AtomicLong writeLastRequest = new AtomicLong(System.currentTimeMillis());

    public long stallFor(Method method) {
        long time = System.currentTimeMillis();

        if (method == Method.GET) {
            return Throttler.stallForHelper(this.readLastRetry, this.readLastRequest, time);
        } else {
            return Throttler.stallForHelper(this.writeLastRetry, this.writeLastRequest, time);
        }
    }

    private static long stallForHelper(AtomicLong aLastRetry, AtomicLong aLastRequest, long time) {
        long lastRetry = aLastRetry.get();
        long lastRequest = aLastRequest.get();

        if (lastRetry > 0 && (time - lastRequest) < lastRetry) {
            return lastRetry - (time - lastRequest);
        } else {
            return 0;
        }
    }

    public void setRead(long value) {
        readLastRetry.set(value);
        readLastRequest.set(System.currentTimeMillis());
    }

    public void setWrite(long value) {
        writeLastRetry.set(value);
        writeLastRequest.set(System.currentTimeMillis());
    }

    public void resetRead() {
        readLastRetry.set(0);
        readLastRequest.set(System.currentTimeMillis());
    }

    public void resetWrite() {
        writeLastRetry.set(0);
        writeLastRequest.set(System.currentTimeMillis());
    }
}
