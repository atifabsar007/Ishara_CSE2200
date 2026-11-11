package com.kuet.ishara.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import com.google.android.material.button.MaterialButton;
import com.kuet.ishara.data.ScenarioDb;
import com.kuet.ishara.databinding.ActivitySimulatorBinding;
import com.kuet.ishara.logic.ScenarioEngine;
import java.util.ArrayList;
import java.util.List;

/** Branching role-play with actual Java state transitions, text-to-speech and SQLite scores. */
public class SimulatorActivity extends BaseSpeechActivity {
    private ActivitySimulatorBinding b;
    private final List<ScenarioEngine.Scenario> choices=ScenarioEngine.all();
    private ScenarioEngine.Scenario scenario;
    private ScenarioEngine.Node current;
    private final List<String> path=new ArrayList<>();
    private int points;
    private String last="";
    @Override protected void onCreate(Bundle state){super.onCreate(state);
        b=ActivitySimulatorBinding.inflate(getLayoutInflater());setContentView(b.getRoot());
        b.btnBack.setOnClickListener(v->finish());
        List<String> names=new ArrayList<>();for(ScenarioEngine.Scenario s:choices)names.add(s.title);
        b.scenarioSpinner.setAdapter(new IsharaSpinnerAdapter(this,names));
        b.btnBegin.setOnClickListener(v->begin());
        b.btnRestart.setOnClickListener(v->begin());
        b.btnSpeak.setOnClickListener(v->speakBangla(last));
        b.btnShare.setOnClickListener(v->shareMessage(last));
        b.btnSpeak.setEnabled(false);b.btnShare.setEnabled(false);
        renderTotals();
    }
    private void begin(){scenario=choices.get(b.scenarioSpinner.getSelectedItemPosition());
        points=0;path.clear();current=scenario.node(scenario.start);last="";
        b.txtScenarioName.setText(scenario.title);b.txtScenarioDescription.setText(scenario.description);
        b.txtFeedback.setText("Choose the response you would like to use. Different choices lead to different paths.");
        showNode();
    }
    private void showNode(){b.choiceContainer.removeAllViews();
        b.txtScenarioScore.setText("This run: "+points+" points");
        if(current==null){b.txtPrompt.setText("Scenario complete!");b.txtContext.setText("You practised expressing your needs clearly.");
            b.txtFeedback.setText("Saved your route and score in SQLite. Choose Restart to try another route.");
            try(ScenarioDb db=new ScenarioDb(this)){db.add(scenario.id,android.text.TextUtils.join(" | ",path),points);}
            renderTotals();return;
        }
        b.txtPrompt.setText(current.prompt);b.txtContext.setText(current.context);
        for(ScenarioEngine.Choice ch:current.choices){MaterialButton button=new MaterialButton(this);
            button.setText(ch.label);button.setAllCaps(false);button.setTextSize(16);button.setCornerRadius(dp(16));
            button.setTextColor(getColor(com.kuet.ishara.R.color.ink));
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(com.kuet.ishara.R.color.primarySoft)));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(10);button.setMinHeight(dp(58));
            b.choiceContainer.addView(button,lp);button.setOnClickListener(v->select(ch));
        }
    }
    private void select(ScenarioEngine.Choice ch){points+=ch.points;last=ch.label;path.add(ch.label);
        recordMessage(ch.label,"scenario");b.btnSpeak.setEnabled(true);b.btnShare.setEnabled(true);
        b.txtFeedback.setText(ch.response);current=scenario.node(ch.next);showNode();
        b.txtFeedback.setText(ch.response+(current==null?"  Scenario complete.":"  Continue below."));
    }
    private void renderTotals(){try(ScenarioDb db=new ScenarioDb(this)){b.txtLifetimeScore.setText(db.summary());}}
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
}
