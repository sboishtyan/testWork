package com.example.sboishtyan.forsportsru.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sboishtyan.forsportsru.Injector;
import com.example.sboishtyan.forsportsru.R;
import com.example.sboishtyan.forsportsru.SportsRuAppComponent;
import com.example.sboishtyan.forsportsru.data.preferences.StringPreference;
import com.example.sboishtyan.forsportsru.data.qualifiers.TeamId;
import com.example.sboishtyan.forsportsru.data.qualifiers.TeamTitle;
import com.example.sboishtyan.forsportsru.databinding.TeamListItemBinding;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    String[]             teamIds;
    String[]             teamTitles;
    SportsRuAppComponent appGraph;

    @Bind(R.id.list) RecyclerView teamsList;
    RecyclerView.Adapter adapter;

    @Inject @TeamId    StringPreference teamPreferenceId;
    @Inject @TeamTitle StringPreference teamTitlePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appGraph = (SportsRuAppComponent) Injector.obtain(getApplication());
        appGraph.inject(this);

        if (teamPreferenceId.isSet() && teamTitlePreference.isSet()) {
            startNewsActivity();
            return;
        }

        setContentView(R.layout.list);
        teamIds = getResources().getStringArray(R.array.team_ids);
        teamTitles = getResources().getStringArray(R.array.team_titles);

        ButterKnife.bind(this);
        adapter = new TeamsAdapter(R.layout.team_list_item);
        teamsList.setLayoutManager(new LinearLayoutManager(this));
        teamsList.setAdapter(adapter);
        teamsList.setHasFixedSize(true);

    }

    private void startNewsActivity() {
        startActivity(new Intent(this, NewsActivity.class));
    }

    class TeamsAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final int viewId;

        TeamsAdapter(@IdRes int viewId) {
            this.viewId = viewId;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(viewId, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TeamListItemBinding.bind(holder.teamTitleView).setTeamTitle(teamTitles[position]);
        }
        @Override
        public int getItemCount() {
            return teamTitles.length;
        }

    }
    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView teamTitleView;

        public ViewHolder(TextView view) {
            super(view);
            teamTitleView = view;
            teamTitleView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            teamPreferenceId.set(teamIds[adapterPosition]);
            teamTitlePreference.set(teamTitles[adapterPosition]);
            startNewsActivity();
        }

    }
}
