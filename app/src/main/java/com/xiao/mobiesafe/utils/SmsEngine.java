package com.xiao.mobiesafe.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class SmsEngine {

    public static void backupSms(final Activity context, final ProgressBar pb) {

        new Thread() {

            @Override
            public void run() {
                Uri uri = Uri.parse("content://sms");

                final Cursor cursor = context.getContentResolver().query(uri, new String[]{"address", "date", "body", "type"},
                        null, null, " _id desc");
                if (cursor != null) {

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setMax(cursor.getCount());

                            pb.setVisibility(View.VISIBLE);
                        }
                    });

                    int progress = 0;

                    File file = new File(Environment.getExternalStorageDirectory(),
                            "sms.json");

                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        PrintWriter out = new PrintWriter(fos);

                        out.println("{\"count\":\"" + cursor.getCount() + "\"");
                        out.println(",\"smses\":[");
                        while (cursor.moveToNext()) {
                            progress++;

                            SystemClock.sleep(100);

                            if (cursor.getPosition() == 0) {
                                out.println("{");
                            } else {
                                out.println(",{");
                            }

                            String body = cursor.getString(2);
                            body = JsonUtils.string2Json(body);
                            body = EncryptTools.encrypt(body);

                            out.println("\"address\":\"" + cursor.getString(0)
                                    + "\",");
                            out.println("\"date\":\"" + cursor.getString(1) + "\",");
                            out.println("\"body\":\"" + body + "\",");
                            out.println("\"type\":\"" + cursor.getString(3) + "\"");

                            out.println("}");
                            pb.setProgress(progress);
                        }

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setVisibility(View.GONE);
                            }
                        });

                        out.println("]}");

                        out.flush();
                        out.close();
                        cursor.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "没有需要备份的短信", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }.start();
    }

    public static void smsResumnJson(final Activity context, final ProgressBar pb) {

        new Thread() {
            @Override
            public void run() {

                Uri uri = Uri.parse("content://sms");
                try {
                    FileInputStream fis = new FileInputStream(new File(
                            Environment.getExternalStorageDirectory(),
                            "sms.json"));

                    StringBuilder jsonSmsStr = new StringBuilder();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(fis));

                    String line = reader.readLine();
                    while (line != null) {
                        jsonSmsStr.append(line);
                        line = reader.readLine();
                    }

                    JSONObject jsonObj = new JSONObject(jsonSmsStr.toString());
                    final int counts = Integer.parseInt(jsonObj.getString("count"));

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setMax(counts);
                            pb.setVisibility(View.VISIBLE);
                        }
                    });


                    JSONArray jarray = (JSONArray) jsonObj.get("smses");
                    for (int i = 0; i < counts; i++) {

                        JSONObject smsjson = jarray.getJSONObject(i);
                        String body = smsjson.getString("body");
                        body=JsonUtils.json2String(EncryptTools.decryption(body));

                        ContentValues values = new ContentValues();
                        values.put("address", smsjson.getString("address"));
                        values.put("body", body);
                        values.put("date", smsjson.getString("date"));
                        values.put("type", smsjson.getString("type"));

                        context.getContentResolver().insert(uri, values);

                        pb.setProgress(i + 1);
                    }

                    reader.close();

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
