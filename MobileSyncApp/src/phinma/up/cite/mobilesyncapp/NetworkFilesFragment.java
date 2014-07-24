package phinma.up.cite.mobilesyncapp;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import phinma.up.cite.mobilesync.adapters.FileItemArrayAdapter;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NetworkFilesFragment extends Fragment {
	ConnectivityManager connManager;
	NetworkInfo mWifi;
	View rootView;
	WifiManager wifi;
	FragmentManager fm;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Fragment fragment = null;
		fm = getFragmentManager();
		
		wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		connManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		rootView = inflater.inflate(R.layout.fragment_networkfiles, container, false);
		if(wifi.isWifiEnabled()){
			if(mWifi.isConnected()){
				fragment = new FillNetworkInfo();
			}else{
				fragment = new ConnectToNetwork();
			}
		}else{
			fragment = new WifiIsOff();
		}
		fm.beginTransaction().add(R.id.content, fragment).commit();
		return rootView;
	}
	
	public void displayView(int position){
		Fragment fragment=null;
		switch(position){
		case 0:
			fragment = new WifiIsOff();
			break;
		case 1:
			fragment = new ListNetworkFiles();
			break;
		case 2:
			fragment = new FillNetworkInfo();
			break;
		case 3:
			fragment = new ConnectToNetwork();
			break;
		}
		fm.beginTransaction().replace(R.id.content, fragment).commit();
	}
	
	
	public class WifiIsOff extends Fragment{
		Button wifiOn;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View rootView = inflater.inflate(R.layout.turn_on_wifi_layout, container,false);
			wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			
			wifiOn = (Button) rootView.findViewById(R.id.button1);
			wifiOn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					wifi.setWifiEnabled(true);
					if(mWifi.isConnected()){
						displayView(2);
					}else{
						displayView(3);
					}
				}
			});
			return rootView;
		}
	}
	
	SmbFile rootDir;
	public class ListNetworkFiles extends Fragment{
		ListView listView;
		TextView txtTitle;
		FileItemArrayAdapter adapter;
		List<Item> dirs;
		List<Item> others;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View rootView = inflater.inflate(R.layout.network_list_files, container,false);
			listView = (ListView) rootView.findViewById(R.id.listView1);
			txtTitle = (TextView) rootView.findViewById(R.id.textView1);
			txtTitle.setText("Current Directory:"+rootDir.getPath());
			txtTitle.setSelected(true);
			listDirectories(rootDir);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub
					Item item = (Item) adapter.getItemAtPosition(position);
					SmbFile notRootDir;
					if(item.getFileIcon()==R.drawable.folder_ic||item.getFileIcon()==R.drawable.ic_launcher){
						try {
							notRootDir = new SmbFile(item.getFilePath(),auth);
							listDirectories(notRootDir);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						
						try {
							notRootDir = new SmbFile(item.getFilePath(),auth);
							File f=new File(Environment.getExternalStorageDirectory(),item.getFileName());
							SmbFileInputStream in=new SmbFileInputStream(notRootDir);
							FileOutputStream out=new FileOutputStream(f);
							byte[] b= new byte[in.available()];
							in.read();
							
							
							Intent i = new Intent();
							i.setAction(android.content.Intent.ACTION_VIEW);
							if(item.getFileType().startsWith(".txt")){
								i.setDataAndType(Uri.parse(notRootDir.getPath()), "text/*");
								
							}
							else if(item.getFileType().startsWith(".jpg")||item.getFileType().startsWith(".png")||item.getFileType().startsWith(".gif")){
								i.setDataAndType(Uri.parse(notRootDir.getPath()), "image/*");
							}
							else{
								i.setDataAndType(Uri.parse(notRootDir.getPath()), "*/*");
							}
							startActivity(i);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}catch(Exception e){
							e.printStackTrace();
						}
					
						
						
					}
				}
			});
			return rootView;
		}
		
		public void listDirectories(SmbFile dir){
			dirs = new ArrayList<Item>();
			others = new ArrayList<Item>();
			try {
				SmbFile[] files = dir.listFiles();
				for(SmbFile file:files){
					Date date = new Date(file.lastModified());
					DateFormat df = DateFormat.getDateTimeInstance();
					String datemodified = df.format(date);
					if(file.isDirectory()){
						//Error with file size in external sd
						SmbFile[] fbuf = file.listFiles();
		                int buf = 0;
		                if(fbuf != null){
		                        buf = fbuf.length;
		                }
		                else buf = 0;
		                String num_item = String.valueOf(buf);
		                if(buf == 0) num_item = num_item + " item";
		                else num_item = num_item + " items";
						dirs.add(new Item(file.getName(), file.getPath(), num_item, "Folder", datemodified, R.drawable.folder_ic));
						
					}else{
						String fileSize2=null, ext=null;
						float fileSize = file.length(); ext = "Byte";
						//KB
						if(fileSize>=1024){ fileSize/=1024; ext="KB";}
						//MB
						if(fileSize>=1024){ fileSize/=1024; ext="MB";}
						//GB
						if(fileSize>=1024){ fileSize/=1024;ext="GB";}
						//TB
						if(fileSize>=1024){ fileSize/=1024;ext= "TB";}
						
							if(fileSize<=1) fileSize2 = fileSize+" "+ext;
							else fileSize2 = fileSize+" "+ext+"s";
						int imgDrawable = 0;
						String fileExtension = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());
						if(fileExtension.startsWith(".txt")){
							imgDrawable = R.drawable.txt_ic;
						}else{
							imgDrawable = R.drawable.file_ic;
						}
						
						others.add(new Item(file.getName(), file.getPath(), fileSize2 , "File", datemodified, R.drawable.file_ic));
					}
				}
				
			} catch (SmbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Collections.sort(dirs);
			Collections.sort(others);
			dirs.addAll(others);
			if(!(dir.getCanonicalPath().equalsIgnoreCase(rootDir.getCanonicalPath()))){
				dirs.add(0,new Item("...",dir.getParent() , "", "", "", R.drawable.ic_launcher));
			}
			adapter = new FileItemArrayAdapter(getActivity().getApplicationContext(), R.layout.item_row_layout, dirs);
			listView.setAdapter(adapter);
			txtTitle.setText("Current Directory: "+dir.getPath());
		}
	}
	NtlmPasswordAuthentication auth;
	public class FillNetworkInfo extends Fragment{
		Button btnLogIn;
		EditText edtIp, edtUser, edtPass;
		String hostname, user, pass, path;
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			View rootView = inflater.inflate(R.layout.network_details_layout, container,false);
			edtIp = (EditText) rootView.findViewById(R.id.edtIPAdd);
			edtUser = (EditText) rootView.findViewById(R.id.edtUsername);
			edtPass = (EditText) rootView.findViewById(R.id.edtPassword);
			
			btnLogIn = (Button) rootView.findViewById(R.id.btnLogIn);
			btnLogIn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v)  {
					// TODO Auto-generated method stub
					if(wifi.isWifiEnabled()){
						if(mWifi.isConnected()){
							displayView(2);
						}else{
							displayView(3);
						}
						hostname = edtIp.getText().toString();
						user = edtUser.getText().toString();
						pass = edtPass.getText().toString();
						if(pass==" "){
							pass=null;
						}
						path= "smb://"+hostname+"/";
						auth = new NtlmPasswordAuthentication(" ",user,pass);
						try {
							
							rootDir=new SmbFile(path,auth);
							Toast.makeText(getActivity().getApplicationContext(), "Connected!"+rootDir.getPath(), Toast.LENGTH_SHORT).show();
							displayView(1);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						displayView(0);
					}
				}
			});
			return rootView;
		}
	}
	
	public class ConnectToNetwork extends Fragment{
		Button btnRefresh;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View rootView = inflater.inflate(R.layout.select_network_layout, container,false);
			btnRefresh = (Button) rootView.findViewById(R.id.btnRefresh);
			btnRefresh.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(wifi.isWifiEnabled()){
						if(mWifi.isConnected()){
							displayView(2);
						}else{
							displayView(3);
						}
					}else{
						displayView(0);
					}
				}
			});
			return rootView;
		}
	}

	
}
