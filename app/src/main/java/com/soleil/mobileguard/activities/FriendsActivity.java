package com.soleil.mobileguard.activities;

import com.soleil.mobileguard.domain.Contact;
import com.soleil.mobileguard.engine.ReadContactsEngine;

import java.util.List;

public class FriendsActivity extends BaseFriendsCallSmsActivity {


    @Override
    public List<Contact> getDatas() {

        return ReadContactsEngine.readContacts(getApplicationContext());
    }

}

