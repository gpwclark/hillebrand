package org.uant.textservice.db;

import org.uant.textservice.db.MessageDriver;
import org.uant.textservice.db.MessageDb;
import org.uant.textservice.db.DataSourceFactory;
import org.uant.textservice.db.MessageDBO;

import javax.sql.DataSource;
import java.sql.*;

import java.lang.Thread;
import java.lang.Runnable;
import java.util.concurrent.TimeUnit;
import java.lang.InterruptedException;
import java.util.Date;

public class MessageCleaner {
    private MessageDriver msgDb;
    private DataSource ds;
    private final int threadSleepTime;
    private final long oldMessageThreshold;
    Date date = new Date();
    long someTimeAgo;
    //private final BlockingQueue<Integer> deleteMsgPipe;

    public MessageCleaner(String dbProps) { //& BlockingQueue<Integer> deleteMsgPipe
        this.ds = DataSourceFactory.getMySQLDataSource(dbProps);
        this.msgDb = new MessageDb(ds);
        //TODO get config.properties to un-hardcode values
        this.threadSleepTime = 5000;
        this.oldMessageThreshold = TimeUnit.MINUTES.toMillis(2);
    }

    public void start() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    //TODO would we ever want to break out of here?
                    try {
                        Thread.sleep(threadSleepTime);
                        deleteOldMessages();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void deleteOldMessages(){
        someTimeAgo = date.getTime() - oldMessageThreshold;
        msgDb.deleteMessagesOlderThan(someTimeAgo);
    }
}
