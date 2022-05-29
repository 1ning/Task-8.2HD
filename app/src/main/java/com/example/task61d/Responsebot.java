package com.example.task61d;

import static com.example.task61d.Account.getBitmap;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.task61d.Database.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Responsebot extends AppCompatActivity {

    private ListView listView;

    //Chat list adapter object
    private ChatAdapter adapter;
    // A collection of all chat data
    private List<ChatBean> chatBeanList;
    // Send box
    private EditText et_send_msg;
    // Send button
    private Button btn_send;
    //Website and API key
    private static final String WEB_SITE = "http://openapi.turingapi.com/openapi/api/v2";
    private static final String KEY = "a5a6676ff6ae445b8b7d2a768f913bc3";
    // Messages sent
    private String sendMsg;
    private String sendMsg2;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    //Arrays to store welcome information
    private String[] welcome;
    //Objects that capture events
    private MHandler mHandler;
    //Access to data
    private static final int MSG_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responsebot);
        chatBeanList = new ArrayList<ChatBean>();
        mHandler = new MHandler();
        //Initialisation of welcome
        welcome = new String[5];
        welcome[0]="Life is a landscape, happiness is a mood. Welcome to our shop, I am your dedicated customer service, how can I help you?";
        welcome[1]="Master, how may I help you?";
        welcome[2]="Hi, it's been a long time coming. What can I do for you?";
        welcome[3]="The kite has the wind, the dolphin has the sea , and you have me, thank you for visiting.";
        welcome[4]="Dear, you can describe your problem in detail and we will try our best to solve it for you.";
        //Get the user's avatar from the database
        dbHelper = new DatabaseHelper(this, "database", null, 1);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("user", null, "id=?", new String[]{String.valueOf(Global.id)}, null, null, null);
        cursor.moveToFirst();
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(Responsebot.this, "please try again", Toast.LENGTH_SHORT).show();
        } else {
            byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow("photo"));
            Global.photo1 = getBitmap(photo);
        }
        initView();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                Intent intent3=new Intent(Responsebot.this,Home.class);
                startActivity(intent3);
                break;
            case R.id.account:
                Intent intent=new Intent(Responsebot.this,Account.class);
                startActivity(intent);
                break;
            case R.id.order:
                Intent intent2=new Intent(Responsebot.this,Myorder.class);
                startActivity(intent2);
            case R.id.help:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    public void initView() {
        listView = (ListView) findViewById(R.id.list);
        et_send_msg = (EditText) findViewById(R.id.et_send_msg);
        btn_send = (Button) findViewById(R.id.btn_send);
        adapter = new ChatAdapter(chatBeanList, this);
        listView.setAdapter(adapter);
        //Adding a click listener for sending keys
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click the Send button to send the message
                sendData();
            }
        });


        //Keyboard Listening: send a message when the enter key is pressed on the keyboard
        et_send_msg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendData();
                }
                return false;
            }
        });

        // Get a random number
        int position = (int) (Math.random() * welcome.length - 1);

        // Get a bot's welcome message with a random number
        showData(welcome[position]);
    }

    //Send a message
    private void sendData() {
        // Get the input string
        sendMsg = et_send_msg.getText().toString();
        sendMsg2=sendMsg;
        //Determine if it is empty
        if (TextUtils.isEmpty(sendMsg)) {
            Toast.makeText(this, "You haven't entered any information yet!", Toast.LENGTH_LONG).show();
            return;
        }
        et_send_msg.setText("");
        // Replace spaces and line feeds, to transfer data to the server
        sendMsg = sendMsg.replaceAll(" ", "").replaceAll("\n", "").trim();
        ChatBean chatBean = new ChatBean();
        chatBean.setMessage(sendMsg2);

        // SEND indicates a message sent by itself
        chatBean.setState(chatBean.SEND);
        chatBeanList.add(chatBean);

        // Update ListView list
        adapter.notifyDataSetChanged();

        // Get messages from the server sent by the bot
        getDataFromServer();
        System.out.println("sendDate:");
    }

    //Receiving data from the server
    public void getDataFromServer() {
        // Used for assembling json data
        // Encapsulated request body
        String json = "{\"reqType\":0,\"perception\":{\"inputText\":{\"text\":\""+sendMsg
                +"\"}},"+ "\"userInfo\":{\"apiKey\":\"a5a6676ff6ae445b8b7d2a768f913bc3\"," +
                "\"userId\":\"768459\"}}";
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json;charset=utf-8"), json);
        //Build the request, passing in the url and request parameters (in json form)
        Request request = new Request.Builder()
                .url(WEB_SITE)
                .post(body)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        // Enabling asynchronous threads to access the network
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Message message = new Message();
                message.what = MSG_OK;
                message.obj = res;
                mHandler.sendMessage(message);
            }
        });
    }

    //Capture of events
    class MHandler extends Handler {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                // After successful data acquisition, the message object
                // is transformed into a string for parsing
                case MSG_OK:
                    if (msg.obj != null) {
                        String vlResult = (String) msg.obj;
                        parseData(vlResult);
                    }
                    break;
            }
        }
    }

    //Parsing the data passed back
    private void parseData(String JsonData) {
        //Resolving the successful update screen
        try {
            JSONObject obj = new JSONObject(JsonData);
            System.out.println("obj:" + obj);
            int code = (int) new JSONObject(obj.get("intent").toString()).get("code");
            String content = obj.getJSONArray("results").getString(0);
            String s =new JSONObject( new JSONObject(content).get("values").toString()).getString("text");
            if(s.equals(sendMsg))
            {
                s="Sorry, I don't know your mean";
            }
            //Update interface
            updateView(code, s);
        } catch (JSONException e) {
            e.printStackTrace();
            showData("Master, your internet is not good");
        }
    }

    private void showData(String message) {
        ChatBean chatBean = new ChatBean();
        chatBean.setMessage(message);
        // RECEIVE indicates a message sent by the robot
        chatBean.setState(ChatBean.RECEIVE);
        // Add the messages sent by the bot to the OchatBeanList collection
        chatBeanList.add(chatBean);
        //Update the chat view
        adapter.notifyDataSetChanged();
    }

    private void updateView(int code, String content) {
        // Show a different statement to the code that returns a failure
        switch (code) {
            case 4004:
                showData("Master, I'm tired today, I need to rest, come back to play with me tomorrow");
                break;
            case 40005:
                showData("Master, are you speaking an alien language?");
                break;
            case 40006:
                showData("Master, I'm going on a date today, so I'll be off work for a while.");
                break;
            case 40007:
                showData("Master, I'll play with you tomorrow, I'm sick, oooh ......\"");
                break;
            default:
                showData(content);  // Default display of incoming data
                break;
        }
    }

    protected long exitTime; // Record the time of the first click

    @Override
    //Automatically switches off if not operated for a long time
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(Responsebot.this, "Press once more to exit the Smart Chat program！",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Responsebot.this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

