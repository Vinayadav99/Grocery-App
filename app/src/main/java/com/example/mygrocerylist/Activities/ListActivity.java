package com.example.mygrocerylist.Activities;

import android.os.Bundle;

import com.example.mygrocerylist.Data.DatabaseHandler;
import com.example.mygrocerylist.Model.Grocery;
import com.example.mygrocerylist.UI.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mygrocerylist.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private DatabaseHandler db;
    private List<Grocery> groceryList;
    private List<Grocery> listItems;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("List Of Items");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                createPopUp();
            }
        });

        db = new DatabaseHandler(this);
        recyclerView = findViewById(R.id.recyclerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groceryList = new ArrayList<>();
        listItems = new ArrayList<>();

        groceryList = db.getAllGrocery();

        for(Grocery g:groceryList){
            Grocery grocery = new Grocery();
            grocery.setId(g.getId());
            grocery.setName(g.getName());
            grocery.setQuantity("Qty: "+g.getQuantity());
            grocery.setDateItemAdded("Added on: "+g.getDateItemAdded());

            listItems.add(grocery);
        }
        recyclerViewAdapter = new RecyclerViewAdapter(this,listItems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }

    public void createPopUp(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.popup,null);

        final EditText groceryItem = view.findViewById(R.id.groceryItem);
        final EditText quantity = view.findViewById(R.id.groceryQty);
        Button saveButton = view.findViewById(R.id.saveButton);

        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Grocery g = new Grocery();
                g.setName(groceryItem.getText().toString());
                g.setQuantity(quantity.getText().toString());

                if(!groceryItem.getText().toString().isEmpty() && !quantity.getText().toString().isEmpty()){
                    db.addGrocery(g);
                    Grocery grocery = db.getByName(g.getName());

                    // grocery for the recycler view with some text edits
                    Grocery groc = new Grocery();
                    groc.setId(grocery.getId());
                    groc.setName(grocery.getName());
                    groc.setQuantity("Qty: "+grocery.getQuantity());
                    groc.setDateItemAdded("Added on: "+grocery.getDateItemAdded());
                    listItems.add(groc);
                    recyclerViewAdapter.notifyDataSetChanged();

                }

                Snackbar.make(view,"Item Added",Snackbar.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                    }
                },1000);
            }
        });
    }

}