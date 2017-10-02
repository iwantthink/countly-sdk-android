package ly.count.android.sdk.internal;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;

import java.net.MalformedURLException;
import java.util.List;

import ly.count.android.sdk.Config;

import static android.support.test.InstrumentationRegistry.getContext;

@RunWith(AndroidJUnit4.class)
public class SessionImplTests {
    private Context ctx = null;

    @Before
    public void setupEveryTest(){
        ctx = new ContextImpl(getContext());
    }

    @After
    public void cleanupEveryTests(){
        //validateMockitoUsage();
    }

    @Test
    public void constructor_empty(){
        final long allowance = 1000000000;
        long time = System.nanoTime();
        SessionImpl session = new SessionImpl(ctx);

        long diff = session.getId() - time;
        Assert.assertEquals(true, diff < allowance);
    }

    @Test
    public void constructor_deserialize(){
        long targetID = 11234L;
        SessionImpl session = new SessionImpl(ctx, targetID);
        Assert.assertEquals(targetID, (long)session.getId());
    }

    @Test
    public void addParams() throws Exception{
        SessionImpl session = new SessionImpl(ctx);
        Assert.assertNotNull(session.params);

        StringBuilder sbParams = new StringBuilder();
        String[] keys = new String[]{"a", "b", "c"};
        String[] vals = new String[]{"11", "22", "33"};

        sbParams.append(keys[0]).append("=").append(vals[0]);
        session.addParam(keys[0], vals[0]);
        Assert.assertEquals(sbParams.toString(), session.params.toString());

        sbParams.append("&").append(keys[1]).append("=").append(vals[1]);
        session.addParam(keys[1], vals[1]);
        Assert.assertEquals(sbParams.toString(), session.params.toString());

        sbParams.append("&").append(keys[2]).append("=").append(vals[2]);
        session.addParam(keys[2], vals[2]);
        Assert.assertEquals(sbParams.toString(), session.params.toString());
    }

    @Test
    public void beginEndSession_doubleBegin() throws MalformedURLException{
        TestingUtilityInternal.setupLogs(null);

        long sessionID = 12345;
        SessionImpl session = new SessionImpl(ctx, sessionID);

        long beginTime = 234;
        session.begin(beginTime);

        session.begin(345L);
        Assert.assertEquals(sessionID, (long)session.getId());
        Assert.assertEquals(beginTime, (long)session.getBegan());

        session.end();
        Assert.assertEquals(sessionID, (long)session.getId());
        Assert.assertEquals(beginTime, (long)session.getBegan());

        session.begin();
        Assert.assertEquals(sessionID, (long)session.getId());
        Assert.assertEquals(beginTime, (long)session.getBegan());
    }

    @Test
    public void beginEndSession_valuesCorrect() {
        SessionImpl session = new SessionImpl(ctx);

        session.begin();
        session.end();

        Assert.assertEquals(session.began, session.getBegan());
        Assert.assertEquals(session.ended, session.getEnded());
    }

    @Test
    public void beginEndSession_simple(){
        SessionImpl session = new SessionImpl(ctx);
        final long timeGuardStart = System.nanoTime();

        session.begin();
        session.end();

        final long timeGuardEnd = System.nanoTime();
        final long timeGuardDiff = timeGuardEnd - timeGuardStart;

        final long sessionTimeStart = session.getBegan();
        final long sessionTimeEnd = session.getEnded();
        final long sessionTimeDiff = sessionTimeEnd - sessionTimeStart;

        Assert.assertEquals(true, sessionTimeDiff <= timeGuardDiff);
    }

    @Test
    public void secondIsCorrect() {
        long secondInMs = 1000 * 1000 * 1000;
        Assert.assertEquals((double)secondInMs, Device.NS_IN_SECOND);
    }

    @Test
    public void getBeganAndEnded() {
        long timeBegin = 123L;
        long timeEnd = 345L;
        SessionImpl session = new SessionImpl(ctx);
        session.begin(timeBegin);
        session.end(timeEnd, null);

        Assert.assertEquals(timeBegin, (long)session.getBegan());
        Assert.assertEquals(timeEnd, (long)session.getEnded());
    }

    @Test
    public void beginAfterEnd() throws MalformedURLException {
        TestingUtilityInternal.setupLogs(null);

        long timeBegin = 123L;
        long timeEnd = 345L;
        SessionImpl session = new SessionImpl(ctx);

        session.end();
        Assert.assertEquals(null, session.getBegan());
        Assert.assertEquals(null, session.getEnded());

        session.begin(timeBegin);
        session.end(timeEnd, null);

        Assert.assertEquals(timeBegin, (long)session.getBegan());
        Assert.assertEquals(timeEnd, (long)session.getEnded());
    }

    @Test
    public void beginAfterEndSet() throws MalformedURLException {
        TestingUtilityInternal.setupLogs(null);

        long timeEnd = 345L;
        SessionImpl session = new SessionImpl(ctx);

        session.ended = timeEnd;
        session.begin();

        Assert.assertEquals(null, session.getBegan());
        Assert.assertEquals(timeEnd, (long) session.getEnded());
    }

    @Test
    public void endedTwice() throws MalformedURLException {
        TestingUtilityInternal.setupLogs(null);
        long timeBegin = 123L;
        long timeEnd = 345L;
        long timeEndSecond = 678L;
        SessionImpl session = new SessionImpl(ctx);

        Assert.assertEquals(null, session.getBegan());
        Assert.assertEquals(null, session.getEnded());

        session.begin(timeBegin);

        Assert.assertEquals(timeBegin, (long) session.getBegan());
        Assert.assertEquals(null, session.getEnded());

        session.end(timeEnd, null);

        Assert.assertEquals(timeBegin, (long)session.getBegan());
        Assert.assertEquals(timeEnd, (long)session.getEnded());

        session.end(timeEndSecond, null);

        Assert.assertEquals(timeBegin, (long)session.getBegan());
        Assert.assertEquals(timeEnd, (long)session.getEnded());
    }

    @Test
    public void updateDuration_simple() throws Exception{
        int beginOffsetSeconds = 4;
        long timeBeginOffset = (long) (Device.NS_IN_SECOND * beginOffsetSeconds);

        SessionImpl session = new SessionImpl(ctx);
        final long timeGuardBase = System.nanoTime() - timeBeginOffset;
        session.begin(timeGuardBase);

        final long timeGuardStart = System.nanoTime();
        Long duration = Whitebox.<Long>invokeMethod(session, "updateDuration", null);
        final long timeGuardEnd = System.nanoTime();

        final long timeGuardDurStart = Math.round((timeGuardStart - timeGuardBase) / Device.NS_IN_SECOND);
        final long timeGuardDurEnded = Math.round((timeGuardEnd - timeGuardBase) / Device.NS_IN_SECOND);

        Assert.assertEquals(true, duration >= timeGuardDurStart);
        Assert.assertEquals(true, duration <= timeGuardDurEnded);
    }

    @Test
    public void updateDuration_notFirstUpdate() throws Exception{
        int beginOffsetSeconds = 12;
        long timeBeginOffset = (long) (Device.NS_IN_SECOND * beginOffsetSeconds);

        final long timeGuardBase = System.nanoTime() - timeBeginOffset;
        SessionImpl session = new SessionImpl(ctx);
        session.begin();
        Whitebox.setInternalState(session, "updated", timeGuardBase);

        final long timeGuardStart = System.nanoTime();
        Long duration = Whitebox.<Long>invokeMethod(session, "updateDuration", null);
        final long timeGuardEnd = System.nanoTime();

        final long timeGuardDurStart = Math.round((timeGuardStart - timeGuardBase) / Device.NS_IN_SECOND);
        final long timeGuardDurEnded = Math.round((timeGuardEnd - timeGuardBase) / Device.NS_IN_SECOND);

        Assert.assertEquals(true, duration >= timeGuardDurStart);
        Assert.assertEquals(true, duration <= timeGuardDurEnded);
    }

    @Test
    public void beginEndSession_simpleWithWait() throws InterruptedException{
        long sleepTimeMs = 500;

        SessionImpl session = new SessionImpl(ctx);
        final long timeGuardStart = System.nanoTime();

        session.begin();
        Thread.sleep(sleepTimeMs);
        session.end();

        final long timeGuardEnd = System.nanoTime();
        final long timeGuardDiff = timeGuardEnd - timeGuardStart;

        final long sessionTimeStart = session.getBegan();
        final long sessionTimeEnd = session.getEnded();
        final long sessionTimeDiff = sessionTimeEnd - sessionTimeStart;

        Assert.assertEquals(true, sessionTimeDiff >= (sleepTimeMs * 1000));
        Assert.assertEquals(true, sessionTimeDiff <= timeGuardDiff);
    }


    @Test
    public void update_simple() throws MalformedURLException {
        TestingUtilityInternal.setupLogs(null);
        long timeBegin = 123L;
        long timeEnd = 345L;
        SessionImpl session = new SessionImpl(ctx);

        session.update();

        Assert.assertEquals(null, session.getBegan());
        Assert.assertEquals(null, session.getEnded());

        session.begin(timeBegin);
        session.update();

        Assert.assertEquals(timeBegin, (long) session.getBegan());
        Assert.assertEquals(null, session.getEnded());

        session.end(timeEnd, null);
        session.update();

        Assert.assertEquals(timeBegin, (long)session.getBegan());
        Assert.assertEquals(timeEnd, (long)session.getEnded());
    }

    @Test
    public void isLeading_null() throws MalformedURLException{
        Core core = TestingUtilityInternal.setupBasicCore(getContext());
        SessionImpl session = new SessionImpl(ctx);
        Assert.assertEquals(false, (boolean)session.isLeading());
    }
    @Test
    public void isLeading_succeed() throws MalformedURLException{
        Core core = TestingUtilityInternal.setupBasicCore(getContext());
        List<SessionImpl> sessions = Whitebox.<List<SessionImpl>>getInternalState(core, "sessions");
        SessionImpl sessionTarget = new SessionImpl(ctx, 123L);
        sessions.add(sessionTarget);
        Assert.assertEquals(true, (boolean)sessionTarget.isLeading());
    }
    @Test
    public void isLeading_fail() throws MalformedURLException{
        Core core = TestingUtilityInternal.setupBasicCore(getContext());
        List<SessionImpl> sessions = Whitebox.<List<SessionImpl>>getInternalState(core, "sessions");

        SessionImpl sessionOther = new SessionImpl(ctx, 456L);
        SessionImpl sessionTarget = new SessionImpl(ctx, 123L);
        sessions.add(sessionOther);
        Assert.assertEquals(false, (boolean)sessionTarget.isLeading());
    }


    public void storeRestore_simple() throws InterruptedException {
        SessionImpl session = new SessionImpl(ctx);
        byte[] data = session.store();
        Assert.assertNotNull(data);
        SessionImpl restored = new SessionImpl(ctx, session.id);
        Assert.assertTrue(restored.restore(data));
        Assert.assertEquals(session, restored);
    }

    @Test
    public void storeRestore_full() throws InterruptedException {
        SessionImpl session = new SessionImpl(ctx);
        session.begin().event("key1")
                .setCount(2)
                .setDuration(3)
                .setSum(4)
                .addSegment("k1", "v1")
                .addSegment("k2", "v2")
                .record();

        session.event("key2")
                .setCount(2)
                .setDuration(3)
                .setSum(4)
                .addSegment("k1", "v1")
                .addSegment("k2", "v2")
                .record();

        Thread.sleep(100);
        session.update();

        session.addParam("param1", 1)
                .addParam("param2", "value 2");

        Thread.sleep(100);
        session.end();

        byte[] data = session.store();

        Assert.assertNotNull(data);
        SessionImpl restored = new SessionImpl(ctx, session.id);

        Assert.assertTrue(restored.restore(data));
        Assert.assertEquals(session, restored);
    }

    @Test
    public void autostore() throws InterruptedException, MalformedURLException {
        Core core = Core.initForApplication(new InternalConfig(new Config("http://count.ly/tests", "123")).setLoggerClass(Log.SystemLogger.class), getContext());
        core.onLimitedContextAcquired(getContext());

        SessionImpl session = new SessionImpl(ctx);
        session.begin().event("key1")
                .setCount(2)
                .setDuration(3)
                .setSum(4)
                .addSegment("k1", "v1")
                .addSegment("k2", "v2")
                .record();

        List<Long> ids = Storage.list(ctx, session.storagePrefix());
        Assert.assertNotNull(ids);
        Assert.assertTrue(ids.contains(session.storageId()));

        SessionImpl restored = Storage.pop(ctx, new SessionImpl(ctx, session.getId()));
        Assert.assertNotNull(restored);
        Assert.assertEquals(restored, session);

        // restored is updated and stored, while session has updated = null
        restored.update();

        restored = Storage.pop(ctx, new SessionImpl(ctx, session.getId()));
        Assert.assertNotNull(restored);
        Assert.assertFalse(restored.equals(session));
    }

    @Test
    public void session_lifecycle() throws InterruptedException {
        SessionImpl session = new SessionImpl(ctx);
        session.begin();
        Long began = Whitebox.getInternalState(session, "began");
        Assert.assertNotNull(began);
        Assert.assertNull(Whitebox.getInternalState(session, "updated"));
        Assert.assertNull(Whitebox.getInternalState(session, "ended"));

        session.begin();
        Assert.assertEquals(began, Whitebox.getInternalState(session, "began"));
        Assert.assertNull(Whitebox.getInternalState(session, "updated"));
        Assert.assertNull(Whitebox.getInternalState(session, "ended"));
        began = Whitebox.getInternalState(session, "began");

        session.update();
        Assert.assertNotNull(Whitebox.getInternalState(session, "began"));
        Assert.assertNotNull(Whitebox.getInternalState(session, "updated"));
        Assert.assertNull(Whitebox.getInternalState(session, "ended"));

        Long updated = Whitebox.getInternalState(session, "updated");
        Assert.assertTrue(began < updated);

        session.update();
        Assert.assertNotNull(Whitebox.getInternalState(session, "began"));
        Assert.assertNotNull(Whitebox.getInternalState(session, "updated"));
        Assert.assertTrue(updated < (long)Whitebox.getInternalState(session, "updated"));
        Assert.assertNull(Whitebox.getInternalState(session, "ended"));
        updated = Whitebox.getInternalState(session, "updated");

        session.end();
        Assert.assertNotNull(Whitebox.getInternalState(session, "began"));
        Assert.assertNotNull(Whitebox.getInternalState(session, "updated"));
        Assert.assertNotNull(Whitebox.getInternalState(session, "ended"));
        Assert.assertTrue(updated < (long)Whitebox.getInternalState(session, "ended"));
        Assert.assertTrue(began < (long)Whitebox.getInternalState(session, "ended"));
        Assert.assertTrue(began < updated);
    }

    @Test
    public void session_updateBeforeBegin() throws InterruptedException {
        SessionImpl session = new SessionImpl(ctx);
        session.update();
        Assert.assertNull(Whitebox.getInternalState(session, "began"));
        Assert.assertNull(Whitebox.getInternalState(session, "updated"));
        Assert.assertNull(Whitebox.getInternalState(session, "ended"));
    }

    @Test
    public void session_endBeforeBegin() throws InterruptedException {
        SessionImpl session = new SessionImpl(ctx);
        session.end();
        Assert.assertNull(Whitebox.getInternalState(session, "began"));
        Assert.assertNull(Whitebox.getInternalState(session, "updated"));
        Assert.assertNull(Whitebox.getInternalState(session, "ended"));
    }

    @Test
    public void session_recoverNoUpdate() throws Exception {
        Core.purgeInternalStorage(ctx, null);

        SessionImpl session = new SessionImpl(ctx);
        session.begin(System.nanoTime() - Device.nsToSec(20));

        Thread.sleep(300);

        session = Storage.read(ctx, session);
        Assert.assertNotNull(session);

        session.recover(new InternalConfig(TestingUtilityInternal.setupConfig()));
        Thread.sleep(300);

        session = Storage.read(ctx, session);
        Assert.assertNull(session);

        List<Long> requests = Storage.list(ctx, Request.getStoragePrefix());
        Assert.assertEquals(2, requests.size());

        Request begin = Storage.read(ctx, new Request(requests.get(0)));
        Request end = Storage.read(ctx, new Request(requests.get(1)));

        Assert.assertNotNull(begin);
        Assert.assertNotNull(end);
        Assert.assertTrue(begin.params.toString().contains("begin_session"));
        Assert.assertTrue(end.params.toString().contains("end_session"));
    }

    @Test
    public void session_recoverWithUpdate() throws Exception {
        Core.purgeInternalStorage(ctx, null);

        SessionImpl session = new SessionImpl(ctx);
        session.begin(System.nanoTime() - Device.secToNs(20));
        session.update(System.nanoTime() - Device.secToNs(10));

        Thread.sleep(300);

        session = Storage.read(ctx, session);
        Assert.assertNotNull(session);

        session.recover(new InternalConfig(TestingUtilityInternal.setupConfig()));
        Thread.sleep(300);

        session = Storage.read(ctx, session);
        Assert.assertNull(session);

        List<Long> requests = Storage.list(ctx, Request.getStoragePrefix());
        Assert.assertEquals(3, requests.size());

        Request begin = Storage.read(ctx, new Request(requests.get(0)));
        Request update = Storage.read(ctx, new Request(requests.get(1)));
        Request end = Storage.read(ctx, new Request(requests.get(2)));

        Assert.assertNotNull(begin);
        Assert.assertNotNull(update);
        Assert.assertNotNull(end);
        Assert.assertTrue(begin.params.toString().contains("begin_session"));
        Assert.assertTrue(update.params.toString().contains("session_duration"));
        Assert.assertTrue(end.params.toString().contains("end_session"));
    }

    @Test
    public void session_recoversNothingWithEnd() throws Exception {
        Core.purgeInternalStorage(ctx, null);

        SessionImpl session = new SessionImpl(ctx);
        session.begin(System.nanoTime() - Device.secToNs(20));
        session.update(System.nanoTime() - Device.secToNs(10));
        session.end();

        Storage.await();

        Assert.assertNull(Storage.read(ctx, session));

        session.recover(new InternalConfig(TestingUtilityInternal.setupConfig()));
        Storage.await();

        session = Storage.read(ctx, session);
        Assert.assertNull(session);

        List<Long> requests = Storage.list(ctx, Request.getStoragePrefix());
        Assert.assertEquals(3, requests.size());

        Request begin = Storage.read(ctx, new Request(requests.get(0)));
        Request update = Storage.read(ctx, new Request(requests.get(1)));
        Request end = Storage.read(ctx, new Request(requests.get(2)));

        Assert.assertNotNull(begin);
        Assert.assertNotNull(update);
        Assert.assertNotNull(end);
        Assert.assertTrue(begin.params.toString().contains("begin_session"));
        Assert.assertTrue(update.params.toString().contains("session_duration"));
        Assert.assertTrue(end.params.toString().contains("end_session"));
    }

}