package com.epam.feeder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.SpaceInterruptedException;
import org.openspaces.core.context.GigaSpaceContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;

/**
 * A feeder bean starts a scheduled task that writes a new Person objects to the
 * space (in an unprocessed state).
 * <p/>
 * <p/>
 * The space is injected into this bean using OpenSpaces support
 * for @GigaSpaceContext
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

    private String typeName = "Product";

    public void setDefaultDelay(long defaultDelay) {
        this.defaultDelay = defaultDelay;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("--- AFTER PROPERTIES SET CALLED");
        registerProductSpaceType();
        log.info("--- TYPE REGISTRATION COMPLETED");
        log.info("--- STARTING FEEDER WITH CYCLE [" + defaultDelay + "]");
        executorService = Executors.newScheduledThreadPool(1);
        feederTask = new FeederTask();
        sf = executorService.scheduleAtFixedRate(feederTask, defaultDelay, defaultDelay, TimeUnit.MILLISECONDS);
    }

    private void registerProductSpaceType() {
        if (gigaSpace.getTypeManager().getTypeDescriptor(typeName) != null) {
            log.info("--- TYPE '" + typeName + "' ALREADY REGISTERED, SKIPPING REGISTRATION");
            return;
        }
        com.gigaspaces.metadata.SpaceTypeDescriptorBuilder builder = new com.gigaspaces.metadata.SpaceTypeDescriptorBuilder(
                typeName)
                .idProperty("CatalogNumber", false)
                .addFixedProperty("CatalogNumber", String.class)
                .addFixedProperty("Name", String.class)
                .addFixedProperty("Price", Float.class)
                .addFixedProperty("zz_META_DI_TIMESTAMP", Long.class);

        gigaSpace.getTypeManager().registerTypeDescriptor(builder.create());
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
                log.info("[FeederTask] Instance: " + System.identityHashCode(this) + ", Thread: "
                        + Thread.currentThread().getName() + ", Counter before: " + counter);

                // Create Product using SpaceDocument API directly
                SpaceDocument product = new SpaceDocument(typeName);
                product.setProperty("CatalogNumber", "cn#" + counter);
                product.setProperty("Name", "Name" + counter);
                product.setProperty("Price", (float) Math.random() * 100);
                product.setProperty("zz_META_DI_TIMESTAMP", (counter % 2 == 0) ? time : null);

                log.info("--- FEEDER WRITING " + product);
                gigaSpace.write(product);

                log.info("--- FEEDER WROTE " + product);
                counter++;
                log.info("[FeederTask] Instance: " + System.identityHashCode(this) + ", Counter after: " + counter);
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
