package org.uant.textservice.communication;

import org.uant.textservice.db.ResourceDriver;
import org.uant.textservice.db.ResourceDb;
import org.uant.textservice.db.MessageDriver;
import org.uant.textservice.db.MessageDb;
import org.uant.textservice.logic.ProcessResponse;
import org.uant.textservice.db.DataSourceFactory;
import org.uant.textservice.db.MessageDBO;

import java.sql.*;
import javax.sql.DataSource;

import java.lang.Thread;
import java.lang.Runnable;
import java.util.concurrent.BlockingQueue;
import java.lang.InterruptedException;

public class MessageProcessor {
    private MessageDriver msgDb;
    private ResourceDriver resourceDb;
    private DataSource ds;
    private ProcessResponse pr;
    private final BlockingQueue<Integer> processMsgPipe;
    private final BlockingQueue<Integer> sendMsgPipe;
    private MessageDBO record;

    public MessageProcessor(BlockingQueue<Integer> processMsgPipe, BlockingQueue<Integer> sendMsgPipe, String dbProps) {
        this. ds = DataSourceFactory.getMySQLDataSource(dbProps);
        this.resourceDb = new ResourceDb(ds);
        this.msgDb = new MessageDb(ds);
        this.pr = new ProcessResponse(resourceDb, msgDb);
        this.processMsgPipe = processMsgPipe;
        this.sendMsgPipe = sendMsgPipe;
    }

    public void start() {
        new Thread(new Runnable() {
            public void run() {
                int newMsgHash;
                while (true) {
                    //TODO would we ever want to break out of here?

                    try {
                        newMsgHash = processMsgPipe.take();

                        processMessage(newMsgHash);

                        sendMsgPipe.put(newMsgHash);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void processMessage(int newMsgHash) {
        this.record = this.pr.processMessageResponse(newMsgHash);
        this.msgDb.updateMessage(this.record);
    }
}
