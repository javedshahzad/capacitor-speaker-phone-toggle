package ar.com.anura.plugins.audiotoggle.audiodevicemanager;

import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;

public class AudioDeviceManagerService {

    public static AudioDeviceManagerInterface get(final AppCompatActivity activity) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        return new AudioDeviceManager34(activity);
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return new AudioDeviceManager33(activity);
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
        return new AudioDeviceManager32(activity);
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        return new AudioDeviceManager31(activity);
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        return new AudioDeviceManager30(activity);
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return new AudioDeviceManager29(activity);
      }
        return null;
    }
}
