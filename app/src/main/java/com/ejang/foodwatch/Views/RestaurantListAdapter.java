package com.ejang.foodwatch.Views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.ejang.foodwatch.Activities.BrowseActivity;
import com.ejang.foodwatch.R;
import com.ejang.foodwatch.Utils.HazardRating;
import com.ejang.foodwatch.Utils.InspectionResult;
import com.ejang.foodwatch.Utils.Restaurant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Eric on 2017-03-22.
 */

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

    private BrowseActivity context;
    
    // This list is what is actually displayed on the ListView. The superclass holds a reference
    // to this list.
    private ArrayList<Restaurant> restaurantsInList;

    // This list should NEVER change in size from the moment it is initialized. It will be sorted
    // throughout its lifetime, but no elements should be added or removed.
    private ArrayList<Restaurant> allOriginalRestaurants;

    // Custom ArrayAdapter that handles TrafficEvent objects.
    public RestaurantListAdapter(BrowseActivity context, ArrayList<Restaurant> restaurants) {

        super(context, R.layout.list_item_restaurant, restaurants);
        this.context = context;
        this.restaurantsInList = restaurants;
        allOriginalRestaurants = new ArrayList<>(restaurantsInList);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View rowView;
        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_restaurant, null, true);
        } else {
            rowView = view;
        }

        // Get each view from the TrafficEvent ListView
        TextView nameView = (TextView) rowView.findViewById(R.id.restaurant_name);
        TextView locationView = (TextView) rowView.findViewById(R.id.restaurant_location);
        TextView cleanliness = (TextView) rowView.findViewById(R.id.hazard_level);
        TextView numInspections = (TextView) rowView.findViewById(R.id.num_inspections);
        TextView lastInspectionDate = (TextView) rowView.findViewById(R.id.last_inspection_date);
        TextView distanceFromUser = (TextView) rowView.findViewById(R.id.distance_from_location);

        Restaurant item = getItem(position);

        // Set the values for each view
        nameView.setText(item.name);
        locationView.setText(item.address);

        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

        HazardRating hazard = item.mostRecentSafety;

        if (hazard == HazardRating.UNSAFE)
        {
            cleanliness.setText(context.getString(R.string.high_hazard));
            cleanliness.setTextColor(context.getColor(R.color.colorHighHazard));
        }
        else if (hazard == HazardRating.SAFE)
        {
            cleanliness.setText(context.getString(R.string.low_hazard));
            cleanliness.setTextColor(context.getColor(R.color.colorLowHazard));
        }
        else if (hazard == HazardRating.MODERATE)
        {
            cleanliness.setText(context.getString(R.string.mod_hazard));
            cleanliness.setTextColor(context.getColor(R.color.colorModerateHazard));
        }
        else
        {
            cleanliness.setText(context.getString(R.string.unknown_hazard));
            cleanliness.setTextColor(context.getColor(R.color.greyFont));
        }

        ArrayList<InspectionResult> results = item.inspectionResults;
        if (results == null || results.size() == 0)
        {
            cleanliness.setText(context.getString(R.string.unknown_hazard));
            cleanliness.setTextColor(context.getColor(R.color.greyFont));
            numInspections.setText(context.getString(R.string.num_inspections, "Not available"));
            lastInspectionDate.setText(context.getString(R.string.last_inspection_date, "Not available"));
        }
        else
        {
            numInspections.setText(context.getString(R.string.num_inspections, String.valueOf(results.size())));

            if (lastInspectionDate != null) {
                String date = dateFormat.format(results.get(0).inspectionDate);
                lastInspectionDate.setText(context.getString(R.string.last_inspection_date, date));
            }
        }

        distanceFromUser.setText(String.format(java.util.Locale.US,"%.1f", item.distanceFromUser / 1000) + " km");

        return rowView;

    }

    @Override
    public RestaurantFilter getFilter() {
        return new RestaurantFilter();
    }

    // This method is called when a new location has been set. When a new location is set, all previous
    // search filters are cleared, so we clear the ListView restaurants and populate it with all of
    // the restaurants ordered by distance from the newly set location.
    public void updateDistancesFromUser()
    {
        System.err.println("INSIDE UPDATE DISTANCES");
        // Iterate through all Restaurants and update their distances from user's chosen location
        System.err.println("THIS COUNT IS: " + this.getCount());
        for (int i=0 ; i < allOriginalRestaurants.size() ; i++)
        {
            allOriginalRestaurants.get(i).updateDistanceFromUser();
        }
        Collections.sort(allOriginalRestaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                return o1.distanceFromUser.compareTo(o2.distanceFromUser);
            }
        });
        restaurantsInList.clear();
        restaurantsInList.addAll(allOriginalRestaurants);
        this.notifyDataSetChanged();
    }

    public void updateAdapterData(ArrayList<Restaurant> restaurants)
    {
        this.clear();
        this.addAll(restaurants);
        this.allOriginalRestaurants.clear();
        this.allOriginalRestaurants.addAll(restaurants);
        this.notifyDataSetChanged();
    }

    public boolean includeSafe = true;
    protected boolean includeModerate = true;
    protected boolean includeUnsafe = true;
    protected boolean includeUnknown = true;

    // Inner class defined to support different types of filtering.
    public class RestaurantFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            System.err.println("tryna filter dawg");
            if (constraint == null)
            {
                // No constraint given, so nothing to change in the original array.
                results.values = allOriginalRestaurants;
                results.count = allOriginalRestaurants.size();
            }
            else
            {
                ArrayList<Restaurant> filteredRestaurants = new ArrayList<>();

                for (Restaurant restaurant : allOriginalRestaurants) {
                    // Filter by matching text in the search box to the restaurant's name.
                    if (restaurant.name.toLowerCase().contains(constraint.toString().toLowerCase()))
                    {
                        HazardRating hazard = restaurant.mostRecentSafety;
                        if ((includeSafe && hazard == HazardRating.SAFE) || (includeModerate && hazard == HazardRating.MODERATE)
                                || (includeUnsafe && hazard == HazardRating.UNSAFE) || (includeUnknown && hazard == HazardRating.UNKNOWN))
                        {
                            filteredRestaurants.add(restaurant);
                        }
                    }
                }
                results.values = filteredRestaurants;
                results.count = filteredRestaurants.size();
            }
            System.err.println("finished filter dawg");
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {
                restaurantsInList.clear();
                notifyDataSetChanged();
            }
            else {
                restaurantsInList.clear();
                restaurantsInList.addAll((ArrayList<Restaurant>) results.values);
                notifyDataSetChanged();
            }
        }

        public void setIncludeSafe(boolean includeSafe)
        {
            RestaurantListAdapter.this.includeSafe = includeSafe;
        }

        public void setIncludeModerate(boolean includeModerate)
        {
            RestaurantListAdapter.this.includeModerate = includeModerate;
        }

        public void setIncludeUnsafe(boolean includeUnsafe)
        {
            RestaurantListAdapter.this.includeUnsafe = includeUnsafe;
        }

        public void setIncludeUnknown(boolean includeUnknown)
        {
            RestaurantListAdapter.this.includeUnknown = includeUnknown;
        }
    }
}
