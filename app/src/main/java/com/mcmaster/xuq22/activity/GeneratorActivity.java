package com.mcmaster.xuq22.activity;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.lang.String;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import com.mcmaster.xuq22.R;
import com.mcmaster.xuq22.process.Generator;

public class GeneratorActivity extends Activity {
	//	private static TableLayout spectrumTable;
//	private static Switch[] freqSwitches = new Switch[Common.FREQUENCY_TO_WATCH];
	private Generator generator;

	//	private MediaPlayer mediaPlayer;
	public void toggleSwitch(View view) {
		if (((Switch) view).isChecked()) {
			if (generator == null || generator.isCancelled()){
				EditText editText = (EditText)this.findViewById(R.id.phoneNumber);
				//String numberStr = editText.getText().toString();
				String hostName = editText.getText().toString();
				String numberStr_real;
				String numberStr = "000000000000";
				//getByName接收的是域名，而不是URL，域名只是URL的一部分
				InetAddress addr;
				try {
					StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);
					addr = InetAddress.getByName(hostName);//注意hostName为域名
					numberStr_real = addr.getHostAddress();
					//numberStr_real="0.0.0.0";
					int length = numberStr_real.length();
					int place_real= length-1;
					int place = 11;
					int dotnum = 0;
					for(int i = 0 ;i <length-3; i++){
						if(numberStr_real.charAt(place_real) == '.'){
							dotnum++;
							place_real--;
							if(dotnum == 1) place = 8;
							if(dotnum == 2) place = 5;
							if(dotnum == 3) place = 2;
						}
						StringBuilder strBuilder = new StringBuilder(numberStr);
						strBuilder.setCharAt(place, numberStr_real.charAt(place_real));
						numberStr=strBuilder.toString();
						place--;
						place_real--;
					}

				}
				catch (UnknownHostException e)
				{
					e.printStackTrace();
				}

				if (numberStr.length() > 0){
					Long phoneNumber = Long.parseLong(numberStr);
					//Long phoneNumber=Long.parseLong("1234567890");
					byte[] arrayToSend = Arrays.copyOfRange(ByteBuffer.allocate(8).putLong(phoneNumber).array(), 3, 8);
					//byte[] arrayToSend = numberStr.getBytes(US-ASCII);
					generator = new Generator(arrayToSend);
					this.generator.execute();

				}else {
					((Switch) view).setChecked(false);
				}
			}

		} else {
			this.generator.cancel(true);
//			mediaPlayer.release();
//			mediaPlayer = null;
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_generator);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.generator, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		AudioManager audioManager;
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_generator,
					container, false);

			//The main logic for volume seekbar, this seekbar is used to control the transmitting power
			audioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);;

			int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			SeekBar volControl = (SeekBar) rootView.findViewById(R.id.volbar);
			volControl.setMax(maxVolume);
			volControl.setProgress(curVolume);
			volControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
				}

				@Override
				public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
				}
			});

			return rootView;
		}
	}

}
