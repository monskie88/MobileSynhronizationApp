package phinma.up.cite.mobilesyncapp;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import phinma.up.cite.mobilesync.adapters.FileItemArrayAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LocalFilesFragment extends Fragment {
	TextView txtDir;
	private ListView listView;
	private FileItemArrayAdapter adapter;
	private File rootDir;
	private List<Item> dirs;
	private List<Item> others;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_localfiles, container, false);
		listView = (ListView) rootView.findViewById(R.id.listView1);
		txtDir = (TextView) rootView.findViewById(R.id.textView1);
		txtDir.setSelected(true);
		//List Storage
		listStorage();
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Item item = (Item) adapter.getItemAtPosition(position);
				
				if(item.getFileIcon()==R.drawable.folder_ic||item.getFileIcon()==R.drawable.ic_launcher){
					
					if(item.getFilePath()==null){
						try{
							listStorage();
						}catch(Exception e){
							e.printStackTrace();
						}
					}else{
						rootDir = new File(item.getFilePath());
						try{
							fillListView(rootDir);
						}catch(NullPointerException npe){
							npe.printStackTrace();
						}
					}
					
				}else{
					rootDir = new File(item.getFilePath());
					Intent i = new Intent();
					boolean noType=true;
					i.setAction(android.content.Intent.ACTION_VIEW);
					if(item.getFileType().startsWith(".txt")){
						i.setDataAndType(Uri.fromFile(rootDir), "text/*");
						noType = false;
					}
					
					String[] images = {".png",".jpg",".jpeg",".gif",".bmp"};
					for(String image:images){
						if(item.getFileType().startsWith(image)){
							i.setDataAndType(Uri.fromFile(rootDir),"image/*");
							noType = false;
						}
					}
					String[] video = {".mp4",".avi",".3gp",".mkv"};
					for(String vid:video){
						if(item.getFileType().startsWith(vid)){
							i.setDataAndType(Uri.fromFile(rootDir),"video/*");
							noType = false;
						}
					}
					String[] sounds = {".mp3",".ogg"};
					for(String sound:sounds){
						if(item.getFileType().startsWith(sound)){
							i.setDataAndType(Uri.fromFile(rootDir),"audio/*");
							noType = false;
						}
					}
					
					if(noType==true){
						i.setDataAndType(Uri.fromFile(rootDir),"*/*");
					}
					
					startActivity(i);
					
				}
			}
			
		});
		
		return rootView;
	}
	public void listStorage(){
		dirs = new ArrayList<Item>();
		StorageUtils su = new StorageUtils();
		for(StorageUtils.StorageInfo si:su.getStorageList()){
			rootDir = new File(si.path);
			Date date = new Date(rootDir.lastModified());
			DateFormat df = DateFormat.getDateTimeInstance();
			String datemodified = df.format(date);
			dirs.add(new Item(si.getDisplayName(), rootDir.getAbsolutePath(), rootDir.listFiles().length + " Items", "Storage", datemodified, R.drawable.folder_ic));
		}
		adapter = new FileItemArrayAdapter(getActivity().getApplicationContext(), R.layout.item_row_layout, dirs);
		listView.setAdapter(adapter);
		txtDir.setText("Current Directory: " + "Storage Device(s)");
	}
	public void fillListView(File dir){
		dirs = new ArrayList<Item>();
		others = new ArrayList<Item>();
		File[] files = dir.listFiles();
		for(File file:files){
			Date date = new Date(file.lastModified());
			DateFormat df = DateFormat.getDateTimeInstance();
			String datemodified = df.format(date);
			if(file.isDirectory()){
				//Error with file size in external sd
				File[] fbuf = file.listFiles();
                int buf = 0;
                if(fbuf != null){
                        buf = fbuf.length;
                }
                else buf = 0;
                String num_item = String.valueOf(buf);
                if(buf == 0) num_item = num_item + " item";
                else num_item = num_item + " items";
				dirs.add(new Item(file.getName(), file.getAbsolutePath(), num_item, "Folder", datemodified, R.drawable.folder_ic));
				
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
				if(fileExtension.startsWith(".txt"))
				{
					imgDrawable = R.drawable.txt_ic;
				}else
				{
					imgDrawable = R.drawable.file_ic;
				}
				
				others.add(new Item(file.getName(), file.getAbsolutePath(), fileSize2 , fileExtension, datemodified, imgDrawable));
			}
			
		}
		Collections.sort(dirs);
		Collections.sort(others);
		boolean rootOfaStorage = false;
		for(StorageUtils.StorageInfo si:StorageUtils.getStorageList()){
			if(si.path.equals(dir.getPath())){
				rootOfaStorage=true;
			}
		}
		if(rootOfaStorage==false){
			dirs.add(0, new Item("...",dir.getParent(),"","","",R.drawable.ic_launcher));
		}else{
			dirs.add(0, new Item("...",null,"","","",R.drawable.ic_launcher));
		}
		dirs.addAll(others);
		adapter = new FileItemArrayAdapter(getActivity().getApplicationContext(), R.layout.item_row_layout, dirs);
		listView.setAdapter(adapter);
		txtDir.setText("Current Directory: " + dir.getPath());
		txtDir.setSelected(true);
	}
	
	
	

}
