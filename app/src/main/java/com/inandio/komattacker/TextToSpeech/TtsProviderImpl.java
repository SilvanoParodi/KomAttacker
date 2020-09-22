package com.inandio.komattacker.TextToSpeech;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.inandio.komattacker.Settings;

import java.util.Locale;

/**
 * Created by parodi on 13/10/2015.
 */
public class TtsProviderImpl extends TtsProviderFactory implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;

    public void init(Context context) {
        if (tts == null) {
            tts = new TextToSpeech(context, this);
        }
    }

    @Override
    public void say(String sayThis) {
        if (Settings.useVocalAssistant)
        {
            tts.speak(sayThis, TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.ITALY);
        say("Init sintesi vocale");
    }

    public void shutdown() {
        tts.shutdown();
    }}