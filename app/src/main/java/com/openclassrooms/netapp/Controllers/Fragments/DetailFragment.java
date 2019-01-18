package com.openclassrooms.netapp.Controllers.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.netapp.Models.GithubUserInfo;
import com.openclassrooms.netapp.R;
import com.openclassrooms.netapp.Utils.GithubStreams;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class DetailFragment extends Fragment {

    private static final String TAG = "DetailFragment";

    // FOR DESIGN
    @BindView(R.id.fragment_detail_follower) TextView mFollower;
    @BindView(R.id.fragment_detail_following) TextView mFollowing;
    @BindView(R.id.fragment_detail_public_repos) TextView mPublicRepos;
    @BindView(R.id.fragment_detail_company) TextView mCompany;
    @BindView(R.id.fragment_detail_blog) TextView mBlog;
    @BindView(R.id.fragment_detail_location) TextView mLocation;
    @BindView(R.id.textView_detail_name) TextView textView_detail_name;
    @BindView(R.id.imageView_detail_photo) ImageView imageView_detail_photo;

    //FOR DATA
    private Disposable disposable;

    public DetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the layout of MainFragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        ButterKnife.bind(this, view);

        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            String login = bundle.getString("login");
            this.executeHttpRequestWithRetrofit(login);
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy(); // Gestion des memory leak
    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(String login){
        this.disposable = GithubStreams.streamFetchUserInfos(login).subscribeWith(new DisposableObserver<GithubUserInfo>() {
            @Override
            public void onNext(GithubUserInfo githubUserInfo) {
                //Log.i(TAG, "welcome user : "+ githubUserInfo.getLogin());

                try{
                    Log.i(TAG, "nb follow user : "+ githubUserInfo.getFollowers());
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }

                updateUi(githubUserInfo);
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

    private void updateUi(GithubUserInfo githubUserInfo){
        this.textView_detail_name.setText(githubUserInfo.getLogin());
        this.mFollower.setText(githubUserInfo.getFollowers().toString());
        this.mFollowing.setText(githubUserInfo.getFollowing().toString());
        this.mPublicRepos.setText(githubUserInfo.getPublicRepos().toString());

        if(githubUserInfo.getBlog() != null){
            this.mBlog.setText(githubUserInfo.getBlog().toString());
        }else{
            this.mBlog.setText("inconnu");
        }

        if(githubUserInfo.getCompany() != null){
            this.mCompany.setText(githubUserInfo.getCompany().toString());
        }else{
            this.mCompany.setText("inconnu");
        }

        if(githubUserInfo.getLocation() != null){
            this.mLocation.setText(githubUserInfo.getLocation().toString());
        }else{
            this.mLocation.setText("inconnu");
        }

        Glide.with(this).load(githubUserInfo.getAvatarUrl()).apply(RequestOptions.circleCropTransform()).into(imageView_detail_photo);
    }

}

