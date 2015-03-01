package d00133579.mapdiscoverer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andmin on 25/02/2015.
 */
public class MapMarkerItem implements Parcelable {
   private String
      title,
      countryCode;
   private byte[]
      image;
   private double
      latitude,
      longitude;
   private LatLng
      position;

   public static final Parcelable.Creator<MapMarkerItem> CREATOR = new Parcelable.Creator<MapMarkerItem>() {
      public MapMarkerItem createFromParcel(Parcel source) {
         return new MapMarkerItem(
            source
         );
      }

      public MapMarkerItem[] newArray(int size) {
         return new MapMarkerItem[size];
      }

   };

   public MapMarkerItem(Parcel source){
      String
         title = source.readString(),
         countryCode = source.readString();

      double[] position = null;
      source.readDoubleArray(position);

      this.title = title;
      this.countryCode = countryCode;
      this.image = new byte[]{};
      this.latitude = position[0];
      this.longitude = position[1];
      this.position =
         new LatLng(
            position[0],
            position[1]
         );
   }

   public MapMarkerItem(String title, String countryCode, byte[] image, double latitude, double longitude) {
      this.title = title;
      this.countryCode = countryCode;
      this.image = image;
      this.latitude = latitude;
      this.longitude = longitude;
      this.position = new LatLng(latitude, longitude);

   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getCountryCode() {
      return countryCode;
   }

   public void setCountryCode(String countryCode) {
      this.countryCode = countryCode;
   }

   public byte[] getImage() {
      return image;
   }

   public void setImage(byte[] image) {
      this.image = image;
   }

   public double getLatitude() {
      return latitude;
   }

   public void setLatitude(double latitude) {
      this.latitude = latitude;
      this.setPosition(new LatLng(latitude,this.longitude));
   }

   public double getLongitude() {
      return longitude;
   }

   public void setLongitude(double longitude) {
      this.longitude = longitude;
      this.setPosition(new LatLng(this.latitude,longitude));
   }

   public LatLng getPosition() {
      return position;
   }

   public void setPosition(LatLng position) {
      this.position = position;
      this.latitude = position.latitude;
      this.longitude = position.longitude;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.title);
      dest.writeString(this.countryCode);
      dest.writeDoubleArray(new double[]{latitude,longitude});
   }
}
