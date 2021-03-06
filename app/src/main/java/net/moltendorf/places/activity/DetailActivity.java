package net.moltendorf.places.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.moltendorf.places.BaseActivity;
import net.moltendorf.places.model.Place;
import net.moltendorf.places.model.QueryHelper;
import net.moltendorf.places.R;

/**
 * Detail Screen
 * <p/>
 * Displays all details fetched from database about a place.
 * <p/>
 * - Allows phone number to be tapped for calls.
 * - Allows tags to be tapped to search for all places with that tag.
 */
public class DetailActivity extends BaseActivity {
	private static final String TAG = "DetailActivity";

	public static final String ACTION_SHOW_PLACE_BY_ID = "net.moltendorf.places.ACTION_SHOW_PLACE_BY_ID";

	public static final String EXTRA_PLACE_ID = "placeId";

	private static final int EXTRA_PLACE_ID_DEFAULT = -1;

	private Place place;

	private TextView placeName, placePhone;
	private CheckBox     placeFavorite;
	private TextView     placeDescription;
	private TextView     placeHours;
	private LinearLayout placeTags;

	private QueryHelper queryHelper;

	@Override
	protected void onCreateContentView() {
		setContentView(R.layout.activity_detail);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: Called.");

		queryHelper = QueryHelper.getInstance(this);

		createViewReferences();
		createViewListeners();

		handleIntent(getIntent());

		populateViews();
	}

	private void createViewReferences() {
		placeName = (TextView) findViewById(R.id.detail_place_name);
		placePhone = (TextView) findViewById(R.id.detail_place_phone);
		placeFavorite = (CheckBox) findViewById(R.id.detail_place_favorite);
		placeDescription = (TextView) findViewById(R.id.detail_place_description);
		placeHours = (TextView) findViewById(R.id.detail_place_hours);
		placeTags = (LinearLayout) findViewById(R.id.detail_place_tags);
	}

	private void createViewListeners() {
		placePhone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = place.getPhoneRaw();

				if (phone != null) {
					Intent callNumber = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
					startActivity(callNumber);
				}
			}
		});

		placeFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				queryHelper.setPlaceIsFavorite(place, isChecked);
			}
		});
	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();

		switch (action) {
			case ACTION_SHOW_PLACE_BY_ID:
				int placeId = intent.getIntExtra(EXTRA_PLACE_ID, EXTRA_PLACE_ID_DEFAULT);

				if (placeId == EXTRA_PLACE_ID_DEFAULT) {
					throw new IllegalStateException("Intent must contain a valid " + EXTRA_PLACE_ID);
				}

				place = queryHelper.getPlaceById(placeId);
				break;

			default:
				throw new IllegalStateException("Invalid action given: " + action);
		}
	}

	private void populateViews() {
		placeName.setText(place.getName());
		placePhone.setText(place.getPhone());
		placeFavorite.setChecked(place.isFavorite());
		placeDescription.setText(place.getDescription());
		placeHours.setText(place.getHours());

		placeTags.removeAllViewsInLayout();

		for (final Place.Tag tag : place.getTags()) {
			TextView tagView = (TextView) LayoutInflater.from(this).inflate(R.layout.tag, placeTags, false);

			tagView.setText(tag.getName());
			tagView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent searchIntent = new Intent(DetailActivity.this, SearchActivity.class);
					searchIntent.setAction(SearchActivity.ACTION_TAG_ID_SEARCH);
					searchIntent.putExtra(SearchActivity.EXTRA_TAG_ID, tag.getId());

					startActivity(searchIntent);
				}
			});

			placeTags.addView(tagView);
		}
	}
}
