package ar.com.anura.plugins.audiotoggle;

import static android.media.AudioManager.MODE_IN_COMMUNICATION;

import androidx.appcompat.app.AppCompatActivity;
import ar.com.anura.plugins.audiotoggle.audiodevicemanager.AudioDeviceManagerInterface;
import ar.com.anura.plugins.audiotoggle.audiodevicemanager.AudioDeviceManagerListener;
import ar.com.anura.plugins.audiotoggle.audiodevicemanager.AudioDeviceManagerService;

public class AudioToggle {

    final AppCompatActivity activity;
    private int savedStreamVolume;
    private AudioDeviceManagerInterface audioManager;

    AudioToggle(final AppCompatActivity activity) {
        this.activity = activity;
        this.savedStreamVolume = activity.getVolumeControlStream();
        this.audioManager = AudioDeviceManagerService.get(activity);
    }

    public void setAudioToggleEventListener(AudioDeviceManagerListener listener) {
        audioManager.setSpeakerChangeListener(listener);
    }

    public void setSpeakerOn(boolean turnOn) {
        audioManager.setSpeakerOn(turnOn);
        activity.setVolumeControlStream(MODE_IN_COMMUNICATION);
    }

    public void reset() {
        audioManager.reset();
        activity.setVolumeControlStream(savedStreamVolume);
    }

    public void onDestroy() {
        audioManager.onDestroy();
    }
}
