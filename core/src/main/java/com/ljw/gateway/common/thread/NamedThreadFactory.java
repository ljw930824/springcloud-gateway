package com.ljw.gateway.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>类 名：</b>NamedThreadFactory<br/>
 * <b>类描述：</b>线程工厂类<br/>
 * <b>创建人：</b>jiaweiluo<br/>
 * <b>创建时间：</b>2019/7/28<br/>
 * <b>修改人：</b><br/>
 * <b>修改时间：</b><br/>
 * <b>修改备注：</b><br/>
 *
 * @version 1.0<br />
 */
public class NamedThreadFactory implements ThreadFactory {

    private boolean daemon;
    private final ThreadGroup group;
    private final String namePrefix;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public NamedThreadFactory(String name, boolean daemon) {
        this(name);
        this.daemon = daemon;
    }

    public NamedThreadFactory(String name) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "gateway-pool-" + name + "-thread-";
        daemon = Thread.currentThread().isDaemon();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        t.setDaemon(daemon);

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        
        return t;
    }

}