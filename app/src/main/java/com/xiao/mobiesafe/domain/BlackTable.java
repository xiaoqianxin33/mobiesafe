package com.xiao.mobiesafe.domain;

public interface BlackTable {
    String PHONE = "phone";
    String MODE = "mode";
    String BLACKTABLE = "blacktb";

    int SMS = 1 << 0;
    int TEL = 1 << 1;
    int ALL = SMS | TEL;

}
