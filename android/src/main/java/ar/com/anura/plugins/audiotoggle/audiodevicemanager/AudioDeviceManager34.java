package ar.com.anura.plugins.audiotoggle.audiodevicemanager;

import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
public class AudioDeviceManager34
  extends AudioDeviceManager
  implements AudioDeviceManagerInterface, AudioManager.OnCommunicationDeviceChangedListener, AudioManager.OnModeChangedListener {

  AudioDeviceManager34(final AppCompatActivity activity) {
    super(activity);
    registerAudioDeviceCallbacks(this::onAudioDevicesAdded, this::onAudioDevicesRemoved);
    audioManager.addOnCommunicationDeviceChangedListener(activity.getMainExecutor(), this);
    audioManager.addOnModeChangedListener(activity.getMainExecutor(), this);
  }

  final long delay = 0;
  final long interval = 500;
  boolean turnOn = false;
  Timer timer;
  TimerTask task;

  @Override
  public void setSpeakerOn(boolean speakerOn) {
    this.turnOn = speakerOn;

    if (timer == null) {
      timer = new Timer();
      task = new TimerTask() {
        @Override
        public void run() {
          setAudioFocus(0);
          boolean success = false;

          if (!turnOn) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

            AudioDeviceInfo bluetoothScoDevice = getAudioDevice(AudioDeviceInfo.TYPE_BLUETOOTH_SCO);
            AudioDeviceInfo bluetoothAD2PDevice = getAudioDevice(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);
            if (bluetoothScoDevice != null) {
              success = audioManager.setCommunicationDevice(bluetoothScoDevice);
            } else if (bluetoothAD2PDevice != null) {
              success = audioManager.setCommunicationDevice(bluetoothAD2PDevice);
            }

            if (success) {
              audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
              return;
            } else {
              Log.d(TAG, "Bluetooth error");
              notifySpeakerStatus();
            }

            AudioDeviceInfo wiredDevice = getWiredAudioDevice();
            if (wiredDevice != null) {
              success = audioManager.setCommunicationDevice(wiredDevice);
            }

            if (success) {
              audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
              return;
            } else {
              Log.d(TAG, "Wired error");
              notifySpeakerStatus();
            }

            AudioDeviceInfo earpieceDevice = getAudioDevice(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE);
            if (earpieceDevice != null) {
              success = audioManager.setCommunicationDevice(earpieceDevice);
              if (success) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
              } else {
                Log.d(TAG, "Earpiece error");
                notifySpeakerStatus();
              }
            }
          } else {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            AudioDeviceInfo speakerphoneDevice = getAudioDevice(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
            if (speakerphoneDevice != null) {
              success = audioManager.setCommunicationDevice(speakerphoneDevice);
              if (success) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
              } else {
                Log.d(TAG, "Speaker error");
                notifySpeakerStatus();
              }
            }
          }
        }
      };

      timer.scheduleAtFixedRate(task, delay, interval);
    }
  }

  @Override
  public void reset() {
    stopTimer();
    super.reset();
    audioManager.clearCommunicationDevice();
    notifySpeakerStatus();
  }

  public void onDestroy() {
    stopTimer();
    super.onDestroy();
    audioManager.removeOnCommunicationDeviceChangedListener(this);
    audioManager.removeOnModeChangedListener(this);
  }

  public void setSpeakerChangeListener(AudioDeviceManagerListener speakerChangeListener) {
    super.setSpeakerChangeListener(speakerChangeListener);
  }

  @Override
  public void onCommunicationDeviceChanged(@Nullable AudioDeviceInfo audioDeviceInfo) {
    if (audioDeviceInfo != null) {
      Log.d(TAG, "Device changed: " + audioDeviceInfo.getType());
    }
    showCurrentAudioDevice();
    notifySpeakerStatus();
  }

  private void showCurrentAudioDevice() {
    AudioDeviceInfo currentDevice = audioManager.getCommunicationDevice();
    if (currentDevice != null) {
      Log.d(TAG, "Current device: " + currentDevice);
    }
  }

  @Override
  public void onModeChanged(int iMode) {
    showMode(iMode);
    notifySpeakerStatus();
  }

  private void showMode(int iMode) {
    String strMode = switch (iMode) {
      case AudioManager.MODE_NORMAL -> "NORMAL";
      case AudioManager.MODE_IN_COMMUNICATION -> "IN_COMMUNICATION";
      default -> "Other: " + iMode;
    };
    Log.d(TAG, "Mode changed: " + strMode);
  }

  private AudioDeviceInfo getAudioDevice(Integer type) {
    List<AudioDeviceInfo> devices = audioManager.getAvailableCommunicationDevices();
    for (AudioDeviceInfo device : devices) {
      if (type.equals(device.getType())) return device;
    }
    return null;
  }

  private AudioDeviceInfo getWiredAudioDevice() {
    AudioDeviceInfo wiredHeadphones = getAudioDevice(AudioDeviceInfo.TYPE_WIRED_HEADPHONES);
    AudioDeviceInfo wiredHeadset = getAudioDevice(AudioDeviceInfo.TYPE_WIRED_HEADSET);
    return wiredHeadphones != null ? wiredHeadphones : wiredHeadset;
  }

  private void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
    notifySpeakerStatus();
  }

  private void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
    notifySpeakerStatus();
  }

  private void notifySpeakerStatus() {
    boolean status = false;
    AudioDeviceInfo communicationDevice = audioManager.getCommunicationDevice();
    if (communicationDevice != null) {
      status = communicationDevice.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER;
    }
    Log.d(TAG, "Speaker status: " + status);
    speakerChangeListener.speakerOn(status);
  }

  private void stopTimer() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }
}
