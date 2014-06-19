package com.google.wi_fi_list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.Fragment;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

public class MainActivity extends Activity {

	Timer timer = null;

	// 現在接続しているWi-Fi情報の取得を開始する
	public void WiFi_info_get_Start() {

		final Handler handler = new Handler();

		this.timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						// 接続開始
						get_WiFi_info_List();
					}
				});
			}
		}, 0, 10000000);
		// ※1000にすると細かく情報を取得してくれますが、
		// 　Wi-Fiの詳細が見えづらいので10000000に設定しています…(-_-;)
	}

	// 情報取得を停止する
	// （※非実装。メモ程度に書きます。お好みでボタン等を追加する時の参考程度に…）
	public void iFi_info_get_stop() {
		this.timer.cancel();
	}

	// 現在接続中のWi-Fi情報を取得する
	public void get_WiFi_info_List() {
		// Wi-Fiマネージャ取得
		WifiManager manager = (WifiManager)getSystemService(WIFI_SERVICE);

		// 現在周知しているWi-Fiスポット検索開始
		manager.startScan();

		List<ScanResult> scanResults = manager.getScanResults();
		int size = scanResults.size();

		String [] ssids = new String [size];
		String [][][] wifis = new String [size][4][2];

		for (int i = 0; i < size; i++) {
			// SSIDを取得して格納
			ssids[i] = scanResults.get(i).SSID;

			// BSSIDを取得して格納
			wifis[i][0][0] = scanResults.get(i).BSSID;
			wifis[i][0][1] = "BSSID";

			// 周波数を取得して格納
			wifis[i][1][0] = String.format("%d", scanResults.get(i).frequency);
			wifis[i][1][1] = "周波数";

			// 信号レベルを取得して格納
			wifis[i][2][0] = String.format("%d", scanResults.get(i).level);
			wifis[i][2][1] = "信号レベル";

			// 暗号化情報を取得して格納
			wifis[i][3][0] = scanResults.get(i).capabilities;
			wifis[i][3][1] = "暗号化情報";

			/*
			 * Android API Level 17以降では、情報取得日時（timestamp）が取得できます
			 * しかし、リアルタイムに周囲のWi-Fiスポットをスキャンして取得しているので、
			 * 情報取得日時が取得できなくともさして問題はないと考えます
			 * もし、Android4.2.X以降のOSを搭載したAndroid端末を持っていて、
			 * timestampも取得してみたいという方は、
			 * scanResults.get(i).timestamp
			 * を追加してみてもいいでしょう
			 * ただし、取得後、データを整形してやる必要があります
			 */

		}

		ExpandableListView elv = (ExpandableListView)findViewById(R.id.ex_List_View);
		ArrayList<Map<String, String>> ssid_list = new ArrayList<Map<String, String>>();
		ArrayList<List<Map<String, String>>> wifi_list = new ArrayList<List<Map<String, String>>>();

		for (int i = 0; i < size; i++) {
			HashMap<String, String> ssid = new HashMap<String, String>();
			ssid.put("ssid_name", ssids[i]);
			ssid_list.add(ssid);

			ArrayList<Map<String, String>> wifi2 = new ArrayList<Map<String, String>>();
			for (int j = 0; j < 4; j++) {
				HashMap<String, String> wifi = new HashMap<String, String>();
				wifi.put("wifi_info", wifis[i][j][0]);
				wifi.put("wifi_type", wifis[i][j][1]);
				wifi2.add(wifi);
			}
			wifi_list.add(wifi2);
		}

		SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter (
				this,
				ssid_list,
				android.R.layout.simple_expandable_list_item_1,
				new String [] {"ssid_name"},
				new int [] {android.R.id.text1},
				wifi_list,
				android.R.layout.simple_expandable_list_item_2,
				new String [] {"wifi_info", "wifi_type"},
				new int [] {android.R.id.text1, android.R.id.text2}
		);
		elv.setAdapter(adapter);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		WiFi_info_get_Start();

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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
