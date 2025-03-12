import Foundation
import Capacitor
import Foundation
import AVFoundation

@objc public class AudioToggle: NSObject {
    @objc public func echo(_ value: String) -> String {
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
