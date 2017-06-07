package org.uant.textservice.main;

//for db conn
import org.uant.textservice.db.DataSourceFactory;

// generic message obj used to pass data thr system
import org.uant.textservice.db.MessageDBO;

// for generating messages
import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.ReceivedMessageHandler;
import org.uant.textservice.communication.MessageReceiver;

// for inserting messages
import org.uant.textservice.db.MessageDriver;
import org.uant.textservice.db.MessageDb;

// for processing messages
import org.uant.textservice.logic.ProcessResponse;
import org.uant.textservice.db.ResourceDriver;
import org.uant.textservice.db.ResourceDb;
import org.uant.textservice.communication.MessageProcessor;

// for sending messages
import org.uant.textservice.message.SendMessageHandler;
import org.uant.textservice.message.ReceivedMessageHandler;
import org.uant.textservice.mail.EmailMessageGetter;
import org.uant.textservice.mail.EmailMessageSender;
import org.uant.textservice.communication.MessageSender;

//for getting rid of old messages
import org.uant.textservice.db.MessageCleaner;

// for message passing
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TextService {
    /*
       public MessageReceiver(ReceivedMessageHandler messageHandler, BlockingQueue<Integer> processMsgPipe) {
       public MessageProcessor(BlockingQueue<Integer> processMsgPipe, BlockingQueue<Integer> sendMsgPipe) {
       public MessageSender(BlockingQueue<Integer> sendMsgPipe, SendMessageHandler msgSender) { //& BlockingQueue<Integer> deleteMsgPipe

       public EmailMessageGetter(String mailAuthProps, String imapProps) {
       public EmailMessageSender(String mailAuthProps, String smtpProps) {
       */

    MessageReceiver messageReceiver;
    MessageProcessor messageProcessor;
    MessageSender messageSender;
    MessageCleaner messageCleaner;

    ReceivedMessageHandler getMessageHandler;
    SendMessageHandler sendMessageHandler;

    BlockingQueue<Integer> processMsgPipe;
    BlockingQueue<Integer> sendMsgPipe;

    public TextService(){
        processMsgPipe = new LinkedBlockingQueue<Integer>();
        sendMsgPipe = new LinkedBlockingQueue<Integer>();

        getMessageHandler =
            new EmailMessageGetter("properties/mailAuth.properties",
                    "properties/imap.properties");
        sendMessageHandler =
            new EmailMessageSender("properties/mailAuth.properties",
                    "properties/smtp.properties");
        String dbProps = "properties/db.properties";
        messageReceiver = new MessageReceiver(getMessageHandler, processMsgPipe, dbProps);
        messageProcessor = new MessageProcessor(processMsgPipe, sendMsgPipe, dbProps);
        messageSender = new MessageSender(sendMsgPipe, sendMessageHandler, dbProps);
        messageCleaner = new MessageCleaner(dbProps);
    }

    public void run() {
        try {
            //TODO more robust tests for important errors thrown.
            messageReceiver.start();
            messageProcessor.start();
            messageSender.start();
            messageCleaner.start();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
       }
