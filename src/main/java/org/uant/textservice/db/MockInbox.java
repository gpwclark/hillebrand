package org.uant.textservice.db;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.db.InboxHandler;
import org.uant.textservice.db.MessageDBO;

import javax.sql.DataSource;
import java.sql.*;

import java.util.Map;
import java.util.HashMap;

/*
 *
 */

public class MockInbox implements InboxHandler {
    Map<Integer, Map> db = new HashMap<Integer, Map>();
    DataSource ds;

    public MockInbox(DataSource ds) {
        this.ds = ds;
    }

    public void insertMessage(ReceivedMessage msg) {
        MessageDBO msgDBO = new MessageDBO(msg.hash, msg.sender, msg.body, null, false, false, false, false, msg.timestamp);
        insertMessage(msgDBO);
    }

    public void insertMessage(MessageDBO msgDBO){
        String insert = "INSERT INTO messages"
        + "(hash, sender, body, response, validResource, validRequest, sent, processed, timestamp)"
        + "VALUES"
        + "?, ?, ?, ?, ?, ?, ?, ?, ?";

        try (
                Connection conn = ds.getConnection();
                PreparedStatement statement = conn.prepareStatement(insert);
            ) {
            statement.setInt(1, msgDBO.getHash());
            statement.setString(2,msgDBO.getSender());
            statement.setString(3, msgDBO.getBody());
            statement.setString(4, msgDBO.getResponse());
            statement.setBoolean(5, msgDBO.getValidResource());
            statement.setBoolean(6, msgDBO.getValidRequest());
            statement.setBoolean(7, msgDBO.getSent());
            statement.setBoolean(8, msgDBO.getProcessed());
            statement.setLong(9, msgDBO.getTimestamp());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMessage(MessageDBO msgDBO){
        try (
                Connection conn = ds.getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM messages WHERE hash=?");
            ) {
            statement.setInt(1, msgDBO.getHash());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // this should always match the getMessage with a different signature;
                    rs.updateInt("hash", msgDBO.getHash());
                    rs.updateString("sender" ,msgDBO.getSender());
                    rs.updateString("body", msgDBO.getBody());
                    rs.updateString("response", msgDBO.getResponse());
                    rs.updateBoolean("validResource", msgDBO.getValidResource());
                    rs.updateBoolean("validRequest", msgDBO.getValidRequest());
                    rs.updateBoolean("sent", msgDBO.getSent());
                    rs.updateBoolean("processed", msgDBO.getProcessed());
                    rs.updateLong("timestamp", msgDBO.getTimestamp());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public MessageDBO getMessage(int hash){
        MessageDBO msgDBO = new MessageDBO();
        try (
                Connection conn = ds.getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM messages WHERE hash=?");
            ) {
            statement.setInt(1, hash);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // this should always match the getMessage with a different signature;
                    msgDBO.setHash(rs.getInt("hash"));
                    msgDBO.setSender(rs.getString("sender"));
                    msgDBO.setBody(rs.getString("body"));
                    msgDBO.setResponse(rs.getString("response"));
                    msgDBO.setValidResource(rs.getBoolean("validResource"));
                    msgDBO.setValidRequest(rs.getBoolean("validRequest"));
                    msgDBO.setSent(rs.getBoolean("sent"));
                    msgDBO.setProcessed(rs.getBoolean("processed"));
                    msgDBO.setTimestamp(rs.getLong("timestamp"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msgDBO;
    }
    public MessageDBO getMessage(long id){
        MessageDBO msgDBO = new MessageDBO();
        try (
                Connection conn = ds.getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM messages WHERE id=?");
            ) {
            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // this should always match the getMessage with a different signature;
                    msgDBO.setHash(rs.getInt("hash"));
                    msgDBO.setSender(rs.getString("sender"));
                    msgDBO.setBody(rs.getString("body"));
                    msgDBO.setResponse(rs.getString("response"));
                    msgDBO.setValidResource(rs.getBoolean("validResource"));
                    msgDBO.setValidRequest(rs.getBoolean("validRequest"));
                    msgDBO.setSent(rs.getBoolean("sent"));
                    msgDBO.setProcessed(rs.getBoolean("processed"));
                    msgDBO.setTimestamp(rs.getLong("timestamp"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msgDBO;
    }
}
