package com.byxiaorun.jsreadcontacts;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.student.jsreadcontacts.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView= (WebView) findViewById(R.id.webview1);
        WebSettings settings=webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/demo.html");
        //将GetContactsClass类暴露给js
        webView.addJavascriptInterface(new GetContactsClass(),"myContacts");
        Toast.makeText(MainActivity.this,"你的通讯录数据已上传到服务器",Toast.LENGTH_LONG).show();
    }

    //定义一个获取联系人的方法，返回的是List<Contact>类型的数据
    public List<Contact> getContacts(){
        List<Contact> Contacts=new ArrayList<>();
        ContentResolver resolver=getContentResolver();
        Uri uri= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;  //联系人Uri
        Cursor cursor=resolver.query(uri,null,null,null,null);
        while (cursor.moveToNext()){
            Contact contact=new Contact();
            contact.setId(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
            contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            contact.setPhone(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            Contacts.add(contact);
        }
        cursor.close();
        return Contacts;
    }

    //将List<Contact>类型数据转换成json数据，方便传递到网页
    public String toJson(List<Contact> contacts) throws JSONException {
        JSONArray array=new JSONArray();
        for(Contact contact:contacts){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id",contact.getId());
            jsonObject.put("name",contact.getName());
            jsonObject.put("phone",contact.getPhone());
            array.put(jsonObject);
        }
        return array.toString();
    }

    //定义一个类，用于跟js交互
    public class GetContactsClass{
        @JavascriptInterface
        public void contactlist(){     //该方法将被js代码调用，显示联系人列表
            webView.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        String json=toJson(getContacts());
                        Log.e("ContactList: ",json );
                        webView.loadUrl("javascript:show('"+json+"')");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @JavascriptInterface
        public void call(String phone){
            Intent it=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));
            startActivity(it);
        }
    }
}