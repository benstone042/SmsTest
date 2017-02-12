package com.sujityadav.smstest.Model;

/**
 * Created by sujit yadav on 2/10/2017.
 */

public class Conversations {
    String recipient_number;
    String message_count;
    String recipient_ids;
    String snippet;
    String address;
    String readcount, snippet_cs, type, error, has_attachment, status;
    Long date;
    int read;
    int _id;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRead() { return read; }

    public void setRead(int read) { this.read = read; }

    public Long getDate() { return date; }

    public void setDate(Long date) { this.date = date; }

    public String getRecipient_ids() { return recipient_ids; }

    public void setRecipient_ids(String recipient_ids) { this.recipient_ids = recipient_ids; }

    public String getMessage_count() { return message_count; }

    public void setMessage_count(String message_count) { this.message_count = message_count; }

    public String getRecipient_number() { return recipient_number; }

    public void setRecipient_number(String recipient_number) { this.recipient_number = recipient_number; }

    public String getSnippet() { return snippet; }

    public void setSnippet(String snippet) { this.snippet = snippet; }


}
