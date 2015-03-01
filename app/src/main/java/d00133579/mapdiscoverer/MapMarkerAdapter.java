package d00133579.mapdiscoverer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andmin on 25/02/2015.
 */
public class MapMarkerAdapter extends BaseAdapter {
   private final List<MapMarkerItem> markers;
   private Context context;
   private List<String> countryCodes;
   LayoutInflater inflater = null;

   public MapMarkerAdapter(Context context) {
      this.context = context;
      this.markers = new ArrayList<>();
      this.countryCodes = new ArrayList<>();
      inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   }

   // Add a ToDoItem to the adapter
   // Notify observers that the data set has changed
   public void add(MapMarkerItem item) {

//      System.out.println("adding item: " + item.getLatitude());
      this.markers.add(item);

      notifyDataSetChanged();
   }

   public void update(int position, MapMarkerItem mItem){
      markers.set(position,mItem);

      notifyDataSetChanged();
   }

   // Clears the list adapter of all items.
   public void clear() {

      this.markers.clear();
      notifyDataSetChanged();

   }

   // Clears the list adapter of an item.
   public void delete(int position) {

      this.markers.remove(position);
      notifyDataSetChanged();

   }

   @Override
   public int getCount() {
      return this.markers.size();
   }

   public List<MapMarkerItem> getMarkers() {
      return markers;
   }

   public List<String> getCountryCodes() {
      return countryCodes;
   }

   public void setCountryCodes(List<String> countryCodes) {
      this.countryCodes = countryCodes;
   }

   @Override
   public Object getItem(int position) {
      return this.markers.get(position);
   }

   @Override
   public long getItemId(int position) {
      return position;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      final MapMarkerItem mItem = (MapMarkerItem) getItem(position);
      final RelativeLayout itemLayout = (RelativeLayout) inflater.inflate(R.layout.map_markers, null);

      ImageView flag = (ImageView) itemLayout.findViewById(R.id.flagElementMarker);
      if(mItem.getImage().length > 1){
         flag.setImageBitmap(
            BitmapFactory.decodeByteArray(mItem.getImage(), 0, mItem.getImage().length)
         );
      }else{
         flag.setImageResource(
            R.drawable.empty
         );
      }

      TextView latitude = (TextView) itemLayout.findViewById(R.id.latitude);
      TextView longitude = (TextView) itemLayout.findViewById(R.id.longitude);

      latitude.setText("lat: " + mItem.getLatitude());
      longitude.setText("lng: " + mItem.getLongitude());

      return itemLayout;
   }

   public Object getFromPosition(LatLng position){
      for (MapMarkerItem marker: markers){
         if (marker.getPosition().equals(position)){
            return marker;
         }
      }
      return null;
   }

   public int getFromTitle(String title) {
      for (MapMarkerItem marker: markers){
         if (marker.getTitle().equals(title)){
            return markers.indexOf(marker);
         }
      }
      return -1;
   }

   public int getFromCountryCode(String countryCode) {
      for (MapMarkerItem marker: markers){
         if (marker.getCountryCode().equals(countryCode)){
            return markers.indexOf(marker);
         }
      }
      return -1;
   }


}
