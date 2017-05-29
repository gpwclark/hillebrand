package org.uant.textservice.communication;

import org.uant.textservice.db.DataSourceFactory;
import org.uant.textservice.db.MessageDriver;
import org.uant.textservice.db.MessageDBO;
import org.uant.textservice.db.MessageDb;

import org.uant.textservice.message.ReceivedMessage;
import java.util.UUID;

import java.sql.*;
import javax.sql.DataSource;

import java.lang.Thread;
import java.lang.Runnable;
import java.util.concurrent.BlockingQueue;
import java.lang.InterruptedException;

public class MessageReceiver  {
    private MessageDriver msgDb;
    private DataSource ds;
    private final BlockingQueue<ReceivedMessage> newMsgPipe;
    private final BlockingQueue<Integer> processMsgPipe;

    public MessageReceiver(BlockingQueue<ReceivedMessage> newMsgPipe, BlockingQueue<Integer> processMsgPipe) {
        this.ds = DataSourceFactory.getMySQLDataSource();
        this.msgDb = new MessageDb(this.ds);
        this.newMsgPipe = newMsgPipe;
        this.processMsgPipe = processMsgPipe;
    }

    public void start() {
        new Thread(new Runnable() {
            public void run() {
                ReceivedMessage newMsg;
                while (true) {
                    //TODO would we ever want to break out of here?

                    try {
                        newMsg = newMsgPipe.take();

                        receiveMessage(newMsg);

                        processMsgPipe.put(newMsg.hash);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void receiveMessage(ReceivedMessage newMsg) {
        this.msgDb.insertMessage(newMsg);
    }
}
