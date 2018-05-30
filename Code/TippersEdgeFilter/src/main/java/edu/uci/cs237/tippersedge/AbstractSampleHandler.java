package edu.uci.cs237.tippersedge;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Samples a provided sensor (an implementation of {@link SampleProvider}), analyzes if the sample (sensor reading)
 * should be uploaded (by invoking the subclass' implementation of {@link #shouldIncludeSample(Object)}) and, if so,
 * ultimately uploads the reading (using the subclass' implementation of {@link #uploadSample(Object)}) to the TIPPERS
 * backend. The class accepts a type parameter, thereby making it applicable any type of sensor sample, not just images
 * shot by the surveillance cameras.
 * </p>
 *
 * Previous samples are cached in {@link #mSampleCache}, providing subclasses with the opportunity to consider recent
 * samples in their implementations of {@link #shouldIncludeSample(Object)}.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @param <S> A class that encapsulates/models the sample/reading data obtained from the sensor.
 */
public abstract class AbstractSampleHandler<S> {

    /**
     * Flag for toggling console debug output on/off.
     */
    private static final boolean ENABLE_DEBUG_OUTPUT = true;

    /**
     * Provider of sensor readings (samples), e.g., a {@link edu.uci.cs237.tippersedge.cameras.CameraRestClient} is a
     * provider of camera images.
     */
    protected final SampleProvider<S> mSampleProvider;

    /**
     * Fixed-size cache of included samples, i.e., samples that were (attempted) uploaded to TIPPERS backend.
     * Exposed to subclasses such that sample inclusion logic in subclasses may consider past samples.
     * In multithreaded environments, access to this variable should be in synchronized blocks using the variable itself
     * as the lock.
     * For example, the following code could be an implementation of {@link #shouldIncludeSample(Object)} which only
     * uploads a new sensor reading if it differs from the previous one:
     *
     * <pre>
     * public boolean shouldIncludeSample(S sample) {
     *     synchronized(mSampleCache) {
     *         if (mSampleCache.size() > 0) {
     *             // There are cached samples, compare this and the previous sample, and only upload this sample if
     *             // it differs from the previous one.
     *             return !mSampleCache.get(mSampleCache.size()-1).equals(sample);
     *         }
     *         return true;
     *     }
     * }
     * </pre>
     *
     * Courtesy of <a href="https://stackoverflow.com/a/21047889/1214974">https://stackoverflow.com/a/21047889/1214974</a>.
     */
    protected final List<S> mSampleCache = new ArrayList<S>() {
        @Override
        public boolean add(S s) {
            boolean added = super.add(s);
            if (size() > AbstractSampleHandler.this.mSampleCacheSize) {
                removeRange(0, size() - AbstractSampleHandler.this.mSampleCacheSize);
            }
            return added;
        }
    };

    /**
     * The maximum number of elements in {@link #mSampleCache}.
     */
    protected final int mSampleCacheSize;

    public AbstractSampleHandler(SampleProvider<S> sampleProvider, int sampleCacheSize) {
        mSampleProvider = sampleProvider;
        mSampleCacheSize = sampleCacheSize;
    }

    /**
     * Sample the {@link SampleProvider} associated with this {@code AbstractSampleHandler} (i.e., the one provided
     * to the constructor) and upload the sample to the TIPPERS backend if the sample is chosen for inclusion (based on
     * the return value of {@link #shouldIncludeSample(Object)}).
     */
    public void sampleAndUpload() {
        S sample = mSampleProvider.sample();

        if (ENABLE_DEBUG_OUTPUT && sample != null) {
            System.out.println(String.format("[ New sample read: '%s' ]", sample.toString()));
        }

        if (sample != null && shouldIncludeSample(sample)) {
            // Sample is valid and should be included.
            // Attempt upload sample to TIPPERS backend db.
            boolean uploaded = uploadSample(sample);
            if (ENABLE_DEBUG_OUTPUT) {
                String output = uploaded ? String.format("[ Upload of '%s' SUCCEEDED ]", sample) :
                        String.format("[ Upload of '%s' FAILED ]", sample);
                System.out.println(output);
            }
            if (uploaded) {
                // Only cache sample if sample was persisted at the backend.
                mSampleCache.add(sample);
            }
        }
    }

    /**
     * Decides if a given sample should be included (i.e, uploaded to the TIPPERS backend).
     * Subclasses should implement their sample inclusion decision logic here.
     *
     * @param sample The sample to test for inclusion.
     * @return {@code true} if the sample should be included, {@code false} if it should be discarded.
     */
    abstract protected boolean shouldIncludeSample(S sample);

    /**
     * Uploads the sample to the TIPPERS backend. Subclasses should implement this method so that it targets the endpoint
     * designated for the sample type S.
     *
     * @param sample The sample to be uploaded to the TIPPERS backend.
     * @return {@code true} if the sample was successfully uploaded, {@code false} otherwise (e.g., in case of network error).
     */
    abstract protected boolean uploadSample(S sample);
}
