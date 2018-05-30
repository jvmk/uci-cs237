package edu.uci.cs237.tippersedge;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An {@link AbstractSampleHandler} that <em>periodically</em> samples the sensor and uploads the sensor reading to a
 * REST endpoint.
 * In other words, this class simply periodically invokes {@link AbstractSampleHandler#sampleAndUpload()}.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @param <S> A class that encapsulates/models the sample/reading data obtained from the sensor.
 */
public abstract class AbstractPeriodicSampleHandler<S> extends  AbstractSampleHandler<S> {

    private final ScheduledExecutorService mScheduledExecutor;
    private final long mSampleRateMillis;

    public AbstractPeriodicSampleHandler(SampleProvider<S> sampleProvider, int sampleCacheSize, long sampleRateMillis) {
        super(sampleProvider, sampleCacheSize);
        /*
         * Use more than one thread in order to prevent long-running invocations of
         * AbstractSampleHandler#sampleAndUpload() from breaking the periodicity (e.g., Darknet object detection takes
         * approximately ~14 seconds on a Mid 2014 MacBook Pro with an Intel i5 2.6GHz)
         *
         * TODO not sure if this solution will actually work, probably will on many-core CPU, but not on my dual-core.
         */
        mScheduledExecutor = Executors.newScheduledThreadPool(10);
        mSampleRateMillis = sampleRateMillis;
    }

    /**
     * Starts periodic sampling and upload.
     */
    public void startPeriodicSampling() {
        mScheduledExecutor.scheduleAtFixedRate(() -> this.sampleAndUpload(), 0, mSampleRateMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops periodic sampling and upload, but attempts to let ongoing and queued tasks finish first.
     * @param awaitTermination Set to {@code true} if caller wishes to block while waiting for graceful shutdown.
     * @param timeoutMillis The maximum number of milliseconds to wait for graceful shutdown.
     *                      Ignored if {@code awaitTermination} is {@code false}.
     */
    public void stopPeriodicSampling(boolean awaitTermination, long timeoutMillis) {
        mScheduledExecutor.shutdown();
        if (awaitTermination) {
            try {
                mScheduledExecutor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
