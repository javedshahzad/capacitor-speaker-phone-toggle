package ar.com.anura.plugins.audiotoggle.audiodevicemanager;

import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.S_V2)
public class AudioDeviceManager32
  extends AudioDeviceManager
  implements AudioDeviceManagerInterface, AudioManager.OnCommunicationDeviceChangedListener, AudioManager.OnModeChangedListener {

  private static final String TAG = "AudioDeviceManager31";
  private boolean first = true;
  private boolean notified = false;
  private final Handler handler = new Handler(Looper.getMainLooper());

  AudioDeviceManager32(final AppCompatActivity activity) {
    super(activity);
    registerAudioDeviceCallbacks(this::onAudioDevicesAdded, this::onAudioDevicesRemoved);
    audioManager.addOnCommunicationDeviceChangedListener(activity.getMainExecutor(), this);
    audioManager.addOnModeChangedListener(activity.getMainExecutor(), this);
  }

  @Override
  public void setSpeakerOn(boolean speakerOn) {
    notified = false;
    handler.postDelayed(this::notifySpeakerStatus, 3000);

    super.setAudioFocus(0);

    boolean success;
    if (!speakerOn) {
      AudioDeviceInfo earpieceDevice = getAudioDevice(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE);
      if (earpieceDevice != null) {
        success = audioManager.setCommunicationDevice(earpieceDevice);
        if (!success) {
          Log.e(TAG, "Failed to switch to earpiece mode");
          notifySpeakerStatus();
        }
      } else {
        Log.e(TAG, "No earpiece device found");
      }
    } else {
      setSpeakerOnInternal();
    }
  }

  @Override
  public void reset() {
    super.reset();
    first = true;
    audioManager.clearCommunicationDevice();
    notifySpeakerStatus();
  }

  public void setSpeakerChangeListener(AudioDeviceManagerListener speakerChangeListener) {
    super.setSpeakerChangeListener(speakerChangeListener);
  }

  public void onDestroy() {
    super.onDestroy();
    audioManager.removeOnCommunicationDeviceChangedListener(this);
    audioManager.removeOnModeChangedListener(this);
    handler.removeCallbacksAndMessages(null);
  }

  private void setSpeakerOnInternal() {
    audioManager.setMode(AudioManager.MODE_NORMAL);
    AudioDeviceInfo speakerDevice = getAudioDevice(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
    if (speakerDevice != null) {
      boolean success = audioManager.setCommunicationDevice(speakerDevice);
      if (!success) {
        Log.e(TAG, "Failed to switch to speaker mode");
        notifySpeakerStatus();
      }
    }
  }

  @Override
  public void onCommunicationDeviceChanged(@Nullable AudioDeviceInfo audioDeviceInfo) {
    Log.d(TAG, "Communication device changed: " + (audioDeviceInfo != null ? audioDeviceInfo.getType() : "null"));
    notifySpeakerStatus();
  }

  @Override
  public void onModeChanged(int iMode) {
    Log.d(TAG, "Mode changed: " + getModeString(iMode));
    notifySpeakerStatus();
  }

  private String getModeString(int iMode) {
    return switch (iMode) {
      case AudioManager.MODE_NORMAL -> "NORMAL";
      case AudioManager.MODE_IN_COMMUNICATION -> "IN_COMMUNICATION";
      default -> "Other: " + iMode;
    };
  }

  private AudioDeviceInfo getAudioDevice(int type) {
    List<AudioDeviceInfo> devices = audioManager.getAvailableCommunicationDevices();
    for (AudioDeviceInfo device : devices) {
      if (device.getType() == type) {
        return device;
      }
    }
    return null;
  }

  private void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
    notifySpeakerStatus();
  }

  private void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
    notifySpeakerStatus();
  }

  private boolean isWiredConnected() {
    return getAudioDevice(AudioDeviceInfo.TYPE_WIRED_HEADPHONES) != null ||
      getAudioDevice(AudioDeviceInfo.TYPE_WIRED_HEADSET) != null;
  }

  private void notifySpeakerStatus() {
    boolean status = false;
    AudioDeviceInfo communicationDevice = audioManager.getCommunicationDevice();
    if (communicationDevice != null) {
      status = communicationDevice.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER;
    }
    Log.d(TAG, "Speaker status: " + status);
    speakerChangeListener.speakerOn(status);
    notified = true;
  }
}
