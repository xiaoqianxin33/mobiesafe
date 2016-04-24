package com.xiao.mobiesafe.activity;

import com.xiao.mobiesafe.utils.ReadContactEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactActivity extends BaseContactCallSmsActivity {

    public ArrayList<HashMap<String, String>> readContact() {
        return ReadContactEngine.readContact(this);
    }
}