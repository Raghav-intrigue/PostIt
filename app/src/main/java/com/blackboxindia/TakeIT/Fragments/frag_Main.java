package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.Interfaces.getAllAdsListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.adapters.adViewTransition;
import com.blackboxindia.TakeIT.adapters.mainAdapter;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class frag_Main extends Fragment {

    private static String TAG = frag_Main.class.getSimpleName() + " YOYO";

    //region variables
    View view;
    Context context;
    NetworkMethods networkMethods;
    UserInfo userInfo;
    FirebaseAuth mAuth;
    SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<AdData> allAds;
    //endregion


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_main, container, false);
        context = view.getContext();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        refresh();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }

    void refresh() {

        userInfo = ((MainActivity)context).userInfo;
        mAuth = ((MainActivity)context).mAuth;

        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        if(mAuth!=null){
            networkMethods = new NetworkMethods(context, mAuth);
            getAllAds();
        }
    }

    private void getAllAds() {
        final ProgressDialog dialog = ProgressDialog.show(context, "Just a sec", "Getting the good stuff", true, false);

        networkMethods.getAllAds(userInfo, 30 ,new getAllAdsListener() {
            @Override
            public void onSuccess(ArrayList<AdData> list) {
                allAds = list;
                setUpRecyclerView();
                dialog.cancel();
            }

            @Override
            public void onFailure(Exception e) {
                dialog.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRecyclerView() {

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.ads_recycler);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        mainAdapter adapter = new mainAdapter(context,userInfo, mAuth, new mainAdapter.ImageClickListener() {

            @Override
            public void onClick(mainAdapter.adItemViewHolder holder, int position, AdData currentAd) {

                frag_ViewAd fragViewAd = new frag_ViewAd();

                fragViewAd.setSharedElementEnterTransition(new adViewTransition());
                fragViewAd.setEnterTransition(new Fade());
                setExitTransition(new Fade());
                fragViewAd.setSharedElementReturnTransition(new adViewTransition());

                Bundle args = new Bundle();

                args.putParcelable("adData",allAds.get(position));

                fragViewAd.setArguments(args);

                getActivity().getFragmentManager()
                        .beginTransaction()
                        .addSharedElement(holder.getMajorImage(), "adImage0")
                        .replace(R.id.frame_layout, fragViewAd, MainActivity.VIEW_AD_TAG)
                        .addToBackStack(null)
                        .commit();

            }
        });
        recyclerView.setAdapter(adapter);
    }

}
