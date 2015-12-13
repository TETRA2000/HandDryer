//
//  ViewController.swift
//  Hand Dryer
//
//  Created by takahikoinayama on 9/27/14.
//  Copyright (c) 2014 TETRA2000. All rights reserved.
//

import UIKit
import AVFoundation

class ViewController: UIViewController, AVAudioPlayerDelegate {
    private var audioSession: AVAudioSession?
    private var player: AVAudioPlayer?

    override func viewDidLoad() {
        super.viewDidLoad()
        
        // keep screen awake
        UIApplication.sharedApplication().idleTimerDisabled = true
        
        configureAudioSession()
        
        setupSound()
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        
        startMonitorProximity()
    }
    
    override func viewWillDisappear(animated: Bool) {
        stopMonitorProximity()
        
        super.viewWillDisappear(animated)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    private func startMonitorProximity() {
        UIDevice.currentDevice().proximityMonitoringEnabled = true;
        NSNotificationCenter.defaultCenter().addObserver(self, selector: Selector("proximityChanged"), name: "UIDeviceProximityStateDidChangeNotification", object: nil);
    }
    
    private func stopMonitorProximity() {
        NSNotificationCenter.defaultCenter().removeObserver(self)
        
        UIDevice.currentDevice().proximityMonitoringEnabled = false;
    }
    
    func proximityChanged() {
        if UIDevice.currentDevice().proximityState {
            self.player!.play()
        } else {
            self.player!.stop()
        }
    }
    
    func configureAudioSession() {
        audioSession = AVAudioSession.sharedInstance()
        
        do {
            try audioSession!.setCategory(AVAudioSessionCategoryPlayback)
        } catch is NSError {
            // TODO: DO SOMETHINGT
            abort()
        }
    }
    
    func setupSound() {
        let path = NSBundle.mainBundle().pathForResource("dryer", ofType: "m4a")
        let url = NSURL(fileURLWithPath: path!)
        do {
            try self.player = AVAudioPlayer(contentsOfURL: url)

            
        }catch is NSError {
            // FIXME: Do something
            abort()
        }
        
        self.player!.delegate = self
        self.player!.numberOfLoops = -1
        self.player!.prepareToPlay()
    }
    
    // AVAudioPlayerDelegate:
    
    func audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully flag: Bool) {
        // TODO:
    }
    
}

