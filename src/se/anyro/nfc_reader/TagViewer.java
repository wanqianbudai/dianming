/*
 * Copyright (C) 2010 The Android Open Source Project
 * Copyright (C) 2011 Adam Nyb盲ck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.anyro.nfc_reader;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An {@link Activity} which handles a broadcast of a new tag that the device just discovered.
 */
public class TagViewer extends Activity {
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch(msg.what){
			case 0:
				 Course c=(Course) msg.obj;
				 System.out.println("code  "+c.code);
				 switch(c.code){
				 case 0:
					 status.setTextColor(0x77ee0000);
					 status.setText("口令错误");
					 teacher.setText("");
					 teacher_class.setText("");
					 Toast.makeText(getApplicationContext(), "口令错误", 0).show();
					 play_bad_sound();
					 /*if(TextUtils.isEmpty(name.getText().toString())||TextUtils.isEmpty(stuId.getText().toString())){
					   Toast.makeText(getApplicationContext(), "姓名或者学号不能为空", 1).show();
						 
					 }
					 else{
						 Toast.makeText(getApplicationContext(), "姓名或者学号有问题", 1).show(); 
						 
					 }*/
				   break;
				 case 1:
					 status.setTextColor(0x77ee0000);
					 status.setText("注册失败");
					 teacher.setText("");
					 teacher_class.setText("");
					 Toast.makeText(getApplicationContext(), "注册信息不完整", 0).show();
					 play_bad_sound();
					 break;
					 
				 case 2:
					 status.setTextColor(0x7700ee00);
					 status.setText("注册成功");
					 //name.setText(c.getStuName());
					// stuId.setText(c.getStuId());
					 teacher.setText("");
					 teacher_class.setText("");
					 play_sound();
					 break;
				 case 3:
					 status.setTextColor(0x7700ee00);
					 status.setText("点名成功");
					 teacher.setText("老师: "+c.getTeacher());
					 teacher_class.setText("课程: "+c.getTeacher_class());
					 play_sound();
					 break;
				 case 4:
					 status.setTextColor(0x77ee0000);
					 status.setText("点名失败");
					 teacher.setText("");
					 teacher_class.setText("");
					 play_bad_sound();
				default:
					 status.setTextColor(0x77ee0000);
					 status.setText("未知错误");
					 teacher.setText("");
					 teacher_class.setText("");
					 play_bad_sound();
					break;
				 }
				
			break;
			case 1:
				myCreate();
				break;
			
			
			}
			
			
		};
	};
    private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();
  //  private LinearLayout mTagContent;
    private TextView stuId;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;

    private AlertDialog mDialog;
    private EditText name;
    private EditText order;
    private CheckBox save_order;
    private CheckBox auto_submit;
    private Button sumbit;
    TextView status;
    TextView teacher;
    TextView teacher_class;
    SharedPreferences mSharedPreferences;
    TextView tagId;
    String nfcId="123456789";
    LinearLayout ll;
    private SoundPool soundpool;    //声明一个SoundPool对象  
    HashMap<Integer, Integer> soundPoolMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.tag_viewer);
        setContentView(R.layout.start);
       
        soundpool=new SoundPool(4, AudioManager.STREAM_MUSIC, 0);//创建一个SoundPool对象，该对象可以容纳8个音频流 
        soundPoolMap=new HashMap<Integer, Integer>();
		soundPoolMap.put(1, soundpool.load(this, R.raw.bugu, 1));
		soundPoolMap.put(2, soundpool.load(this, R.raw.bad, 1));
		handler.sendEmptyMessageDelayed(1, 1500);
       /* ll=(LinearLayout)findViewById(R.id.start);
        final AlphaAnimation animation = new AlphaAnimation(1, 0); 
        animation.setDuration(1000);//设置动画持续时间 
        ll.setAnimation(animation);
        animation.startNow(); 
        animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
				
			}
		});*/
     
          
        /*resolveIntent(getIntent());
       // mTagContent = (LinearLayout) findViewById(R.id.list);
        mSharedPreferences = getSharedPreferences("data", 0);
        name=(EditText) findViewById(R.id.name);
        order=(EditText) findViewById(R.id.kouling);
        String ostr=mSharedPreferences.getString("order", "");
        order.setText(ostr);//设置保存了的数据
        save_order=(CheckBox) findViewById(R.id.savekouling);
        auto_submit=(CheckBox) findViewById(R.id.zidongtijiao);
        sumbit=(Button) findViewById(R.id.submit);
        status=(TextView) findViewById(R.id.status);
        result=(TextView) findViewById(R.id.result);
        tagId=(TextView) findViewById(R.id.tagId);
        boolean IsAuto=mSharedPreferences.getBoolean("auto", false);
        if(!TextUtils.isEmpty(ostr)){
        	save_order.setChecked(true);
        }
        auto_submit.setChecked(IsAuto);
        order.setText(ostr);//设置保存了的数据
        if(IsAuto){
        	
        	//System.out.println("auto="+IsAuto+"  order="+order.getText());
        	if(TextUtils.isEmpty(order.getText().toString())||TextUtils.isEmpty(stuId)){
				Toast.makeText(getApplicationContext(), "口令或者学生ID号不能为空", 1).show();
				
			}
        	else{
        		submit_way();
        	}
        	
        	
        }
        sumbit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//System.out.println(order.getText().toString());
				if(TextUtils.isEmpty(order.getText().toString())||TextUtils.isEmpty(stuId)){
					Toast.makeText(getApplicationContext(), "口令或者学生ID号不能为空", 1).show();
					return;
				}
				else{
					submit_way();
				}
			}
		});
       
        save_order.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(save_order.isChecked()){
					String kouling=order.getText().toString();
					//System.out.println("口令："+kouling);
					//Toast.makeText(getApplicationContext(), "口令不能为空", 1).show();
					if(TextUtils.isEmpty(kouling)){
						Toast.makeText(getApplicationContext(), "口令不能为空", 1).show();
						save_order.setChecked(false);
						return;
					}
					 SharedPreferences.Editor mEditor = mSharedPreferences.edit(); 
					 mEditor.putString("order",kouling);  
				     mEditor.commit();  
					 
				}
				else{
					 SharedPreferences.Editor mEditor = mSharedPreferences.edit(); 
					 mEditor.putString("order",""); 
					 mEditor.putBoolean("auto", false);
				     mEditor.commit(); 
				     auto_submit.setChecked(false);
				}
			}
		});
        auto_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!save_order.isChecked()){
					Toast.makeText(getApplicationContext(), "口令没有保存", 1).show();
					auto_submit.setChecked(false);
					return;
				}
				else{
					 SharedPreferences.Editor mEditor = mSharedPreferences.edit(); 
					 mEditor.putBoolean("auto", true);  
				     mEditor.commit();
				     if(TextUtils.isEmpty(order.getText())||TextUtils.isEmpty(stuId)){
							Toast.makeText(getApplicationContext(), "口令或者学生ID号不能为空", 1).show();
							
						}
			        	else{
			        		submit_way();
			        	}
				}
			}
		});*/
        
        
      

        /*mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            showMessage(R.string.error, R.string.no_nfc);
            finish();
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true) });*/
    }
    	
    	
    	
    	
    protected void play_bad_sound() {
    	 soundpool.play( soundPoolMap.get(2), 1, 1, 0, 0, 1);
		
	}




	protected void play_sound() {
    	 soundpool.play( soundPoolMap.get(1), 1, 1, 0, 0, 1);
		
	}




	protected void myCreate() {
    	   setContentView(R.layout.main1);
    	  // gif1 = (GifView)findViewById(R.id.gif); 
  		  // gif1.setMovieResource(R.drawable.bb); 
    	   resolveIntent(getIntent());
    	   mSharedPreferences = getSharedPreferences("data", 0);
    	   
    	   //为控件找ID
           name=(EditText) findViewById(R.id.name);
           order=(EditText) findViewById(R.id.kouling);
           stuId=(TextView) findViewById(R.id.stuID);
           save_order=(CheckBox) findViewById(R.id.savekouling);
           auto_submit=(CheckBox) findViewById(R.id.zidongtijiao);
           sumbit=(Button) findViewById(R.id.submit);
           status=(TextView) findViewById(R.id.status);
           teacher=(TextView) findViewById(R.id.teacher);
           teacher_class=(TextView) findViewById(R.id.teacher_class);
           
           String ostr=mSharedPreferences.getString("order", "");
           boolean IsAuto=mSharedPreferences.getBoolean("auto", false);//得到保存了的自动提交位
           order.setText(ostr);//设置保存了的口令数据
           tagId=(TextView) findViewById(R.id.nfcID);
           if(!TextUtils.isEmpty(ostr)){//如果口令保存了
              	save_order.setChecked(true);
            }
          
           auto_submit.setChecked(IsAuto);
           if(IsAuto){
           	
           	if(TextUtils.isEmpty(order.getText().toString())||TextUtils.isEmpty(nfcId)){
   				Toast.makeText(getApplicationContext(), "口令或者ID号不能为空", 1).show();
   				
   			}
           	else{
           		submit_way();
           	}
           	
           	
           }
           sumbit.setOnClickListener(new OnClickListener() {
   			
   			@Override
   			public void onClick(View v) {
   				//play_sound();
   				//System.out.println(order.getText().toString());
   				if(TextUtils.isEmpty(order.getText().toString())||TextUtils.isEmpty(nfcId)){
   					Toast.makeText(getApplicationContext(), "口令或者ID号不能为空", 0).show();
   					return;
   				}
   				else{
   					submit_way();
   				}
   			}
   		});
          
           save_order.setOnClickListener(new OnClickListener() {
   			@Override
   			public void onClick(View v) {
   				if(save_order.isChecked()){
   					String kouling=order.getText().toString();
   					if(TextUtils.isEmpty(kouling)){
   						Toast.makeText(getApplicationContext(), "口令不能为空", 1).show();
   						save_order.setChecked(false);
   						return;
   					}
   					 SharedPreferences.Editor mEditor = mSharedPreferences.edit(); 
   					 mEditor.putString("order",kouling);  
   				     mEditor.commit();  
   					 
   				}
   				else{
   					 SharedPreferences.Editor mEditor = mSharedPreferences.edit(); 
   					 mEditor.putString("order",""); 
   					 mEditor.putBoolean("auto", false);
   				     mEditor.commit(); 
   				     auto_submit.setChecked(false);
   				}
   			}
   		});
           auto_submit.setOnClickListener(new OnClickListener() {
   			
   			@Override
   			public void onClick(View v) {
   				if(!save_order.isChecked()){
   					Toast.makeText(getApplicationContext(), "口令没有保存", 1).show();
   					auto_submit.setChecked(false);
   					return;
   				}
   				else{
   					 SharedPreferences.Editor mEditor = mSharedPreferences.edit(); 
   					 mEditor.putBoolean("auto", true);  
   				     mEditor.commit();
   				     if(TextUtils.isEmpty(order.getText())||TextUtils.isEmpty(nfcId)){
   							Toast.makeText(getApplicationContext(), "口令或者ID号不能为空", 1).show();
   							
   						}
   			        	else{
   			        		submit_way();
   			        	}
   				}
   			}
   		});
           
           
         

         
          
          
    	   
	}
	public boolean isNetworkAvailable(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        if (cm == null) {   
        } 
        else {
            NetworkInfo[] info = cm.getAllNetworkInfo();   
            if (info != null) {   
                for (int i = 0; i < info.length; i++) {   
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
                        return true; 
                    }
                }
            }
        }
        return false;
    }
    /**
     * 强制隐藏输入法键盘
     */
    private void hideInput(Context context,View view){
        InputMethodManager inputMethodManager =
        (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    protected void submit_way() {
        if(!isNetworkAvailable(getApplicationContext())){
        	Toast.makeText(getApplicationContext(), "请检查网络", 0).show();
        	return;
        }
    	//hideInput(getApplicationContext(),order);
    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
    	///boolean isOpen=imm.isActive();
    	//System.out.println("open:"+isOpen);
    	//if(isOpen)
    	//imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
       //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
    	
       final Map<String,String> params =new HashMap<String, String>();
 	   params.put("nfcId", nfcId);
 	   params.put("stuId", stuId.getText().toString());
 	   params.put("stuName", name.getText().toString());
 	   params.put("keyword", order.getText().toString());
 	   new Thread(){
 		   public void run() {
 			   
 			   Course ms=HttpUtils.submitPostData(params, "utf-8");
 			   Message msg=handler.obtainMessage();
 			   msg.what=0;
 			   msg.obj=ms;
 			   handler.sendMessage(msg);
 			   
 		   };
 		   
 	   }.start();
		
	}

	private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }

    private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
            mAdapter.disableForegroundNdefPush(this);
        }
    }

    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_disabled);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
        return;
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
               // byte[] empty = new byte[0];
               // byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                nfcId=dumpTagData(tag);
                if(TextUtils.isEmpty(nfcId)){
                	tagId.setText("ID: 没有检测到");
                }
                else{
                	tagId.setText("ID: "+nfcId+"");
                }
                
                
               /* byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs = new NdefMessage[] { msg };*/
            }
            // Setup the views
            //buildTagViews(msgs);
        }
    }

    private String dumpTagData(Parcelable p) {
    	Tag tag = (Tag) p;
        byte[] id = tag.getId();
    	return getHex(id);
        /*StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");*/
        /*sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
        sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                case MifareClassic.TYPE_CLASSIC:
                    type = "Classic";
                    break;
                case MifareClassic.TYPE_PLUS:
                    type = "Plus";
                    break;
                case MifareClassic.TYPE_PRO:
                    type = "Pro";
                    break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                case MifareUltralight.TYPE_ULTRALIGHT:
                    type = "Ultralight";
                    break;
                case MifareUltralight.TYPE_ULTRALIGHT_C:
                    type = "Ultralight C";
                    break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();*/
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

   /* void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout content = mTagContent;

        // Parse the first message in the list
        // Build views for all of the sub records
        Date now = new Date();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();
        for (int i = 0; i < size; i++) {
            TextView timeView = new TextView(this);
            timeView.setText(TIME_FORMAT.format(now));
            content.addView(timeView, 0);
            ParsedNdefRecord record = records.get(i);
            content.addView(record.getView(this, inflater, content, i), 1 + i);
            content.addView(inflater.inflate(R.layout.tag_divider, content, false), 2 + i);
        }
    }*/

   /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_main_clear:
             // menuMainClearClick();
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

  /*  private void menuMainClearClick() {
        for (int i = mTagContent.getChildCount() -1; i >= 0 ; i--) {
            View view = mTagContent.getChildAt(i);
            if (view.getId() != R.id.tag_viewer_text) {
                mTagContent.removeViewAt(i);
            }
        }
    }*/

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
        SharedPreferences  mSharedPreferences1 = getSharedPreferences("data", 0);
        boolean IsAuto=mSharedPreferences1.getBoolean("auto", false);
        if(IsAuto){
        	
        	//System.out.println("auto="+IsAuto+"  order="+order.getText());
        	if(TextUtils.isEmpty(order.getText())||TextUtils.isEmpty(nfcId)){
				Toast.makeText(getApplicationContext(), "口令或者学生ID号不能为空", 0).show();
				
			}
        	else{
        		submit_way();
        	}
        	
        	
        }
    }
}
