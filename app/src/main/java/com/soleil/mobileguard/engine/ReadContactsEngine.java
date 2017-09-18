package com.soleil.mobileguard.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.soleil.mobileguard.domain.Contact;

import java.util.ArrayList;
import java.util.List;


/**
 * 对于没有任何读写权利的数据库
 * 我们有两种方法访问
 * Contentprovider/root
 */
public class ReadContactsEngine {

    /**
     * @param context
     * @return 读取短信记录中的联系人
     */
    public static List<Contact> readSmsLog(Context context) {
        List<Contact> list_Contacts = new ArrayList<>();
        //从联系人数据库中找到电话记录
        Uri uri = Uri.parse("content://sms");
        Uri uriDatas = Uri.parse("content://com.android.contacts/data");
        Cursor cursor1 = context.getContentResolver().query(uri, new String[]{"address"}, null, null, null);
        while (cursor1.moveToNext()) {
            Contact contact = new Contact();
            String phone = cursor1.getString(0);

            contact.setPhone(phone);

            Cursor cursor2 = context.getContentResolver().query(uriDatas, new String[]{"raw_contact_id"}, "data1=?", new String[]{phone}, null);

            while (cursor2.moveToNext()) {
                String raw_contact_id = cursor2.getString(0);

                Cursor cursor3 = context.getContentResolver().query(uriDatas, new String[]{"mimetype", "data1"}, "raw_contact_id=?", new String[]{raw_contact_id}, null);

                while (cursor3.moveToNext()) {
                    String mimetype = cursor3.getString(0);
                    String name = cursor3.getString(1);
                    if (mimetype.equals("vnd.android.cursor.item/name")) {

                        contact.setName(name);
                    }
                }

                cursor3.close();

            }
            cursor2.close();


            list_Contacts.remove(contact);
            list_Contacts.add(contact);


        }
        cursor1.close();
        return list_Contacts;
    }


    /**
     * @param context
     * @return 读取通话记录中的联系人
     */
    public static List<Contact> readCallLog(Context context) {
        List<Contact> list_Contacts = new ArrayList<>();
        //从联系人数据库中找到电话记录
        Uri uri = Uri.parse("content://call_log/calls");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"number", "name"}, null, null, null);
        while (cursor.moveToNext()) {
            Contact contact = new Contact();
            String phone = cursor.getString(0);
            String name = cursor.getString(1);

            contact.setName(name);
            contact.setPhone(phone);
            list_Contacts.remove(contact);
            list_Contacts.add(contact);


        }
        cursor.close();
        return list_Contacts;
    }

    /**
     * @param context
     * @return 读取联系人
     */
    public static List<Contact> readContacts(Context context) {
        List<Contact> list_Contacts = new ArrayList<>();
        Uri uriContacts = Uri.parse("content://com.android.contacts/contacts");
        Uri uriDatas = Uri.parse("content://com.android.contacts/data");
        Cursor cursor1 = context.getContentResolver().query(uriContacts, new String[]{"_id"}, null, null, null);
        while (cursor1.moveToNext()) {
            Contact contact = new Contact();
//            System.out.println(cursor.getString(0));
            String id = cursor1.getString(0);
            Cursor cursor2 = context.getContentResolver().query(uriDatas, new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{id}, null);
            while (cursor2.moveToNext()) {
                String data = cursor2.getString(0);
                String mimetype = cursor2.getString(1);

                if (mimetype.equals("vnd.android.cursor.item/name")) {
                    System.out.println("第" + id + "用户的名字：" + data);
                    contact.setName(data);
                } else {
                    System.out.println("第" + id + "用户的电话：" + data);
                    contact.setPhone(data);
                }
            }
            list_Contacts.add(contact);
            cursor2.close();
        }
        cursor1.close();
        return list_Contacts;
    }


}
