package com.epam.feeder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.epam.common.Person;
import com.epam.common.Product;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.SpaceInterruptedException;
import org.openspaces.core.context.GigaSpaceContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * A feeder bean starts a scheduled task that writes a new Person objects to the
 * space (in an unprocessed state).
 * <p/>
 * <p/>
 * The space is injected into this bean using OpenSpaces support for @GigaSpaceContext
 * annotation.
 * <p/>
 * <p/>
 * The scheduling uses the java.util.concurrent Scheduled Executor Service. It
 * is started and stopped based on Spring lifecycle events.
 */
public class Feeder implements InitializingBean, DisposableBean {

    Logger log = Logger.getLogger(this.getClass().getName());

    private ScheduledExecutorService executorService;

    private ScheduledFuture<?> sf;

    private long defaultDelay = 1000;

    private FeederTask feederTask;

    @GigaSpaceContext
    private GigaSpace gigaSpace;

    public void setDefaultDelay(long defaultDelay) {
        this.defaultDelay = defaultDelay;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("--- STARTING FEEDER WITH CYCLE [" + defaultDelay + "]");
        executorService = Executors.newScheduledThreadPool(1);
        feederTask = new FeederTask();
        sf = executorService.scheduleAtFixedRate(feederTask, defaultDelay, defaultDelay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() throws Exception {
        sf.cancel(false);
        sf = null;
        executorService.shutdown();
    }

    public long getFeedCount() {
        return feederTask.getCounter();
    }

    public class FeederTask implements Runnable {

        private int counter = 1;

        @Override
        public void run() {
            try {
                long time = System.currentTimeMillis();
                Person person = new Person((counter++ % 100), "NAME " + Long.toString(time));
                gigaSpace.write(person);

                Product product = new Product()
                        .setCatalogNumber("cn#" + counter)
                        .setName("Name" + counter)
                        .setPrice((float) Math.random() * 100);
                gigaSpace.write(product);

                log.info("--- FEEDER WROTE " + person);
                log.info("--- FEEDER WROTE " + product);
            } catch (SpaceInterruptedException e) {
                // ignore, we are being shutdown
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public long getCounter() {
            return counter;
        }
    }

}
