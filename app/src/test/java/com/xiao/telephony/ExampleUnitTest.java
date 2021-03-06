package com.xiao.telephony;

import android.test.AndroidTestCase;

import com.xiao.mobiesafe.dao.BlackDao;
import com.xiao.mobiesafe.domain.BlackTable;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest extends AndroidTestCase {

    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    public void testAddBlackNumber(){
        BlackDao dao = new BlackDao(getContext());
        for (int i = 0; i < 200;i++){
            dao.add("1234567" + i, BlackTable.SMS);
        }
    }



}