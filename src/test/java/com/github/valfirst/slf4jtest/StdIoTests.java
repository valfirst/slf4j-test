package com.github.valfirst.slf4jtest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public class StdIoTests {
    private static final String CHARSET = StandardCharsets.UTF_8.toString();

    private static final PrintStream DEFAULT_OUT_STREAM = System.out;
    private static final PrintStream DEFAULT_ERR_STREAM = System.err;

    private static ByteArrayOutputStream out;
    private static ByteArrayOutputStream err;

    protected String getStdOut() {
        try {
            return out.toString(CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected String getStdErr() {
        try {
            return err.toString(CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new UncheckedIOException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        out = new ByteArrayOutputStream();
        System.setOut(createPrintStream(out));
        err = new ByteArrayOutputStream();
        System.setErr(createPrintStream(err));
    }

    @AfterEach
    void after() {
        out.reset();
        err.reset();
    }

    @AfterAll
    static void afterAll() throws IOException {
        System.setOut(DEFAULT_OUT_STREAM);
        out.close();
        System.setErr(DEFAULT_ERR_STREAM);
        err.close();
    }

    private static PrintStream createPrintStream(ByteArrayOutputStream out) {
        try {
            return new PrintStream(out, true, CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
