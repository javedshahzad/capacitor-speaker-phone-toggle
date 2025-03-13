package ar.com.anura.plugins.audiotoggle.audiodevicemanager;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.S_V2) // Targeting Android 12L
public class AudioDeviceManager32 extends AudioDeviceManager implements AudioDeviceManagerInterface {

    private static final long INTERVAL = 500;
    private boolean turnOn = false;
    private Timer timer;
    private TimerTask task;
    private final AudioManager audioManager;

    public AudioDeviceManager32(final Context context) {
        super(context);
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        registerAudioDeviceCallbacks(this::onAudioDevicesAdded, this::onAudioDevicesRemoved);
    }

    public void setSpeakerOn(boolean speakerOn) {
        this.turnOn = speakerOn;
        stopTimer(); // Stop any existing timer before setting a new one

        if (audioManager != null) {
            setAudioFocus(); // Ensure proper audio focus

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Use new setCommunicationDevice API for Android 12+
                AudioDeviceInfo speakerDevice = findSpeakerDevice();
                if (speakerDevice != null) {
                    audioManager.setCommunicationDevice(speakerDevice);
                }
            } else {
                // Fallback for older versions
                audioManager.setSpeakerphoneOn(turnOn);
                audioManager.setMode(AudioManager.MODE_NORMAL);
            }

            notifySpeakerStatus();
        }
    }

    public void reset() {
        stopTimer();
        super.reset();
        notifySpeakerStatus();
    }

    public void onDestroy() {
        stopTimer();
        unregisterAudioDeviceCallbacks(); // Unregister callbacks for cleanup
        super.onDestroy();
    }

    private void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
        notifySpeakerStatus();
    }

    private void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
        notifySpeakerStatus();
    }

    private void notifySpeakerStatus() {
        if (speakerChangeListener != null) { // Null check added
            speakerChangeListener.speakerOn(audioManager.isSpeakerphoneOn());
        }
    }

    private void stopTimer() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void setAudioFocus() {
        // Implement proper audio focus handling if needed
    }

    private AudioDeviceInfo findSpeakerDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            for (AudioDeviceInfo device : audioManager.getAvailableCommunicationDevices()) {
                if (device.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                    return device;
                }
            }
        }
        return null;
    }
}
