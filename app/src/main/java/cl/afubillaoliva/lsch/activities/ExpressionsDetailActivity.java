package cl.afubillaoliva.lsch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cl.afubillaoliva.lsch.Interfaces.RecyclerViewOnClickListenerHack;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.adapters.ExpressionsListAdapter;
import cl.afubillaoliva.lsch.models.Expressions;
import cl.afubillaoliva.lsch.utils.SharedPreference;


public class ExpressionsDetailActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreference mSharedPreferences = new SharedPreference(this);
        if(mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.expression_detail_activity);

        Toolbar mToolbar = findViewById(R.id.toolbar);

        Intent intent = getIntent();
        Expressions expression = (Expressions) intent.getSerializableExtra("position");

        mToolbar.setTitle(expression.getTitle());
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onClickListener(View view, int position) {

    }

    @Override
    public void onLongPressClickListener(View view, int position) {

    }
}
