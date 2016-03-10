package se.anyro.nfc_reader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class HttpUtils {

	public static Course submitPostData(Map<String, String> params,
			String encode) {
		byte[] data = getRequestData(params, encode).toString().getBytes();
		Course c=new Course();
		c.code=5;
		try {
			
			URL url=new URL("http://jut33amdemo.applinzi.com/appjoin.php");
			//URL url=new URL("http://172.22.14.1/android_post/servlet/LoginAction");
			HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
			//  System.out.println("1");//�������������Ӧ���
            httpURLConnection.setConnectTimeout(5000);        
            httpURLConnection.setDoInput(true);                  
            httpURLConnection.setDoOutput(true);                 
            httpURLConnection.setRequestMethod("POST"); 
            //System.out.println("2");//�������������Ӧ���
            httpURLConnection.setUseCaches(false);               
            //������������������ı�����
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //����������ĳ���
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //�����������������д������
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);
           // System.out.println("3");//�������������Ӧ���
            int response = httpURLConnection.getResponseCode();            //��÷���������Ӧ��
           // System.out.println(response);//�������������Ӧ���
            if(response == HttpURLConnection.HTTP_OK) {
                 InputStream inptStream = httpURLConnection.getInputStream();
                 String temp=dealResponseResult(inptStream);
                 System.out.println("------");
                // String[] str
                 System.out.println(temp);
                 
                 JSONObject jsonObject2 = new JSONObject(temp);
                 //String str="";
                 String resultCode=jsonObject2.getString("code");
                 System.out.println(resultCode);
                 if(resultCode.equals("0")){
                	 c.code=0;
                	 
                 }
                 else if(resultCode.equals("1")){
                	 c.code=1;
                	
                 }
                 else if(resultCode.equals("2")){
                	 c.code=2;
                	 /*String obj=jsonObject2.getString("obj");
                	 JSONObject json = new JSONObject(obj);
                	 c.stuId=json.getString("stuId");
                	 c.stuName=json.getString("stuName");*/
                	 
                 }
                 else if(resultCode.equals("3")){
                	 c.code=3;
                	 String obj=jsonObject2.getString("obj");
                	 JSONObject json = new JSONObject(obj);
                	 c.teacher=json.getString("tName");
                	 c.teacher_class=json.getString("className");
                 }
                 else if(resultCode.equals("4")){
                	 c.code=4;
                 }
                 
               return c;
            }
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("�쳣");
		}
		
		return c;
	}
	  /*
	 2      * Function  :   �������������Ӧ�������������ת�����ַ�����
	 3      * Param     :   inputStream����������Ӧ������
	 4      * Author    :   ����԰-���ɵ�Ȼ
	 5      */
	     public static String dealResponseResult(InputStream inputStream) {
	         String resultData = null;      //�洢������
	          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	          byte[] data = new byte[1024];
	         int len = 0;
	         try {
	             while((len = inputStream.read(data)) != -1) {
	                 byteArrayOutputStream.write(data, 0, len);
	             }
	         } catch (IOException e) {
	             e.printStackTrace();
	         }
	       
	         resultData = new String(byteArrayOutputStream.toByteArray());    
	         return resultData;
	     }
	private static StringBuffer  getRequestData(Map<String, String> params,
			String encode) {
		StringBuffer stringBuffer=new StringBuffer();
		for(Map.Entry<String, String> entry:params.entrySet()){
			 try {
				stringBuffer.append(entry.getKey())
				 .append("=")
				 .append(URLEncoder.encode(entry.getValue(), encode))
				 .append("&");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		 stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //ɾ������һ��"&"
		
		
		return stringBuffer;
	}

}
