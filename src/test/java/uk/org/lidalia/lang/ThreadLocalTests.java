package uk.org.lidalia.lang;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

/**
 * This class was written by Robert Elliot and is originally located at
 * https://github.com/Mahoney/lidalia-lang
 */
public class ThreadLocalTests {

    @Test
    public void differentValuePerThread() throws InterruptedException {
        final ThreadLocal<String> threadLocal = new ThreadLocal<>("Initial");
        final AtomicReference<String> fromThread = new AtomicReference<>();
        threadLocal.set("Thread1");

        Thread thread =
                new Thread(
                        () -> {
                            threadLocal.set("Thread2");
                            fromThread.set(threadLocal.get());
                        });
        thread.start();
        thread.join();

        assertThat(threadLocal.get(), is("Thread1"));
        assertThat(fromThread.get(), is("Thread2"));
    }

    @Test
    public void resetWorksForAllThreads() throws InterruptedException {
        final ThreadLocal<String> threadLocal = new ThreadLocal<>("Initial");
        final AtomicReference<String> fromThread = new AtomicReference<>();

        Thread thread =
                new Thread(
                        () -> {
                            threadLocal.set("Thread2");
                            threadLocal.reset();
                            fromThread.set(threadLocal.get());
                        });
        thread.start();
        thread.join();

        assertThat(threadLocal.get(), is("Initial"));
        assertThat(fromThread.get(), is("Initial"));
    }

    @Test
    public void initialValueWorksForAllThreads() throws InterruptedException {
        final ThreadLocal<String> threadLocal = new ThreadLocal<>("Initial Value");
        final AtomicReference<String> fromThread = new AtomicReference<>();

        Thread thread = new Thread(() -> fromThread.set(threadLocal.get()));
        thread.start();
        thread.join();

        assertThat(threadLocal.get(), is("Initial Value"));
        assertThat(fromThread.get(), is("Initial Value"));
    }

    @Test
    public void initialValueSourceIsCalledSeparatelyPerThread() throws InterruptedException {
        final ThreadLocal<String> threadLocal =
                new ThreadLocal<>(() -> Thread.currentThread().getName());
        final AtomicReference<String> fromThread = new AtomicReference<>();

        Thread thread = new Thread(() -> fromThread.set(threadLocal.get()));
        thread.start();
        thread.join();

        assertThat(threadLocal.get(), is(Thread.currentThread().getName()));
        assertThat(fromThread.get(), is(thread.getName()));
    }

    @Test
    public void initialValueSourceIsStateful() {
        final ThreadLocal<AtomicReference<String>> threadLocal =
                new ThreadLocal<>(() -> new AtomicReference<>("initial value"));

        threadLocal.get().set("new value");

        assertThat(threadLocal.get().get(), is("new value"));
    }

    @Test
    public void initialValueSourceIsStatefulOtherThread() throws InterruptedException {
        final ThreadLocal<AtomicReference<String>> threadLocal =
                new ThreadLocal<>(() -> new AtomicReference<>("initial value"));

        final AtomicReference<String> fromThread = new AtomicReference<>();

        Thread thread =
                new Thread(
                        () -> {
                            threadLocal.get().set("new value");
                            fromThread.set(threadLocal.get().get());
                        });
        thread.start();
        thread.join();

        assertThat(fromThread.get(), is("new value"));
    }

    @Test
    public void removeWorks() {
        ThreadLocal<String> threadLocal = new ThreadLocal<>("Initial Value");
        threadLocal.set("New Value");
        threadLocal.remove();
        assertThat(threadLocal.get(), is("Initial Value"));
    }

    @Test
    public void removeWorksOtherThread() throws InterruptedException {
        final ThreadLocal<String> threadLocal =
                new ThreadLocal<>(() -> Thread.currentThread().getName());
        final AtomicReference<String> fromThread = new AtomicReference<>();

        Thread thread =
                new Thread(
                        () -> {
                            threadLocal.set("New Value");
                            threadLocal.remove();
                            fromThread.set(threadLocal.get());
                        });
        thread.start();
        thread.join();

        assertThat(threadLocal.get(), is(Thread.currentThread().getName()));
        assertThat(fromThread.get(), is(thread.getName()));
    }
}
