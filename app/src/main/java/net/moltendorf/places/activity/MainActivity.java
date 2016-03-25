package net.moltendorf.places.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.moltendorf.places.PlacesQueryHelper;
import net.moltendorf.places.R;

/**
 * Main screen; displays information about the overall location.
 */
public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";

	private Button
		allPlacesButton,
		myFavoritesButton,
		dineInButton,
		confectionsButton,
		allTagsButton;

	private PlacesQueryHelper queryHelper;

	@Override
	protected void onCreateContentView() {
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");

		queryHelper = PlacesQueryHelper.getInstance(this);

		createViewReferences();
		createViewListeners();
	}

	private void createViewReferences() {
		allPlacesButton = (Button) findViewById(R.id.main_all_places);
		myFavoritesButton = (Button) findViewById(R.id.main_my_favorites);
		dineInButton = (Button) findViewById(R.id.main_dine_in);
		confectionsButton = (Button) findViewById(R.id.main_confections);
		allTagsButton = (Button) findViewById(R.id.main_all_tags);
	}

	private void createViewListeners() {
		searchButtonListener(allPlacesButton, null, null, null);
		searchButtonListener(myFavoritesButton, SearchActivity.ACTION_FAVORITE_SEARCH, null, null);
		searchButtonListener(dineInButton, SearchActivity.ACTION_TAG_ID_SEARCH, SearchActivity.EXTRA_TAG_ID, queryHelper.getTagIdByName("dining"));
		searchButtonListener(confectionsButton, SearchActivity.ACTION_TAG_ID_SEARCH, SearchActivity.EXTRA_TAG_ID, queryHelper.getTagIdByName("confections"));

		allTagsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, TagsActivity.class);
				startActivity(intent);
			}
		});
	}

	private void searchButtonListener(Button button, final String action, final String extra, final Integer value) {
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SearchActivity.class);

				if (action != null) {
					intent.setAction(action);
				}

				if (extra != null && value != null) {
					intent.putExtra(extra, value);
				}

				startActivity(intent);
			}
		});
	}
}
