package com.savvy.floatingwindowexample;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private int K10_ONOFF = 0;
	private int  K10_AUTO_WATER = 1;
	private int  K10_PUMP_WATER = 2;
	private int  K10_HEATING = 3;
	private int  K10_TEMP = 4;
	private int state_onoff = 0;
	private int state_auto_pump = 0;
	private int state_pump = 0;
	private int state_heat = 0;

	private int highest = 100;
	private int intArray[] = new int[7];

	private ImageView onoff;
	private ImageView auto_pump;
	private TextView temp,ccc;
	private ImageView pump;
	private ImageView heat;
	private int tempp,heating_temp;
	private boolean isfinished = false,ispaused = false ,heating = false,noAlert = true;
	private RelativeLayout forcolor;
	private ImageView cir_dot;

	private AlertDialog boil_err;

	Handler temp_hand, heat_hand;

	static {
		System.loadLibrary("gpio");
	}
	public native void water_ops(int ops,int state);
	public native int get_temp(int state);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.v(TAG, "onCreate main_land");
		setContentView(R.layout.main_land);

		Bundle bundle = getIntent().getExtras();

		/*if(bundle != null && bundle.getString("LAUNCH").equals("YES")) {
			startService(new Intent(MainActivity.this, FloatingWindow.class));
		}*/

		//check time
		GetBunch a = new GetBunch();
	//	if((a.getData())) finish();

		Button launch = (Button)findViewById(R.id.button1);
		launch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startService(new Intent(MainActivity.this, FloatingWindow.class));
			}
		});

		Button stop = (Button)findViewById(R.id.button2);
		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopService(new Intent(MainActivity.this, FloatingWindow.class));
			}
		});

		onoff = (ImageView) findViewById(R.id.onoff);
		//onoff.setOnClickListener(mOnClickListener);
		onoff.setOnLongClickListener(new OnOffOnLongClick());
		auto_pump = (ImageView) findViewById(R.id.auto_pump);
		auto_pump.setOnClickListener(mOnClickListener);
		pump = (ImageView) findViewById(R.id.pump);
		pump.setOnClickListener(mOnClickListener);
		heat = (ImageView) findViewById(R.id.heat);
		heat.setOnClickListener(mOnClickListener);
		temp = (TextView) findViewById(R.id.temp);
		ccc = (TextView) findViewById(R.id.ccc);
		cir_dot = (ImageView) findViewById(R.id.frame_image1);

		Button white = (Button) findViewById(R.id.white);
		white.setOnClickListener(mOnClickListener);
		white.setVisibility(View.GONE);
		Button darkblue = (Button) findViewById(R.id.darkblue);
		darkblue.setOnClickListener(mOnClickListener);
		darkblue.setVisibility(View.GONE);
		Button navy = (Button) findViewById(R.id.navy);
		navy.setOnClickListener(mOnClickListener);
		navy.setVisibility(View.GONE);
		forcolor = (RelativeLayout) findViewById(R.id.forcolor);

		auto_pump.setEnabled(false);
		pump.setEnabled(false);
		heat.setEnabled(false);

		//
		AssetManager assets = getAssets();
		//final Typeface font = Typeface.createFromAsset(assets, "digital-7.ttf");
		//final Typeface font = Typeface.createFromAsset(assets, "digital-7 (italic).ttf");
		//final Typeface font = Typeface.createFromAsset(assets, "digital-monoitalic.ttf");
		final Typeface font = Typeface.createFromAsset(assets, "digital-7 (mono).ttf");
		temp.setTypeface(font);// 设置字体
		ccc.setTypeface(font);
		temp.setText("000");
		temp.setTextColor(getResources().getColor(R.color.LIGHTGRAY));
		ccc.setTextColor(getResources().getColor(R.color.LIGHTGRAY));
		cir_dot.setColorFilter(getResources().getColor(R.color.LIGHTGRAY));

		temp_hand = new Handler();
		heat_hand = new Handler();
		//

		intArray[0] = 40;
		intArray[1] = 100;
		intArray[2] = 90;
		intArray[3] = 80;
		intArray[4] = 70;
		intArray[5] = 60;
		intArray[6] = 50;

	/*	boil_err = new AlertDialog.Builder(this).//(new ContextThemeWrapper(this,R.style.CustomDialog)).
			//	setTitle("WARNING").
				setMessage("OK").
				setPositiveButton("水壶异常", null).
				create();*/
		boil_err = new AlertDialog.Builder(this,R.style.CustomDialog).create();
		//boil_err.show();
		//boil_err.getWindow().setContentView(R.layout.boil_err);
		//boil_err.dismiss();

/*		boil_err.setOnShowListener(new DialogInterface.OnShowListener() {
			//private Button negativeBtn ;
			private Button positiveBtn;
			@Override
			public void onShow(DialogInterface dialogInterface) {
				//设置button文本大小
				positiveBtn = ((AlertDialog) boil_err).getButton(DialogInterface.BUTTON_POSITIVE);
				//negativeBtn = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
				positiveBtn.setTextSize(60);
				//negativeBtn.setTextSize(20);
			}
		});*/


		try {
			pump.setOnTouchListener(new View.OnTouchListener() {
				@Override public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							Log.v(TAG, "ACTION_DOWN");
							pump.setImageResource(R.drawable.pump_on);

							if(state_auto_pump == 1) {
								state_auto_pump = 0;
								auto_pump.setImageResource(R.drawable.auto_pump_off);
								water_ops(K10_AUTO_WATER, state_auto_pump);
							}

							water_ops(K10_PUMP_WATER,1);
							break;
						case MotionEvent.ACTION_UP:
							Log.v(TAG, "ACTION_UP");
							pump.setImageResource(R.drawable.pump_off);
							water_ops(K10_PUMP_WATER,0);
							break;
					}
					return false;
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onResume() {
	/*	Bundle bundle = getIntent().getExtras();

		if(bundle != null && bundle.getString("LAUNCH").equals("YES")) {
			startService(new Intent(MainActivity.this, FloatingWindow.class));
		}*/
		Log.v(TAG, "onResume:");
		startService(new Intent(MainActivity.this, FloatingWindow.class));
		super.onResume();
	}
	@Override
	protected void onPause() {
		Log.v(TAG, "onPause:");
		//Log.v(TAG, "onPause:isFinishing=" + isFinishing());
		ispaused = true;
		//if(temp_hand != null) temp_hand.removeCallbacks(run_t);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy:");
		isfinished = true;
		if(temp_hand != null) temp_hand.removeCallbacks(run_t);
		if(heat_hand != null) heat_hand.removeCallbacks(run_heat);
		super.onDestroy();
	}

		private class OnOffOnLongClick implements View.OnLongClickListener {
			@Override
			public boolean onLongClick(View view){
				try{
					Log.v(TAG, "onoff");
					if(state_onoff == 0) {
						state_onoff = 1;
						onoff.setImageResource(R.drawable.on);
						auto_pump.setEnabled(true);
						pump.setEnabled(true);
						heat.setEnabled(true);

						getTemp();
					}
					else{
						state_onoff = 0;
						onoff.setImageResource(R.drawable.off);
						if(state_auto_pump == 1) {
							state_auto_pump = 0;
							auto_pump.setImageResource(R.drawable.auto_pump_off);
							water_ops(K10_AUTO_WATER, state_auto_pump);
						}
						//if(state_heat > 0){
						if(heating == true){
							state_heat = 0;
							heating = false;
							heat.setImageResource(R.drawable.heat_off);
							water_ops(K10_HEATING,state_heat);
						}
						if(temp_hand != null) {
							temp_hand.removeCallbacks(run_t);
							temp.setText("000");
							temp.setTextColor(getResources().getColor(R.color.LIGHTGRAY));
							ccc.setTextColor(getResources().getColor(R.color.LIGHTGRAY));
							cir_dot.setColorFilter(getResources().getColor(R.color.LIGHTGRAY));
						}
						auto_pump.setEnabled(false);
						pump.setEnabled(false);
						heat.setEnabled(false);
					}
					water_ops(K10_ONOFF,state_onoff);
				}
				catch(Exception e){
				}
				return true;
			}
		}

		private final OnClickListener mOnClickListener = new OnClickListener() {
		Handler auto_pm = new Handler();
		@Override
		public void onClick(final View v) {
			Log.v(TAG, "onClick: " + v.getId());
			switch (v.getId()) {
				case R.id.onoff:
		/*			Log.v(TAG, "onoff");
					if(state_onoff == 0) {
						state_onoff = 1;
						onoff.setImageResource(R.drawable.on);
						auto_pump.setEnabled(true);
						pump.setEnabled(true);
						heat.setEnabled(true);

						getTemp();
					}
					else{
						state_onoff = 0;
						onoff.setImageResource(R.drawable.off);
						if(state_auto_pump == 1) {
							state_auto_pump = 0;
							auto_pump.setImageResource(R.drawable.auto_pump_off);
							water_ops(K10_AUTO_WATER, state_auto_pump);
						}
						if(state_heat == 1){
							state_heat = 0;
							heat.setImageResource(R.drawable.heat_off);
							water_ops(K10_HEATING,state_heat);
						}
						if(temp_hand != null) {
							temp_hand.removeCallbacks(run_t);
							temp.setText("000");
							temp.setTextColor(getResources().getColor(R.color.LIGHTGRAY));
							ccc.setTextColor(getResources().getColor(R.color.LIGHTGRAY));
							cir_dot.setColorFilter(getResources().getColor(R.color.LIGHTGRAY));
						}
						auto_pump.setEnabled(false);
						pump.setEnabled(false);
						heat.setEnabled(false);
					}
					water_ops(K10_ONOFF,state_onoff);*/
					break;
				case R.id.auto_pump:
					Log.v(TAG, "auto_pump");
					if(auto_pm != null) auto_pm.removeCallbacks(r);
					if(state_auto_pump == 0) {
						state_auto_pump = 1;
						auto_pump.setImageResource(R.drawable.auto_pump_on);

						auto_pm.postDelayed(r, 20*1000);
					}
					else{
						state_auto_pump = 0;
						auto_pump.setImageResource(R.drawable.auto_pump_off);
					}
					water_ops(K10_AUTO_WATER,state_auto_pump);
					break;
				case R.id.heat:
					state_heat = (state_heat+1)%7;
					Log.v(TAG, "state_heat = " + state_heat);
					/*if(state_heat == 0){// && heating == false) {
						heating = false;
						heating_temp = 0;
						if(heat_hand != null) heat_hand.removeCallbacks(run_heat);
						heat.setImageResource(R.drawable.heat_off);
						water_ops(K10_HEATING,state_heat);
					}
					else if(heating == false && state_heat > 0){
						heat.setImageResource(R.drawable.heat_on);
						if(heating == false) {
							if(heat_hand != null) heat_hand.removeCallbacks(run_heat);
							heat_hand.postDelayed(run_heat, 2*1000);
						}
					}*/

					if(heating == false) {
						heat.setImageResource(R.drawable.heat_on);
						if(heat_hand != null) heat_hand.removeCallbacks(run_heat);
						heat_hand.postDelayed(run_heat, 2*1000);
					}

				//	if(state_heat > 0){
						int ttm;
						//if(heating == false)
							ttm = intArray[state_heat];
						//else
						//	ttm = heating_temp;

						Log.v(TAG, "ttm = " + ttm +"  heating_temp " + heating_temp);
						if(temp_hand != null) temp_hand.removeCallbacks(run_t);
						if(ttm < 100)
							temp.setText("0"+Integer.toString(ttm));
						else
							temp.setText(Integer.toString(ttm));

						temp.setTextColor(getResources().getColor(R.color.RED));
						ccc.setTextColor(getResources().getColor(R.color.RED));
						cir_dot.setColorFilter(getResources().getColor(R.color.RED));

						temp_hand.postDelayed(run_t, 2*1000);
				//	}

					break;
				case R.id.white:
					forcolor.setBackgroundColor(getResources().getColor(R.color.WHITE));
					break;
				case R.id.darkblue:
					forcolor.setBackgroundColor(getResources().getColor(R.color.DARKBLUE));
					break;
				case R.id.navy:
					forcolor.setBackgroundColor(getResources().getColor(R.color.NAVY));
					break;
			}
		}
	};

	private Runnable r = new Runnable() {
		public void run() {
			if(state_auto_pump == 1) {
				state_auto_pump = 0;
				auto_pump.setImageResource(R.drawable.auto_pump_off);
				water_ops(K10_AUTO_WATER, state_auto_pump);
			}
		}
	};
	private Runnable run_t = new Runnable() {
		public void run() {
			getTemp();
		}
	};
	private Runnable run_heat = new Runnable() {
		public void run() {
			heating_temp = intArray[state_heat];
			heating = true;
			water_ops(K10_HEATING, state_heat);
		}
	};

	private void getTemp()
	{
		tempp = get_temp(1);
		Log.v(TAG, "tempp " + tempp);
		if(isfinished == true) return;

		if(tempp > 0){
			if(tempp < 10)
				temp.setText("00"+Integer.toString(tempp));
			else if(tempp < 100)
				temp.setText("0"+Integer.toString(tempp));
			else
				temp.setText(Integer.toString(tempp));

			temp.setTextColor(getResources().getColor(R.color.RED));
			ccc.setTextColor(getResources().getColor(R.color.RED));
			cir_dot.setColorFilter(getResources().getColor(R.color.RED));
		}
		if(tempp > (intArray[state_heat]-1)){//99){
			if(heating == true) {
				state_heat = 0;
				heating = false;
				heat.setImageResource(R.drawable.heat_off);
				water_ops(K10_HEATING, state_heat);
			}
		}

		if(false){//(tempp < 0){
			//Toast.makeText(getApplicationContext(), "水壶异常", Toast.LENGTH_SHORT).show();
			if(!(boil_err.isShowing())){
				boil_err.show();
				boil_err.getWindow().setContentView(R.layout.boil_err);
			}
			/*new AlertDialog.Builder(this)//should use activity, not context
					.setTitle("水壶异常")
					.setMessage("voltage 1500")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int which) {
							noAlert = true;}}).show();*/
		}

		temp_hand.postDelayed(run_t, 1*1000);
	}
}


	/*	switch (tempp) {
				case 25:
					temp.setImageResource(R.drawable.t__25);
					break;
				case 40:
					temp.setImageResource(R.drawable.t__40);
					break;
				case 45:
					temp.setImageResource(R.drawable.t__45);
					break;
				case 50:
					temp.setImageResource(R.drawable.t__50);
					break;
				case 55:
					temp.setImageResource(R.drawable.t__55);
					break;
				case 60:
					temp.setImageResource(R.drawable.t__60);
					break;
				case 65:
					temp.setImageResource(R.drawable.t__65);
					break;
				case 70:
					temp.setImageResource(R.drawable.t__70);
					break;
				case 75:
					temp.setImageResource(R.drawable.t__75);
					break;
				case 80:
					temp.setImageResource(R.drawable.t__80);
					break;
				case 85:
					temp.setImageResource(R.drawable.t__85);
					break;
				case 90:
					temp.setImageResource(R.drawable.t__90);
					break;
				case 95:
					temp.setImageResource(R.drawable.t__95);
					break;
				case 100:
					temp.setImageResource(R.drawable.t__100);
					break;
				default:
					break;
			}*/
