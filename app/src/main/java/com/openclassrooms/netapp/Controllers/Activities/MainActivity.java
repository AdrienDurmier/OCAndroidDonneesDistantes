package com.openclassrooms.netapp.Controllers.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.openclassrooms.netapp.Controllers.Fragments.MainFragment;
import com.openclassrooms.netapp.Models.GithubUser;
import com.openclassrooms.netapp.R;

public class MainActivity extends AppCompatActivity implements MainFragment.MyItemClickListener {

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.configureAndShowMainFragment();
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    private void configureAndShowMainFragment(){

        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_frame_layout);

        if (mainFragment == null) {
            mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main_frame_layout, mainFragment)
                    .commit();
        }
    }

    /**
     * Lors du clic sur un utilisateur de Github
     * @param user = utilisateur Github
     */
    @Override
    public void onItemClicked(GithubUser user) {
        // Envoie vers la fiche de l'utilisateur de Github
        startActivity(new Intent(this, DetailActivity.class).putExtra("login", user.getLogin()));
    }
}
