import Foundation
import Capacitor
import Foundation
import AVFoundation
/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(AudioTogglePlugin)
public class AudioTogglePlugin: CAPPlugin {
    private let implementation = AudioToggle()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
                let session: AVAudioSession = AVAudioSession.sharedInstance();
        if (value == "false") {
                do {
                    print("AUDIO MODE: \(value)");
                    try session.setCategory(.playAndRecord, mode: .voiceChat, options:[.interruptSpokenAudioAndMixWithOthers])
                    try session.setActive(true);
                    try session.overrideOutputAudioPort(.none);
                    call.resolve([
                        "mode":"EARPIECE"
                    ]);
                } catch let error as NSError {
                    call.reject("ERROR \(value):  \(error.localizedDescription)")
                }
            } else {
                do {
                    print("AUDIO MODE: \(value)");
                    try session.setCategory(.playAndRecord, mode: .voiceChat, options:[.interruptSpokenAudioAndMixWithOthers, .defaultToSpeaker]);
                    try session.setActive(true);
                    try session.overrideOutputAudioPort(.speaker);
                  call.resolve([
                        "mode":"SPEAKER"
                    ]);
                } catch let error as NSError {
                    call.reject("ERROR \(value):  \(error.localizedDescription)");
                }
            }
    }
}
