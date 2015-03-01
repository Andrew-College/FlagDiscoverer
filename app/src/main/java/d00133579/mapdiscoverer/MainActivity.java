package d00133579.mapdiscoverer;

/**
 * EXAMPLE GEONAMES URL
 * http://www.geonames.org/flags/x/ie.gif
 */

//new Thread(new Runnable(){
//   @Override
//   public void run(){
//      WebService.setUserName("d00133579");
//      ToponymSearchCriteria searchCriteria=new ToponymSearchCriteria();
//      searchCriteria.setQ("drogheda");
//      ToponymSearchResult searchResult=null;
//      try{
//         searchResult=WebService.search(searchCriteria);
//      }catch(Exception e){
//         e.printStackTrace();
//      }
//      for(Toponym toponym:searchResult.getToponyms()){
//         System.out.println(toponym.getName()+" "+toponym.getCountryName());
//      }
//   }
//}).start();

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.commons.io.IOUtils;
import org.geonames.WebService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity implements GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {
   private static final String TAG = "MAP FLAGGER PROGRAM";
   public static final String ADAPTER_MARKERS = "AdapterMarkers";
   public static final String MARKER_OPTIONS = "MarkerOptions";
   public static final int DURATION = 30000;
   public static final int SCALE = 7;

   private HashMap<String, byte[]> flagmap = new HashMap<>();

   private GoogleMap mMap; // Might be null if Google Play services APK is not available.
   private markerManager manager = new markerManager();
   Marker
      currentlySelected = null,
      choice1 = null,
      choice2 = null;
   MapMarkerAdapter mmAdapter;

   boolean selectMarkers = false;
   Button runButton = null;

   BitmapFactory.Options options;
   //set current descriptor
   BitmapDescriptor
      currentFlag = null,
      emptyFlag = null;
   MarkerOptions currentCountryMarkerOptions = new MarkerOptions().visible(true).position(new LatLng(0, 0)).title("Marker").draggable(true);
   Marker currentCountryMarker = null;
   Polyline pathLine = null;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      WebService.setUserName("d00133579");
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mmAdapter = new MapMarkerAdapter(getApplicationContext());

      Button
         addButton = (Button) findViewById(R.id.addButton),
         removeButton = (Button) findViewById(R.id.removeButton),
         connectButton = (Button) findViewById(R.id.connectButton);

      runButton = (Button) findViewById(R.id.runButton);

      addButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
//            System.out.println("adding button");
            MarkerOptions moptions = new MarkerOptions().position(new LatLng(0, 0)).title(String.valueOf(Math.random())).draggable(true);
            manager.addMarker(moptions);
            mMap.addMarker(moptions);
         }
      });

      removeButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (currentlySelected != null) {
               if (mmAdapter.getFromTitle(currentlySelected.getTitle()) > -1) {
                  mmAdapter.delete(mmAdapter.getFromTitle(currentlySelected.getTitle()));
               }
               manager.removeMarker(currentlySelected.getTitle());
               currentlySelected.remove();
            }
         }
      });

      connectButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            selectMarkers = true;
            choice1 = null;
            choice2 = null;

            runButton.setVisibility(View.GONE);

            if (currentCountryMarker != null) {
               currentCountryMarker.remove();
            }
            if (pathLine != null) {
               pathLine.remove();
            }
         }
      });

      runButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            currentCountryMarkerOptions.position(choice1.getPosition());

            currentCountryMarker = mMap.addMarker(currentCountryMarkerOptions);

            animateMarker(currentCountryMarker, choice2.getPosition(), false, DURATION);
         }
      });

      ListView lactivity = (ListView) findViewById(R.id.flagListView);

/*
      //Inflate footerView for footer_view.xml file
      TextView footerView = (TextView) getLayoutInflater().inflate(R.layout.footer_view, null);

      //Add footerView to ListView
      lactivity.addFooterView(footerView);

      footerView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

            Log.i(TAG, "Entered footerView.OnClickListener.onClick()");


         }
      });
*/

      lactivity.setAdapter(mmAdapter);
      //Setting up map
      setUpMapIfNeeded();


      mMap.setOnMarkerDragListener(this);
      mMap.setOnMarkerClickListener(this);

//      currentCountryMarker = mMap.addMarker(currentCountryMarkerOptions);
//
//      animateMarker(currentCountryMarker, new LatLng(10, 10), false, 3000);
      //Retrieve markers(if any)
      if (savedInstanceState != null) {
         if (savedInstanceState.getParcelableArrayList(MARKER_OPTIONS) == null) {
         /*//Test out the marker, animation, and polyline
         final Handler handler = new Handler();
         System.out.println("no markers: " + manager.getMarkers().size());
         */
//         currentCountryMarker = mMap.addMarker(currentCountryMarkerOptions);
            /*
         pathLine = mMap.addPolyline(popt);
         */
//         animateMarker(currentCountryMarker, new LatLng(10, 10), false, 5000);
            /*
         handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               animateMarker(currentCountryMarker, new LatLng(0, 10), false, 4000);
            }
         }, 5000);
         */

         } else {
            ArrayList<MarkerOptions> savedOptions = savedInstanceState.getParcelableArrayList(MARKER_OPTIONS);
            System.out.println("markers: " + manager.getMarkers().size());
            for (MarkerOptions option : savedOptions) {
               manager.addMarker(option);
               mMap.addMarker(option);
            }
         }

         if (savedInstanceState.getParcelableArrayList(ADAPTER_MARKERS) != null) {
            ArrayList<MapMarkerItem> savedOptions = savedInstanceState.getParcelableArrayList(ADAPTER_MARKERS);
            System.out.println("Get saved Options");

            for (MapMarkerItem option : savedOptions) {
               System.out.println(option.getTitle());
               mmAdapter.add(option);
            }
         }
      }

      options = new BitmapFactory.Options();
      options.inMutable = true;
      options.inSampleSize = SCALE;
      //populate empty flag
      emptyFlag = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.empty, options));
   }

   /**
    * Method graciously augmented from an android sample code
    *
    * @param marker
    * @param toPosition
    * @param hideMarker
    */
   public void animateMarker(final Marker marker, final LatLng toPosition,
                             final boolean hideMarker, final long duration) {
      final Handler handler = new Handler();
      final long start = SystemClock.uptimeMillis();
      Projection proj = mMap.getProjection();
      Point startPoint = proj.toScreenLocation(marker.getPosition());
      final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//      final long duration = 30000;

      final Interpolator interpolator = new LinearInterpolator();

      handler.post(new Runnable() {
         @Override
         public void run() {
            long elapsed = SystemClock.uptimeMillis() - start;
            float t = interpolator.getInterpolation((float) elapsed
               / duration);
            double lng = t * toPosition.longitude + (1 - t)
               * startLatLng.longitude;
            double lat = t * toPosition.latitude + (1 - t)
               * startLatLng.latitude;
            marker.setPosition(new LatLng(lat, lng));

            //Set to the flag of the current country
            final String[] countryCode = new String[1];
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  try {
                     countryCode[0] = new CountryCoder(marker.getPosition()).execute().get();
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               }
            });

            if (flagmap.get(countryCode[0].toLowerCase()) != null) {
               byte[] data = flagmap.get(countryCode[0].toLowerCase());
               Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
               //set current descriptor
               currentFlag = BitmapDescriptorFactory.fromBitmap(bitmap);

               marker.setIcon(currentFlag);
            } else {
               int index = mmAdapter.getFromCountryCode(countryCode[0]);
               if (index > -1) {

                  byte[] data = ((MapMarkerItem) mmAdapter.getItem(index)).getImage();
                  Bitmap bitmap = null;
                  if (data.length > 0) {
                     bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                     //set current descriptor
                     currentFlag = BitmapDescriptorFactory.fromBitmap(bitmap);

                     marker.setIcon(currentFlag);
                  } else {
                     marker.setIcon(emptyFlag);
                  }


               } else {

                  new Thread(new Runnable() {
                     @Override
                     public void run() {

                        byte[] data = null;

                        if (countryCode[0] != null && !countryCode[0].equals("") && countryCode[0].length() < 5) {
                           flagDownloader flag = new flagDownloader("http://www.geonames.org/flags/x/" + countryCode[0].toLowerCase() + ".gif");


                           try {
                              data = flag.execute().get();
                           } catch (InterruptedException e) {
                              e.printStackTrace();
                           } catch (ExecutionException e) {
                              e.printStackTrace();
                           }
                        }


                        final byte[] image = (data != null ? data : new byte[]{});

                        runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                              Bitmap bitmap = null;
                              if (image.length > 0) {
                                 bitmap = BitmapFactory.decodeByteArray(image, 0, image.length, options);
                                 //set current descriptor
                                 currentFlag = BitmapDescriptorFactory.fromBitmap(bitmap);

                                 marker.setIcon(currentFlag);
                              } else {
                                 marker.setIcon(emptyFlag);
                              }


                           }
                        });
                     }
                  }).start();
               }
            }

//            MapMarkerItem markerItem = (MapMarkerItem) mmAdapter.getItem(mmAdapter.getFromTitle(marker.getTitle()));

//            if (currentFlag != null) {
//
//
//               marker.setIcon(currentFlag);
//
//               //reset currentFlag to avoid additional work
////               currentFlag = null;
//            }
            if (t < 1.0) {
               // Post again 16ms later.
               handler.postDelayed(this, 50);
            } else {
               marker.remove();
               //System.out.println("update");
               if (hideMarker) {
                  marker.setVisible(false);
               } else {
                  marker.setVisible(true);

               }
            }
         }
      });
   }

   @Override
   protected void onResume() {
      super.onResume();
      for (MarkerOptions mopts : manager.getMarkers().values()) {
         mMap.addMarker(mopts);
      }
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();


      //noinspection SimplifiableIfStatement
      if (id == R.id.action_settings) {
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
      ArrayList<Parcelable> optionArray = new ArrayList<>();
      for (MarkerOptions mopt : manager.getMarkers().values()) {
         optionArray.add(mopt);
      }

      ArrayList<Parcelable> theMarkers = new ArrayList<>();
      for (MapMarkerItem item : mmAdapter.getMarkers()) {
         theMarkers.add(item);
      }
      savedInstanceState.putParcelableArrayList(MARKER_OPTIONS, optionArray);
      savedInstanceState.putParcelableArrayList(ADAPTER_MARKERS, theMarkers);
   }

   /**
    * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
    * installed) and the map has not already been instantiated.. This will ensure that we only ever
    * call {@link #setUpMap()} once when {@link #mMap} is not null.
    * <p/>
    * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
    * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
    * install/update the Google Play services APK on their device.
    * <p/>
    * A user can return to this FragmentActivity after following the prompt and correctly
    * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
    * have been completely destroyed during this process (it is likely that it would only be
    * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
    * method in {@link #onResume()} to guarantee that it will be called.
    */
   private void setUpMapIfNeeded() {
      // Do a null check to confirm that we have not already instantiated the map.
      if (mMap == null) {
         // Try to obtain the map from the SupportMapFragment.
         mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
            .getMap();
         // Check if we were successful in obtaining the map.
         if (mMap != null) {
            setUpMap();
         }
      }
   }


   /**
    * This is where we can add markers or lines, add listeners or move the camera. In this case, we
    * just add a marker near Africa.
    * <p/>
    * This should only be called once and when we are sure that {@link #mMap} is not null.
    */
   private void setUpMap() {

   }

   @Override
   public void onMarkerDragStart(Marker marker) {
   }

   @Override
   public void onMarkerDrag(Marker marker) {

//      System.out.println(marker.getPosition());
   }

   @Override
   public void onMarkerDragEnd(final Marker marker) {
      MarkerOptions mopt =
         new
            MarkerOptions()
            .title(marker.getTitle())
            .draggable(marker.isDraggable())
            .flat(marker.isFlat())
            .rotation(marker.getRotation())
            .position(marker.getPosition());
//      System.out.println("added marker: " + marker.getTitle() + ", lat/lng: " + marker.getPosition());
      manager.addMarker(mopt);

      final LatLng markerPosition = marker.getPosition();
      final String markerTitle = marker.getTitle();
      new Thread(new Runnable() {
         @Override
         public void run() {

            String countryCode = null;
            try {
               countryCode = new CountryCoder(markerPosition).execute().get();
            } catch (InterruptedException e) {
               e.printStackTrace();
            } catch (ExecutionException e) {
               e.printStackTrace();
            }


//            System.out.println("Country Code: \"" + "http://www.geonames.org/flags/x/" + countryCode.toLowerCase() + ".gif\"");

            byte[] data = null;
            if (countryCode != null && !countryCode.equals("") && countryCode.length() < 5) {
               flagDownloader flag = new flagDownloader("http://www.geonames.org/flags/x/" + countryCode.toLowerCase() + ".gif");


               try {
                  data = flag.execute().get();
                  flagmap.put(countryCode.toLowerCase(), data);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               } catch (ExecutionException e) {
                  e.printStackTrace();
               }
            }

            final byte[] image = (data != null ? data : new byte[]{});

//            System.out.println("Image downloaded, size: " + data.length);

//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inMutable = true;
//            options.inSampleSize = 10;
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//            //set current descriptor
//            currentFlag = BitmapDescriptorFactory.fromBitmap(bitmap);
//            final Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            //send image to imageview
//            fullImage.post(new Runnable() {
//               @Override
//               public void run() {
//                  fullImage.setBackground(drawable);
//               }
//            });
            final String cCode = countryCode;
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  int markerIndex = mmAdapter.getFromTitle(markerTitle);
                  if (markerIndex > -1) {
                     //Since the marker exists, modify the flag and coordinates
                     MapMarkerItem newMarker = (MapMarkerItem) mmAdapter.getItem(markerIndex);
                     newMarker.setPosition(markerPosition);
                     newMarker.setImage(image);

                     mmAdapter.update(markerIndex, newMarker);
                  } else {
//                  System.out.println("image " + image.length);
                     mmAdapter.add(
                        new MapMarkerItem(markerTitle, cCode, image, markerPosition.latitude, markerPosition.longitude)
                     );
                  }
               }
            });
         }
      }).start();
   }

   @Override
   public boolean onMarkerClick(Marker marker) {
      if (selectMarkers) {
         if (choice1 == null) {
            System.out.println("choice1");
            choice1 = marker;
         } else if (choice2 == null) {
            if (choice1.equals(marker)) {
               Toast.makeText(getApplicationContext(), "That marker is already selected, pick another one.", Toast.LENGTH_LONG).show();
               return false;
            }
            System.out.println("choice2");
            choice2 = marker;
            System.out.println("choices made");
            selectMarkers = false;

            PolylineOptions popt =
               new PolylineOptions()
                  .add(
                     new LatLng(
                        choice1.getPosition().latitude,
                        choice1.getPosition().longitude
                     )
                  )
                  .add(
                     new LatLng(
                        choice2.getPosition().latitude,
                        choice2.getPosition().longitude
                     )
                  )
                  .width(5)
                  .color(Color.BLACK);

            pathLine = mMap.addPolyline(popt);

            runButton.setVisibility(View.VISIBLE);
         }
      } else {
         currentlySelected = marker;
      }
      return false;
   }

   private class markerManager {
      private HashMap<String, MarkerOptions> markers;

      public markerManager() {
         this.markers = new HashMap<>();
      }

      public HashMap<String, MarkerOptions> getMarkers() {
         return markers;
      }

      public void setMarkers(HashMap<String, MarkerOptions> markers) {
         this.markers = markers;
      }

      public void addMarker(MarkerOptions newMarker) {
         this.markers.put(newMarker.getTitle(), newMarker);

      }

      public void removeMarker(String markerName) {
         this.markers.remove(markerName);
      }

      public MarkerOptions getMarker(String markerName) {
         return this.markers.get(markerName);
      }

      public void setMarker(MarkerOptions updatedMarker) {
         this.markers.put(updatedMarker.getTitle(), updatedMarker);
      }
   }

   private class CountryCoder extends AsyncTask<Void, Void, String> {

      private LatLng position;

      public CountryCoder(LatLng position) {
         this.position = position;
      }

      @Override
      protected String doInBackground(Void... params) {

         try {
            return WebService.countryCode(this.position.latitude, this.position.longitude);
         } catch (IOException e) {
            e.printStackTrace();
         }

         return null;
      }
   }

   private class flagDownloader extends AsyncTask<Void, Void, byte[]> {
      private String URL;

      public flagDownloader(String URL) {
         this.URL = URL;
      }

      @Override
      protected byte[] doInBackground(Void... params) {
         HttpURLConnection httpUrlConnection = null;
         byte[] data = null;
         try {
            httpUrlConnection = (HttpURLConnection) new java.net.URL(URL)
               .openConnection();

            data =
               IOUtils.toByteArray(httpUrlConnection.getInputStream());//use apaches converter utils


         } catch (MalformedURLException exception) {
            Log.e(TAG, "MalformedURLException");
         } catch (IOException exception) {// Error occurred downloading image,
            Log.e(TAG, "IOException: Downloading image; " + this.URL + " failed");
         } finally {
            if (null != httpUrlConnection)
               httpUrlConnection.disconnect();
         }
         return data;
      }
   }
}
