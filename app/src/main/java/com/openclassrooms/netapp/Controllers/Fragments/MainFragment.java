package com.openclassrooms.netapp.Controllers.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.openclassrooms.netapp.Models.GithubUser;
import com.openclassrooms.netapp.R;
import com.openclassrooms.netapp.Utils.GithubStreams;
import com.openclassrooms.netapp.Utils.ItemClickSupport;
import com.openclassrooms.netapp.Views.GithubUserAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements GithubUserAdapter.Listener, ItemClickSupport.OnItemClickListener {

    // FOR DESIGN
    @BindView(R.id.fragment_main_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fragment_main_swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    //FOR DATA
    private Disposable disposable;
    private List<GithubUser> githubUsers;
    private GithubUserAdapter adapter;

    // Declare callback
    private MyItemClickListener mCallback;

    // Declare our interface that will be implemented by any container activity
    public interface MyItemClickListener {
        public void onItemClicked(GithubUser user);
    }

    public MainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflate the layout of MainFragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, view);
        this.configureRecyclerView();
        this.configureSwipeRefreshLayout();
        this.configureOnClickRecyclerView();
        this.executeHttpRequestWithRetrofit();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Call the method that creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    // -----------------
    // ACTION
    // -----------------

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        GithubUser user = adapter.getUser(position);

        // Spread the click to the parent activity
        mCallback.onItemClicked(user);
    }

    private void configureOnClickRecyclerView(){
        // Set onClickListener to one item (of recycler view)
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_main_item).setOnItemClickListener(this);
    }

    @Override
    public void onClickDeleteButton(int position) {
        GithubUser user = adapter.getUser(position);
        Toast.makeText(getContext(), "You are trying to delete user : "+user.getLogin(), Toast.LENGTH_SHORT).show();
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    private void configureRecyclerView(){
        this.githubUsers = new ArrayList<>();
        // Create adapter passing in the sample user data
        this.adapter = new GithubUserAdapter(this.githubUsers, Glide.with(this), this);
        // Attach the adapter to the recyclerview to populate items
        this.recyclerView.setAdapter(this.adapter);
        // Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequestWithRetrofit();
            }
        });
    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(){
        this.disposable = GithubStreams.streamFetchUserFollowing("JakeWharton").subscribeWith(new DisposableObserver<List<GithubUser>>() {
            @Override
            public void onNext(List<GithubUser> users) {
                updateUI(users);
            }

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onComplete() { }
        });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    // -------------------
    // UPDATE UI
    // -------------------

    private void updateUI(List<GithubUser> users){
        githubUsers.clear();
        githubUsers.addAll(users);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    // --------------
    // FRAGMENT SUPPORT
    // --------------

    // 3 - Create callback to parent activity
    private void createCallbackToParentActivity(){
        try {
            //Parent activity will automatically subscribe to callback
            mCallback = (MyItemClickListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString()+ " must implement OnButtonClickedListener");
        }
    }

}