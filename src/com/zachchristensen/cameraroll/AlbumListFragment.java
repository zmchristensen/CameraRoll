package com.zachchristensen.cameraroll;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.zachchristensen.cameraroll.adapters.AlbumListAdapter;
import com.zachchristensen.cameraroll.objects.Gallery;

/**
 * A list fragment representing a list of Albums. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link AlbumDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class AlbumListFragment extends ListFragment implements
	LoaderManager.LoaderCallbacks<Cursor>{

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String gallery);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String gallery) {
		}
	};
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public AlbumListFragment() {
	}

	// This is the Adapter being used to display the list's data.
    //SimpleCursorAdapter mAdapter;
    
    AlbumListAdapter adapter;
    ArrayList<Gallery> galleries;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("Loading galleries");
		
        setListShown(false);
        
        getLoaderManager().initLoader(0, null, this);
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		
		//Cursor c = ((SimpleCursorAdapter) getListAdapter()).getCursor();
		//c.moveToPosition(position);
		
		//int columnIndex = c.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
		
		mCallbacks.onItemSelected(galleries.get(position).getName());
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// which image properties are we querying
	    String[] projection = new String[]{
	            MediaStore.Images.Media._ID,
	            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
	    };
	
	    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

	    CursorLoader cursorLoader = new CursorLoader(getActivity(), images, projection, null, null, null);
	    
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		galleries = new ArrayList<Gallery>();
	    
		if (data != null) {
	        int bucketColumn = data.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

	        if(data.moveToFirst()) {
	        
	        	String bucket;
	        	
	        	do {
		            // Get the field values
		            bucket = data.getString(bucketColumn);
		          
		            // Do something with the values.
		            Log.i("ListingImages", " bucket=" + bucket);
		            
		            boolean contains = false;
		            for(Gallery g : galleries){
		            	if(g.getName().equals(bucket)){
		            		g.incrememntCount();
		            		contains = true;
		            	}
		            }
		            if(!contains){
		            	galleries.add(new Gallery(bucket, 1));
		            }
		            
		        } while (data.moveToNext());
	        }
		}

		adapter = new AlbumListAdapter(getActivity(), R.layout.album_list_row,
				R.id.gallery_name, galleries);
	    setListAdapter(adapter);
		
        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}
}
