package edu.uci.cs237.tippersedge;

/**
 * An interface that should be implemented by classes that model a sensor that can be sampled.
 * By using a common interface, {@link AbstractSampleHandler} and its subclasses can be implemented in terms of the
 * interface and hence be kept general-purpose.
 *
 * @param <S> The type of data sampled by the sensor.
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public interface SampleProvider<S> {

    /**
     * Sample the sensor (retrieve a new reading).
     * @return The new sample (reading) or {@code null} if no data is available or an error occurred.
     */
    S sample();

}
