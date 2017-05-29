package org.uant.textservice.communication;

import org.uant.textservice.db.MessageDriver;
import org.uant.textservice.db.MessageDb;
import org.uant.textservice.message.SendMessageHandler;
import org.uant.textservice.message.MockMessageSender;
import org.uant.textservice.db.DataSourceFactory;
import org.uant.textservice.db.MessageDBO;

import javax.sql.DataSource;
import java.sql.*;

import java.lang.Thread;
import java.lang.Runnable;
import java.util.concurrent.BlockingQueue;
import java.lang.InterruptedException;

public class MessageSender {
    private SendMessageHandler msgSender;
    private MessageDriver msgDb;
    private DataSource ds;
    private final BlockingQueue<Integer> sendMsgPipe;
    private MessageDBO record;
    //private final BlockingQueue<Integer> deleteMsgPipe;

    public MessageSender(BlockingQueue<Integer> sendMsgPipe) { //& BlockingQueue<Integer> deleteMsgPipe
        this.ds = DataSourceFactory.getMySQLDataSource();
        this.msgDb = new MessageDb(ds);
        this.msgSender = new MockMessageSender(msgDb);
        this.sendMsgPipe = sendMsgPipe;
    }

    public void start() {
        new Thread(new Runnable() {
            public void run() {
                int newMsgHash;
                while (true) {
                    //TODO would we ever want to break out of here?

                    try {
                        newMsgHash = sendMsgPipe.take();

                        sendMessage(newMsgHash);
                        // TODO remove DB dep, this should truly be a dummy
                        // function, we can handle the sent=true call here,
                        // do this after you do the test cases.

                        //deleteMsgPipe.put(newMsgHash);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void sendMessage(int newMsgHash){
        this.record = this.msgDb.getMessage(newMsgHash);
        this.record = this.msgSender.sendMessage(record);
        this.record.setSent(true);
        msgDb.updateMessage(this.record);
    }
}
