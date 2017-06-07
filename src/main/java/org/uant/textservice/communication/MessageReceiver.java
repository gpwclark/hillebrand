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
import java.util.ArrayList;

public class MessageReceiver  {
    private MessageDriver msgDb;
    private DataSource ds;
    private ReceivedMessageHandler messageHandler;
    private final BlockingQueue<Integer> processMsgPipe;
    private ArrayList<ReceivedMessage> newMsgs = new ArrayList<ReceivedMessage>();
    private ArrayList<Integer> newMsgHashes = new ArrayList<Integer>();

    public MessageReceiver(ReceivedMessageHandler messageHandler, BlockingQueue<Integer> processMsgPipe, String dbProps) {
        this.ds = DataSourceFactory.getMySQLDataSource(dbProps);
        this.msgDb = new MessageDb(this.ds);
        this.processMsgPipe = processMsgPipe;
        this.messageHandler = messageHandler;
    }

    public void start() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    //TODO would we ever want to break out of here?
                    // poison pill?
                    // http://web.mit.edu/6.005/www/fa14/classes/20-queues-locks/message-passing/
                    try {
                        Thread.sleep(10000);
                        //TODO make LL
                        newMsgs.clear();
                        newMsgHashes.clear();
                        receiveMessages();

                        for (Integer newMsgHash : newMsgHashes){
                            processMsgPipe.put(newMsgHash);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public ArrayList<Integer> receiveMessages() {
        newMsgs = this.messageHandler.getMessages();
        for (ReceivedMessage msg : newMsgs) {
            msgDb.insertMessage(msg);
            newMsgHashes.add(msg.hash);
        }
        return newMsgHashes;
    }
}
