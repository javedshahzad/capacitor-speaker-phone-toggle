package ar.com.anura.plugins.audiotoggle.audiodevicemanager;

import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.R)
public class AudioDeviceManager30 extends AudioDeviceManager implements AudioDeviceManagerInterface {

  private boolean first = true;
  private boolean notified = false;

  AudioDeviceManager30(final AppCompatActivity activity) {
    super(activity);
    registerAudioDeviceCallbacks(this::onAudioDevicesAdded, this::onAudioDevicesRemoved);
  }

  @Override
  public void setSpeakerOn(boolean speakerOn) {
    notified = false;
    Thread checkNotify = new Thread(() -> {
      try {
        Thread.sleep(3000);
        if (!notified) {
          notifySpeakerStatus();
        }
      } catch (InterruptedException v) {
        System.out.println(v);
        notifySpeakerStatus();
      }
    });
    checkNotify.start();

    if (isBluetoothConnected()) {
      super.setAudioFocus(1300);
    } else {
      super.setAudioFocus(0);
    }

    if (first) {
      first = false;
      if (isWiredConnected() || isBluetoothConnected()) {
        setSpeakerOn();
      }
    }

    if (!speakerOn) {
      if (isWiredConnected() || isBluetoothConnected()) {
        audioManager.setSpeakerphoneOn(false);
      } else {
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(false);
      }
    } else {
      audioManager.setMode(AudioManager.MODE_NORMAL);
      audioManager.setSpeakerphoneOn(true);
    }

    notifySpeakerStatus();
  }

  @Override
  public void reset() {
    super.reset();
    first = true;
    audioManager.setSpeakerphoneOn(false);
    notifySpeakerStatus();
  }

  public void setSpeakerChangeListener(AudioDeviceManagerListener speakerChangeListener) {
    super.setSpeakerChangeListener(speakerChangeListener);
  }

  public void onDestroy() {
    super.onDestroy();
  }

  private void setSpeakerOn() {
    audioManager.setMode(AudioManager.MODE_NORMAL);
    audioManager.setSpeakerphoneOn(true);
  }

  private void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
    notifySpeakerStatus();
  }

  private void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
    notifySpeakerStatus();
  }

  private boolean isBluetoothConnected() {
    return getAudioDevice(AudioDeviceInfo.TYPE_BLUETOOTH_SCO) != null ||
      getAudioDevice(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) != null;
  }

  private boolean isWiredConnected() {
    return getAudioDevice(AudioDeviceInfo.TYPE_WIRED_HEADPHONES) != null ||
      getAudioDevice(AudioDeviceInfo.TYPE_WIRED_HEADSET) != null;
  }

  private AudioDeviceInfo getAudioDevice(int type) {
    AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
    return Arrays.stream(devices)
      .filter(device -> device.getType() == type)
      .findFirst()
      .orElse(null);
  }

  private void notifySpeakerStatus() {
    boolean status = audioManager.isSpeakerphoneOn();
    Log.d(TAG, "Speaker on: " + status);
    speakerChangeListener.speakerOn(status);
    notified = true;
  }
}
