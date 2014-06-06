package com.zachchristensen.cameraroll.adapters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zachchristensen.cameraroll.AlbumDetailFragment.ImagesQuery;
import com.zachchristensen.cameraroll.R;
import com.zachchristensen.cameraroll.utils.Image_Helper;

public class AlbumRollAdpater extends CursorAdapter{

	/**
     * State of ListView item that has never been determined.
     */
    private static final int STATE_UNKNOWN = 0;

    /**
     * State of a ListView item that is sectioned. A sectioned item must
     * display the separator.
     */
    private static final int STATE_SECTIONED_CELL = 1;

    /**
     * State of a ListView item that is not sectioned and therefore does not
     * display the separator.
     */
    private static final int STATE_REGULAR_CELL = 2;

    private String mDate;
    private int[] mCellStates;
    private final static String DATE_FORMAT = "MM/dd/yyyy";
	
	public AlbumRollAdpater(Context context, Cursor cursor, int flags) {
		super(context, cursor, flags);
	    mCellStates = cursor == null ? null : new int[cursor.getCount()];
	}
	
    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        mCellStates = cursor == null ? null : new int[cursor.getCount()];
    }
	
	private static class AlbumRowHolder {
        public TextView separator;
        public TextView name;
        public CharArrayBuffer titleBuffer = new CharArrayBuffer(128);
        public String date;
        public ImageView image;
    }
    
	public static String getDate(long milliSeconds, String dateFormat)
	{
	    // Create a DateFormatter object for displaying date in specified format.
	    DateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     return formatter.format(calendar.getTime());
	}
	
	public static boolean sameDay(String one, String two){
		return one.equals(two);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		
		 final AlbumRowHolder holder = (AlbumRowHolder) view.getTag();

		 boolean needSeparator = false;

	     final int position = cursor.getPosition();
	     cursor.copyStringToBuffer(ImagesQuery.TITLE, holder.titleBuffer);
	     holder.date = getDate(cursor.getLong(ImagesQuery.DATE), DATE_FORMAT);
	     Log.i("Milliseconds", (cursor.getString(ImagesQuery.DATE)));
	     
	        switch (mCellStates[position]) {
	            case STATE_SECTIONED_CELL:
	                needSeparator = true;
	                break;

	            case STATE_REGULAR_CELL:
	                needSeparator = false;
	                break;

	            case STATE_UNKNOWN:
	            default:
	                // A separator is needed if it's the first itemview of the
	                // ListView or if the group of the current cell is different
	                // from the previous itemview.
	                if (position == 0) {
	                    needSeparator = true;
	                } else {
	                    cursor.moveToPosition(position - 1);

	                    mDate = getDate(cursor.getLong(ImagesQuery.DATE), DATE_FORMAT);
	                    
	                    needSeparator = !sameDay(holder.date, mDate);

	                    cursor.moveToPosition(position);
	                }

	                // Cache the result
	                mCellStates[position] = needSeparator ? STATE_SECTIONED_CELL : STATE_REGULAR_CELL;
	                break;
	        }

	        if (needSeparator) {
	            holder.separator.setText(holder.date);
	            holder.separator.setVisibility(View.VISIBLE);
	        } else {
	            holder.separator.setVisibility(View.GONE);
	        }

	        /*
	         * Title
	         */
	        holder.name.setText(holder.titleBuffer.data, 0, holder.titleBuffer.sizeCopied);	
	        
	        /*
	         * Thumbnail
	         */
	     	// Build URI to the main image from the cursor
	    	int imageID = cursor.getInt(ImagesQuery.ID);
	    	Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(imageID));
	    	holder.image.setImageBitmap(Image_Helper.loadThumbnailImage(context, uri.toString()));
	}

	@Override
	public View newView(Context context, Cursor arg1, ViewGroup parent) {
		View v = LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);

        // The following code allows us to keep a reference on the child
        // views of the item. It prevents us from calling findViewById at
        // each getView/bindView and boosts the rendering code.
        AlbumRowHolder holder = new AlbumRowHolder();
        holder.separator = (TextView) v.findViewById(R.id.separator);
        holder.name = (TextView) v.findViewById(R.id.name);
        holder.image = (ImageView) v.findViewById(R.id.thumbnail);

        v.setTag(holder);

        return v;
	}
	
}
