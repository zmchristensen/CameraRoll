package com.zachchristensen.cameraroll.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zachchristensen.cameraroll.R;
import com.zachchristensen.cameraroll.objects.Gallery;

public class AlbumListAdapter extends ArrayAdapter<Gallery> {

	public AlbumListAdapter(Context context, int resource, int textViewResourceId, ArrayList<Gallery> galleries) {
		super(context, resource, textViewResourceId, galleries);
			this.context = context;
			this.galleries = galleries;
	}

	Context context;
	ArrayList<Gallery> galleries;
	
	private static class AlbumListRowHolder {
		public TextView name;
		public TextView count;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		super.getView(position, convertView, parent);
		
		View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.album_list_row, null);
        }

        Gallery g = galleries.get(position);
        
        if (g != null) {
            TextView name = (TextView) view.findViewById(R.id.gallery_name);
            TextView count = (TextView) view.findViewById(R.id.gallery_photocount);
            
            name.setText(g.getName());
            count.setText(g.getPhotoCount() + " photos");
         }

        return view;
	}
}
