package org.uant.textservice.communication;

import org.uant.textservice.db.DataSourceFactory;
import org.uant.textservice.db.MessageDriver;
import org.uant.textservice.db.MessageDBO;
import org.uant.textservice.db.MessageDb;
import org.uant.textservice.message.ReceivedMessage;

import org.uant.textservice.message.ReceivedMessage;
import java.util.UUID;

import java.sql.*;
import javax.sql.DataSource;

import java.lang.Thread;
import java.lang.Runnable;
import java.util.concurrent.BlockingQueue;
import org.uant.textservice.message.ReceivedMessageHandler;
import java.lang.InterruptedException;

public class MessageReceiver  {
    private MessageDriver msgDb;
    private DataSource ds;
    private ReceivedMessageHandler messageHandler;
    private final BlockingQueue<Integer> processMsgPipe;

    public MessageReceiver(ReceivedMessageHandler messageHandler, BlockingQueue<Integer> processMsgPipe) {
        this.ds = DataSourceFactory.getMySQLDataSource();
        this.msgDb = new MessageDb(this.ds);
        this.processMsgPipe = processMsgPipe;
        this.messageHandler = messageHandler;
    }

    public void start() {
        new Thread(new Runnable() {
            public void run() {
                int newMsgHash;
                while (true) {
                    //TODO would we ever want to break out of here?
                    // poison pill?
                    // http://web.mit.edu/6.005/www/fa14/classes/20-queues-locks/message-passing/
                    try {
                        newMsgHash = receiveMessage();

                        processMsgPipe.put(newMsgHash);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public int receiveMessage() {
        ReceivedMessage newMsg = this.messageHandler.getMessage();
        this.msgDb.insertMessage(newMsg);
        return newMsg.hash;
    }
}
