package com.xiao.mobiesafe.domain;

import android.net.Uri;

public interface LockTable {
    String PACKNAME = "packname";
    String LOCKTABLE = "locktb";

    Uri uri = Uri.parse("content://xiao/locked");
}
