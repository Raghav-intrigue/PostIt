package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackboxindia.PostIT.Network.Interfaces.onDeleteListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.adapters.ViewAdImageAdapter;
import com.blackboxindia.PostIT.dataModels.AdData;
import com.blackboxindia.PostIT.dataModels.UserInfo;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Frag_ViewMyEvent extends Fragment {

    //region Variables
    private static String TAG = Frag_ViewMyEvent.class.getSimpleName() +" YOYO";
    RecyclerView imgRecyclerView;
    TextView tv_Title, tv_Description;
    TextView tv_Date, tv_Time;
    View view;
    Context context;

    AdData event;
    Bitmap main;

    //endregion

    //region Initial Setup

    @Override
    public void onResume() {
        MenuItem item = ((MainActivity) getActivity()).toolbar.getMenu().findItem(R.id.toolbar_delete);
        item.setVisible(true);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.toolbar_delete){

                    final ProgressDialog dialog = ProgressDialog.show(context, "Deleting...", "", true, false);
                    NetworkMethods methods = new NetworkMethods(context);
                    methods.deleteAd(((MainActivity) getActivity()).userInfo, event, new onDeleteListener() {
                        @Override
                        public void onSuccess(UserInfo userInfo) {
                            dialog.cancel();
                            ((MainActivity)context).onBackPressed();
                            ((MainActivity)context).UpdateUI(userInfo,false,false);
                            ((MainActivity)context).createSnackbar("Ad Deleted Successfully", Snackbar.LENGTH_LONG);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            dialog.cancel();
                            ((MainActivity)context).createSnackbar(e.getMessage(),Snackbar.LENGTH_LONG);
                        }
                    });
                }
                return true;
            }
        });
        super.onResume();
    }

    public static Frag_ViewMyEvent newInstance(AdData event) {

        Frag_ViewMyEvent fragment = new Frag_ViewMyEvent();
        fragment.event = event;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_viewevent, container, false);
        context = view.getContext();

        initVariables();

        PopulateViews();
        return view;
    }

    private void initVariables() {

        tv_Title = (TextView) view.findViewById(R.id.Ad_tvTitle);
        tv_Description = (TextView) view.findViewById(R.id.Ad_tvDescription);
        imgRecyclerView = (RecyclerView) view.findViewById(R.id.Ad_imgRecycler);

        tv_Date = (TextView) view.findViewById(R.id.Ad_etDate);
        tv_Time = (TextView) view.findViewById(R.id.Ad_etTime);

    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).toolbar.getMenu().findItem(R.id.toolbar_delete).setVisible(false);
        ((MainActivity) getActivity()).closeImageListener = null;
    }
    //endregion

    void PopulateViews() {

        if(event!=null) {

            tv_Title.setText(event.getTitle());
            tv_Description.setText(event.getDescription());

            String myFormat = "dd/MM/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            tv_Date.setText(sdf.format(event.getDateTime().toCalender().getTime()));
            String timeFormat = "hh:mm a";
            SimpleDateFormat tf = new SimpleDateFormat(timeFormat, Locale.US);
            tv_Time.setText(tf.format(event.getDateTime().toCalender().getTime()));

            setUpImgRecycler();
        }
        else
            Log.i("Frag_ViewAd YOYO","no adDATA");
    }

    void setUpImgRecycler() {
        //Todo: Correct this
//        main = ((Frag_Ads)(getFragmentManager().findFragmentByTag(MainActivity.ALL_FRAG_TAG))).current;

        if(event.getNumberOfImages()>0) {
            ViewAdImageAdapter adapter = new ViewAdImageAdapter(context, event, main, view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            imgRecyclerView.setLayoutManager(linearLayoutManager);
            imgRecyclerView.setAdapter(adapter);
        }
        else
            imgRecyclerView.setVisibility(View.GONE);
    }

}
