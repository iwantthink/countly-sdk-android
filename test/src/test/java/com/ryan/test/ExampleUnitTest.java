package com.ryan.test;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testFuture() {
        Future<String> future = Executors.newFixedThreadPool(1).submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return doSomeThing();
            }
        });
        while (!future.isDone()) {
            System.out.println("not  done");
        }
//        try {
//            System.out.println(future.get());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

    private String doSomeThing() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "123";
    }

    @Test
    public void testRunnable() throws Exception {
        String result = java.net.URLEncoder.encode("[{\"key\":\"123\"},{},{}]", "UTF-8");
        System.out.println("result = " + result);
        result = java.net.URLDecoder.decode("%5B%7B%22key%22%3A%22%5BCLY%5D_view%22%2C%22count%22%3A1%2C%22timestamp%22%3A1519485579358%2C%22hour%22%3A23%2C%22dow%22%3A6%2C%22segmentation%22%3A%7B%22name%22%3A%22com.ryan.test.MainActivity%22%2C%22visit%22%3A%221%22%2C%22segment%22%3A%22Android%22%2C%22start%22%3A%221%22%7D%2C%22sum%22%3A0%7D%5D", "UTF-8");
        System.out.println("result = " + result);

    }
}