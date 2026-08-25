package br.ufu.facom.ereno.api;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Single seeded source of randomness for the whole simulation.
 *
 * <p>Before this class existed, {@code IED.randomBetween} built a
 * {@code new Random(System.nanoTime())} on every call. That made every run
 * unreproducible: the same configuration re-executed produced a statistically
 * equivalent but element-wise unrelated dataset, so no result could be traced
 * back to the data that produced it.</p>
 *
 * <p>All randomness now flows through one {@link Random} seeded once from
 * {@code run.seed} in {@code params.properties}. Set it to {@code auto} to draw
 * a fresh seed from the clock: the drawn value is logged and recorded in
 * {@link RunContext}, so the run stays reproducible afterwards.</p>
 *
 * <p>Reproducibility requires the sequence of calls to be deterministic, which
 * holds because message generation is single-threaded. {@code Random} is itself
 * thread-safe, so parallel work elsewhere cannot corrupt the state - but it
 * would make the draw order non-deterministic, so do not call this from
 * parallel streams.</p>
 *
 * @see RunContext
 */
public final class Rng {

    private static Random random;
    private static long seed;

    private Rng() {
    }

    /**
     * Seeds the generator. Called once by {@link RunContext#loadConfigs()}.
     */
    public static synchronized void seed(long value) {
        seed = value;
        random = new Random(value);
        Logger.getLogger("Rng").info("Seeded with " + value);
    }

    public static long getSeed() {
        return seed;
    }

    private static synchronized Random get() {
        if (random == null) {
            // Nothing seeded us, which means a scenario ran without loading
            // RunContext. Fail loudly instead of silently going unreproducible.
            throw new IllegalStateException(
                    "Rng was never seeded. Call RunContext.loadConfigs() before generating messages.");
        }
        return random;
    }

    public static int nextInt(int lowerLimit, int upperLimit) {
        return lowerLimit + get().nextInt(upperLimit - lowerLimit + 1);
    }

    public static double nextDouble(double lowerLimit, double upperLimit) {
        return lowerLimit + (upperLimit - lowerLimit) * get().nextDouble();
    }
}
