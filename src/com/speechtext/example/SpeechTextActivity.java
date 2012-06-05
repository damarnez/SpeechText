package com.speechtext.example;

import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SpeechTextActivity extends Activity implements TextToSpeech.OnInitListener{
	private static final String TAG = SpeechTextActivity.class.getSimpleName();

    private TextToSpeech mTts;
    private Button mAgainButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Initialize text-to-speech. This is an asynchronous operation.
        // The OnInitListener (second argument) is called after initialization completes.
        mTts = new TextToSpeech(this,
            this  // TextToSpeech.OnInitListener
            );
        String[] textLocale = new String[Locale.getAvailableLocales().length];
        int localeSelected = 0;
        for(int i=0;i<Locale.getAvailableLocales().length;i++){
        	textLocale[i] = Locale.getAvailableLocales()[i].toString();
        	if(Locale.getDefault().equals(Locale.getAvailableLocales()[i])){
        		localeSelected = i;
        	}
        }
        Spinner s = (Spinner) findViewById(R.id.spinner1);
        s.setAdapter(new MyCustomAdapter(this, R.layout.row,textLocale));
        s.setSelection(localeSelected); 
        
        // The button is disabled in the layout.
        // It will be enabled upon initialization of the TTS engine.
        mAgainButton = (Button) findViewById(R.id.again_button);

        mAgainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sayHello();
            }
        });
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }

        super.onDestroy();
    }

    // Implements TextToSpeech.OnInitListener.
    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
             
        	Spinner s = (Spinner) this.findViewById(R.id.spinner1);
        	Log.d(TAG,"Selected item : "+s.getSelectedItemPosition());
            int result = mTts.setLanguage(Locale.getAvailableLocales()[s.getSelectedItemPosition()]);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
               // Lanuage data is missing or the language is not supported.
                Log.e(TAG, "Language is not available.");
            } else {
                // Check the documentation for other possible result codes.
                // For example, the language may be available for the locale,
                // but not for the specified country and variant.

                // The TTS engine has been successfully initialized.
                // Allow the user to press the button for the app to speak again.
                mAgainButton.setEnabled(true);
                // Greet the user.
                sayHello();
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }

    private static final Random RANDOM = new Random();
    private static final String[] HELLOS = {
      "Bienvenido a gootaxi",
      "Pidiendo tax’ espere un momento",
      "Su tax’ esta de camino",
      "le avisaremos cuando llegue su taxi",
      "esto es una demo!",
      "supercalifragilisticoespialidoso siempre lo he querido decir del tir—n"
    };

    private void sayHello() {
        // Select a random hello.
        int helloLength = HELLOS.length;
        String hello = HELLOS[RANDOM.nextInt(helloLength)];
        EditText text = (EditText) this.findViewById(R.id.editText1);
     	Spinner s = (Spinner) this.findViewById(R.id.spinner1);
    	Log.d(TAG,"Selected item : "+s.getSelectedItemPosition());
        int result = mTts.setLanguage(Locale.getAvailableLocales()[s.getSelectedItemPosition()]);
    
        if(text!=null && text.getText().toString()!=""){
        	hello = text.getText().toString();
        }
        mTts.speak(hello,
            TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
            null);
    }
    
    public class MyCustomAdapter extends ArrayAdapter<String>{

    	String[] list = null ;
    	
    	public MyCustomAdapter(Context context, int textViewResourceId,String[] objects) {
    		super(context, textViewResourceId, objects);
    		list = objects;
    	}

    	@Override
    	public View getDropDownView(int position, View convertView,ViewGroup parent) {
    		 return getCustomView(position, convertView, parent);
    	}

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		 return getCustomView(position, convertView, parent);
    	}

    	public View getCustomView(int position, View convertView, ViewGroup parent) {
    		 
    		LayoutInflater inflater=getLayoutInflater();
    		View row=inflater.inflate(R.layout.row, parent, false);
    		TextView label=(TextView)row.findViewById(R.id.row_text);
    		label.setText(list[position]);
    		return row;
    	}
    }
}