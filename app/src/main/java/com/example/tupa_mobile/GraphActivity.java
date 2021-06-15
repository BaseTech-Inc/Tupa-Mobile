package com.example.tupa_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;

import com.example.tupa_mobile.Graph.CustomGraphMarker;
import com.example.tupa_mobile.Graph.ForecastGraph;
import com.example.tupa_mobile.Graph.GraphDayItem;
import com.example.tupa_mobile.Graph.GraphDayItemAdapter;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LineChart forecastChart;
    private HorizontalScrollView horizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        toolbar = findViewById(R.id.mapToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_right_icon_white_black_theme_small);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        horizontalScrollView = findViewById(R.id.graphHorizontalScroll);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        recyclerView = findViewById(R.id.graphRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        createItems(getApplicationContext(), recyclerView);

        forecastChart = findViewById(R.id.forecastChart);

        ForecastGraph forecastGraph = new ForecastGraph();
        forecastGraph.createGraph(forecastChart, getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upper_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("result", 3);
                setResult(RESULT_OK, intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void createItems(Context context, RecyclerView recyclerView){
        GraphDayItemAdapter adapter = new GraphDayItemAdapter(context, generateItemsList());
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<GraphDayItem> generateItemsList() {
        ArrayList<GraphDayItem> list = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            list.add(new GraphDayItem("Segunda", "20/06", "20%", "10%"));
        }
        Log.d("List", list.get(2).getDate());
        return list;
    }
}