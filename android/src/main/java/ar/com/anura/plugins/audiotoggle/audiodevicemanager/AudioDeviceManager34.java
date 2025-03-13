package ar.com.anura.plugins.audiotoggle.audiodevicemanager;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.AudioFocusRequest;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE) // Android 14 compatibility
public class AudioDeviceManager34 extends AudioDeviceManager implements AudioDeviceManagerInterface {

    private static final long INTERVAL = 500;
    private boolean turnOn = false;
    private final AudioManager audioManager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable speakerTask;

    private final OnAudioFocusChangeListener audioFocusChangeListener = focusChange -> {
        // Handle audio focus changes (if needed)
    };

    public AudioDeviceManager34(final Context context) {
        super(context);
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        registerAudioDeviceCallbacks(this::onAudioDevicesAdded, this::onAudioDevicesRemoved);
    }

    public void setSpeakerOn(boolean speakerOn) {
        this.turnOn = speakerOn;
        stopTask(); // Ensure no duplicate tasks

        if (audioManager != null) {
            requestAudioFocus(); // Request proper audio focus

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Use setCommunicationDevice for newer versions
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

        // Schedule periodic checks using Handler
        speakerTask = () -> {
            if (audioManager != null) {
                notifySpeakerStatus();
            }
            handler.postDelayed(speakerTask, INTERVAL);
        };
        handler.postDelayed(speakerTask, INTERVAL);
    }

    public void reset() {
        stopTask();
        super.reset();
        notifySpeakerStatus();
    }

    public void onDestroy() {
        stopTask();
        unregisterAudioDeviceCallbacks(); // Cleanup audio device callbacks
        super.onDestroy();
    }

    private void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
        notifySpeakerStatus();
    }

    private void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
        notifySpeakerStatus();
    }

    private void notifySpeakerStatus() {
        if (speakerChangeListener != null) { // Avoid null pointer exception
            speakerChangeListener.speakerOn(audioManager.isSpeakerphoneOn());
        }
    }

    private void stopTask() {
        if (speakerTask != null) {
            handler.removeCallbacks(speakerTask);
            speakerTask = null;
        }
    }

    private void requestAudioFocus() {
        AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build();
        audioManager.requestAudioFocus(focusRequest);
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
