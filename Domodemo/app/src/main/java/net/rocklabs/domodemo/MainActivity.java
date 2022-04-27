package net.rocklabs.domodemo;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
	private static final int TTS_REQUEST_CODE = 2;
	private static final String OK = "Hola Jorge, necesitas algo?";
	private static final String TAG = "MainActivity";
	private Boolean initVoice = true;

	private HashMap<String, String> myHashAlarm = new HashMap<String, String>();

	private ImageButton btnOkMajo;
	private TextView txtPhrase;
	private EditText txt_IP;
	private ProgressBar pbdB;

	public String phrase;

	//Utils util = new Utils();

	public TextToSpeech tts;
	public String respTTS;

	private SpeechRecognizer sr;

	private Intent recogizerIntent;

	public MainActivity() {
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		btnOkMajo = (ImageButton)findViewById(R.id.btnOkMajo);
		txtPhrase = (TextView)findViewById(R.id.textPhrase);
		pbdB = (ProgressBar)findViewById(R.id.pbdB);

		sr = SpeechRecognizer.createSpeechRecognizer(this);
		sr.setRecognitionListener(new MyRecognitionListener());

		tts = new TextToSpeech(this,
			new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int initStatus) {
					if (initStatus == TextToSpeech.SUCCESS) {
						Log.i(TAG, "OnInit initStatus OK");
//						if (tts.isLanguageAvailable(new Locale("es", "ES")) == TextToSpeech.LANG_AVAILABLE) {
//							Log.i(TAG, "OnInit: SetLanguage OK");
//							tts.setLanguage(new Locale("es", "ES"));
					} else if (initStatus == TextToSpeech.ERROR)
						Log.i(TAG, "OnInit: setLanguage FAIL");
					else
						Log.i(TAG, "OnInit FAIL");
				}
			});

		tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
			@Override
			public void onUtteranceCompleted(String s) {
				Log.d(TAG, "onUtteranceCompleted: " + s);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.i(TAG, "runOnUiThread");
						sr.startListening(recogizerIntent);
					}
				});

			}
		});

		btnOkMajo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startRecognitionActivity();
				//view.setClickable(false);
				view.setEnabled(false);
				myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "OK");
				tts.speak(OK, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
			}
		});
    }


	class MyRecognitionListener implements RecognitionListener {
		public void onReadyForSpeech(Bundle params)
		{
			Log.d(TAG, "onReadyForSpeech");
		}
		public void onBeginningOfSpeech()
		{
			Log.d(TAG, "onBeginningOfSpeech");
			pbdB.setIndeterminate(false);
			pbdB.setMax(10);
		}
		public void onRmsChanged(float rmsdB)
		{
//			Log.d(TAG, "onRmsChanged: " + rmsdB);
			pbdB.setProgress((int)rmsdB);
		}
		public void onBufferReceived(byte[] buffer)
		{
			Log.d(TAG, "onBufferReceived");
		}
		public void onEndOfSpeech()
		{
			Log.d(TAG, "onEndOfSpeech: ");
		}
		public void onError(int error)
		{
			Log.d(TAG,  "error " +  error);
			txtPhrase.setText("error " + error);
			btnOkMajo.setEnabled(true);
		}
		public void onResults(Bundle results)
		{
			//Log.d(TAG, "onResults " + results.toString());
			ArrayList<String> datas = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			txtPhrase.setText("results: " + datas.get(0));
			captureTextIntent(datas != null ? datas.get(0) : null);
		}
		public void onPartialResults(Bundle partialResults)
		{
			Log.d(TAG, "onPartialResults");
		}
		public void onEvent(int eventType, Bundle params)
		{
			Log.d(TAG, "onEvent " + eventType);
		}

	}

	private void startTTSActivity(){
		Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(intent, TTS_REQUEST_CODE);

	}

	private void startRecognitionActivity(){
		recogizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		recogizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		recogizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
		recogizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Majo, + action");
		recogizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		recogizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);

		//startActivityForResult(recogizerIntent, VOICE_RECOGNITION_REQUEST_CODE);
		//sr.startListening(recogizerIntent);

	}

	@Override
	protected void onPause(){
		super.onPause();



	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if(tts != null) {
			Log.i(TAG, "OnDestroy: OK");
			tts.stop();
			tts.shutdown();
		}
		if (sr != null){
			sr.stopListening();
			sr.cancel();
			sr.destroy();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "resultCode: " + resultCode);

		switch (requestCode) {
//			case VOICE_RECOGNITION_REQUEST_CODE: {
//				Log.i(TAG, "OnActivityResult: VOICE_RECOGNITION_REQUEST_CODE");
//				if (resultCode == RESULT_OK) {
//					ArrayList<String> datas = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//					captureTextIntent(datas != null ? datas.get(0) : null);
//				} else
//					Log.i("TAG_REC", "OnActivityResult resultCode FAIL");
//			}
			case TTS_REQUEST_CODE: {
				Log.i(TAG, "OnActivityResult: TSS_REQUEST_CODE");
				if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
					tts.speak(respTTS, TextToSpeech.QUEUE_FLUSH, null);
					tts.playSilence(500, TextToSpeech.QUEUE_ADD, myHashAlarm);

				} else {
					Intent installIntent = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
					startActivity(installIntent);
				}
			}
			default:
				Log.i(TAG, "OnActivityResult requestCode FAIL");

			}
	}

	public void captureTextIntent(String text) {
		if (text == null)
			Log.d(TAG, "Text NULL");
		RequestTask request = new RequestTask("TOKEN") {
			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					Log.d(TAG, "Result: " + result);
					respTTS = result;
					startTTSActivity();
				} else
					Log.i(TAG, "onPostExecute FAIL");
			}
		};
		request.execute(text);
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
