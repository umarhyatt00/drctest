package com.yeel.drc.model.phonecontacts;

import java.util.List;

public class ContactPostModel {
    String user_id;
    String iat;
    List<ContactModel> contacts;

    public ContactPostModel(String user_id, String iat, List<ContactModel> contacts) {
        this.user_id = user_id;
        this.iat = iat;
        this.contacts = contacts;
    }
}
