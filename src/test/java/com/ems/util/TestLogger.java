package com.ems.util;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * JUnit 5 extension that logs a clear PASS / FAIL / SKIPPED line
 * after every test method, including the test class and display name.
 *
 * Register on any test class with:
 *   {@code @ExtendWith(TestLogger.class)}
 */
public class TestLogger implements TestWatcher {

    private static final Logger LOG = Logger.getLogger("TEST");

    private static final String GREEN  = "\u001B[32m";
    private static final String RED    = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET  = "\u001B[0m";

    @Override
    public void testSuccessful(ExtensionContext ctx) {
        LOG.info(GREEN + "✔ PASS  " + RESET
                + label(ctx));
    }

    @Override
    public void testFailed(ExtensionContext ctx, Throwable cause) {
        LOG.severe(RED + "✘ FAIL  " + RESET
                + label(ctx)
                + RED + "  → " + cause.getMessage() + RESET);
    }

    @Override
    public void testAborted(ExtensionContext ctx, Throwable cause) {
        LOG.warning(YELLOW + "⊘ ABORT " + RESET
                + label(ctx)
                + YELLOW + "  → " + cause.getMessage() + RESET);
    }

    @Override
    public void testDisabled(ExtensionContext ctx, Optional<String> reason) {
        LOG.warning(YELLOW + "⊙ SKIP  " + RESET
                + label(ctx)
                + reason.map(r -> YELLOW + "  → " + r + RESET).orElse(""));
    }

    private String label(ExtensionContext ctx) {
        String clazz = ctx.getTestClass()
                .map(c -> c.getSimpleName())
                .orElse("?");
        String method = ctx.getDisplayName();
        return "[" + clazz + "] " + method;
    }
}
