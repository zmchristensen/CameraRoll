package com.zachchristensen.cameraroll;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.zachchristensen.cameraroll.adapters.AlbumRollAdpater;

/**
 * A fragment representing a single Album detail screen. This fragment is either
 * contained in a {@link AlbumListActivity} in two-pane mode (on tablets) or a
 * {@link AlbumDetailActivity} on handsets.
 */
public class AlbumDetailFragment extends ListFragment implements
	LoaderManager.LoaderCallbacks<Cursor> {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String GALLERY_NAME = "gallery_name";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public AlbumDetailFragment() {
	}
	
	AlbumRollAdpater adapter;
	String gallery;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(GALLERY_NAME)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			gallery = getArguments().getString(GALLERY_NAME);
		}
		
		getActivity().setTitle(gallery);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getListView().setDividerHeight(0);
		
		setEmptyText("Loading images");
		
		adapter = new AlbumRollAdpater(getActivity(), null, 0);
		setListAdapter(adapter);
		
		setListShown(false);
		
		getLoaderManager().initLoader(1, null, this);
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		Cursor cursor = adapter.getCursor();
			cursor.moveToPosition(position);
		
		int imageID = cursor.getInt(ImagesQuery.ID);
    	Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(imageID));
    	
		Intent i = new Intent(getActivity(), ImagePreview.class);
			i.putExtra(ImagePreview.IMAGE_URI, uri.toString());
		startActivity(i);
	}
	
	public interface ImagesQuery {
		String[] PROJECTION = {
	            MediaStore.Images.Media._ID,
	            MediaStore.Images.Media.TITLE,
	            MediaStore.Images.Media.DATE_TAKEN,
	            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
	    };
	
		int ID = 0;
	    int TITLE = 1;
	    int DATE = 2;
	    int BUCKET = 3;
	
	    String SORT_ORDER = MediaStore.Images.Media.DATE_TAKEN + " DESC";
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			    
	    String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?";
	    String[] selectArgs = new String[] { gallery };
	    
	    CursorLoader cursorLoader = new CursorLoader(getActivity(), images,
	    		ImagesQuery.PROJECTION, selection, selectArgs, ImagesQuery.SORT_ORDER);
	    
	    return cursorLoader; 
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.changeCursor(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}
