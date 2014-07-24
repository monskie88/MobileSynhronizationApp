package phinma.up.cite.mobilesync.adapters;

import java.util.List;

import phinma.up.cite.mobilesyncapp.Item;
import phinma.up.cite.mobilesyncapp.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileItemArrayAdapter extends ArrayAdapter<Item> {
	Context context;
	int resource;
	List<Item> items;
	public FileItemArrayAdapter(Context context, int resource,
			List<Item> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.resource = resource;
		this.items = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v=convertView;
		if(v==null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(resource, null);
		}
		Item item = items.get(position);
		ImageView fileIcon = (ImageView) v.findViewById(R.id.fileIcon);
		Drawable imageDrawable = v.getResources().getDrawable(item.getFileIcon());
		fileIcon.setImageDrawable(imageDrawable);
		
		TextView fileName = (TextView) v.findViewById(R.id.fileName);
		fileName.setText(item.getFileName());
		fileName.setSelected(true);
		
		TextView fileSize = (TextView) v.findViewById(R.id.fileSize);
		fileSize.setText(item.getFileSize());
		
		TextView fileLastModified = (TextView)v.findViewById(R.id.fileLastModified);
		fileLastModified.setText(item.getFileLastModified());
		return v;
	}

}
