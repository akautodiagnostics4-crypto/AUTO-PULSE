package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

object SoundManager {
    private var ringtone: Ringtone? = null
    private var toneGenerator: ToneGenerator? = null

    fun playRideBookingRingSound(context: Context) {
        try {
            // 1. Play Tone Generator for loud alerting chime
            if (toneGenerator == null) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            }
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1500)

            // 2. Play default notification/ringtone sound
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            if (notificationUri != null) {
                ringtone?.stop()
                ringtone = RingtoneManager.getRingtone(context.applicationContext, notificationUri)
                ringtone?.play()
            }

            // 3. Vibrate pattern for driver alert
            vibrateDevice(context)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing ring sound: ${e.message}")
        }
    }

    fun stopRingSound() {
        try {
            ringtone?.stop()
            toneGenerator?.stopTone()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error stopping ring sound: ${e.message}")
        }
    }

    private fun vibrateDevice(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(longArrayOf(0, 400, 200, 400), -1)
                }
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Vibrate error: ${e.message}")
        }
    }
}
