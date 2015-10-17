package com.possibillion.example;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Activity mActivity;
    SharedPreferences shared;
    SharedPreferences.Editor edit;
    MaterialDialog mDailog;
    ArrayList<Contacts> contacts;
    CustomAdapter mAdapter;
    ListView mListView;
    private int pagination = 40;
    private boolean loaded = false;
    private boolean isRunning= false;
    String error ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity =this;
        shared = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
        edit = shared.edit();
        edit.putInt(PreferenceConstants.START_VALUE, 0).commit();

        contacts = new ArrayList<Contacts>();
        mAdapter = new CustomAdapter(this, contacts);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                //Algorithm to check if the last item is visible or not
                final int lastItem = firstVisibleItem + visibleItemCount;
                Log.d("Scroll", firstVisibleItem + " - " + visibleItemCount + " - " + totalItemCount);
                if(isRunning){
                    return;
                }
                if(loaded){
                    pagination = 15;
                }
                if (lastItem == totalItemCount) {

                    ListRequest task = new ListRequest(shared.getInt(PreferenceConstants.START_VALUE, 0), pagination);
                    new LoadList().execute(task);
                }
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void showSnackBar(String message, int layout, int backgroud, int textColor){
        Snackbar snack = Snackbar
                .make(mActivity.findViewById(layout), message, Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snack.getView();
        group.setBackgroundColor(mActivity.getResources().getColor(backgroud));
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (view instanceof TextView) {
                TextView t = (TextView) view;
                t.setTextColor(mActivity.getResources().getColor(textColor));
            }
        }
        snack.show();
    }


    class LoadList extends AsyncTask<ListRequest, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDailog = new MaterialDialog.Builder(mActivity)
                    .content("Fetching list data...")
                    .progress(true, 0)
                    .show();
            isRunning =true;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(ListRequest... params) {
            int start = params[0].getStart_value();
            int pagination = params[0].getPagination();
            OkHttpClient client = new OkHttpClient();
            JSONParser jsonParser = new JSONParser(MainActivity.this,client);
            int code = 0;
            String data = "";
            int count = 0;

            try {
                //TODO Get Dynamic phone num and save in SP
                Log.d("url",PreferenceConstants.GET_LIST_PATH + "?start=" + start + "&pag=" + pagination);
                String res = jsonParser.doGetRequest(PreferenceConstants.GET_LIST_PATH + "?start=" + start + "&pag=" + pagination);
                JSONObject jsonObject = new JSONObject(res);
                 code = jsonObject.getInt("code");
                if(code==400){
                    error = jsonObject.getString("error");
                    return false;
                }
                 data = jsonObject.getString("data");
                 count = jsonObject.getInt("count");

                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj1 = jsonArray.getJSONObject(i);
                    Log.d(i + "", obj1.toString());
                    int id =obj1.getInt("id");
                    String name = obj1.getString("name");
                    String phone = obj1.getString("phone");
                    contacts.add( new Contacts(name, phone) );
                }
                edit.putInt(PreferenceConstants.START_VALUE, start+count).commit();
                return true;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean res) {
            super.onPostExecute(res);
            if(res){
                //TODO update list

                mAdapter.notifyDataSetChanged();
            }else {
                new MaterialDialog.Builder(mActivity)
                        .title("Error..!")
                        .content(error)
                        .positiveText("OK")
                        .show();
                error ="";
            }
            loaded =true;
            isRunning = false;
            mDailog.cancel();
        }
    }
}
