package com.mcmaster.xuq22.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mcmaster.xuq22.R;
import com.mcmaster.xuq22.process.Common;
import com.mcmaster.xuq22.process.Receiver;

@SuppressLint("NewApi")
public class ReceiverActivity extends Activity {
	private static TableLayout spectrumTable;
	private static TextView[] energyTexts = new TextView[Common.FREQUENCY_TO_WATCH];
	private Receiver receiver;
	private Vibrator v;
	
	private static Switch switchWidget;

	public Vibrator getV(){
		return v;
	}

	public void setV(Vibrator v){
		this.v = v;
	}

	public void toggleSwitch(View view) {
		if (receiver == null || receiver.isCancelled()
				|| receiver.getStatus() == AsyncTask.Status.FINISHED)
			receiver = new Receiver(this);
		if (((Switch) view).isChecked()) {
			receiver.execute();
			
		} else {
			if (receiver.getStatus() != AsyncTask.Status.FINISHED)
				receiver.cancel(true);
		}
	}

	public void setReceiverFreqText(Double[] energys) {
		for (int i = 0; i < Common.FREQUENCY_TO_WATCH; i++) {
			energyTexts[i].setText(String.valueOf(energys[i].intValue()));
		}

	}

	public void finished(long result) {
		v.vibrate(300);
		new AlertDialog.Builder(this)
				.setTitle("Result")
				.setMessage(String.valueOf(result))
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
		switchWidget.setChecked(false);
		//SmsManager smsManager = SmsManager.getDefault();
		//smsManager.sendTextMessage(String.valueOf(result), null,"Yes!", null, null);
		long r1, r2, r3, r4;
		r1 = result % 1000;
		r2 = (result / 1000) % 1000;
		r3 = (result /1000000) % 1000;
		r4 = (result / 1000000000) % 1000;
		String op1, op2, op3, op4, op;
		op1 = String.valueOf(r4);
		op2 = String.valueOf(r3);
		op3 = String.valueOf(r2);
		op4 = String.valueOf(r1);
		op = "http://" + op1 + "." + op2 + "." + op3 + "." + op4;
		Uri uri = Uri.parse(op);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receiver);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.receiver, menu);
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

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_receiver,
					container, false);
			spectrumTable = (TableLayout) rootView
					.findViewById(R.id.spectrumTable);
			switchWidget = (Switch)rootView.findViewById(R.id.switch1);
			TableRow row;
			TextView freqText;
			for (int i = 0; i < Common.FREQUENCY_TO_WATCH; i++) {
				row = new TableRow(getActivity());
				freqText = new TextView(getActivity());
				energyTexts[i] = new TextView(getActivity());
				freqText.setText(String.valueOf(Common.frequencys[i]));
				energyTexts[i].setText("0");
				row.addView(freqText);
				row.addView(energyTexts[i]);
				spectrumTable.addView(row, i + 1);
			}
			return rootView;
		}

	}

}
