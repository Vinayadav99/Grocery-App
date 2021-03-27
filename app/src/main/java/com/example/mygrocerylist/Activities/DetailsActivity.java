package com.example.mygrocerylist.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mygrocerylist.Data.DatabaseHandler;
import com.example.mygrocerylist.Model.Grocery;
import com.example.mygrocerylist.R;

public class DetailsActivity extends AppCompatActivity {

    private TextView itemName;
    private TextView quantity;
    private TextView dateAdded;
    private Button editButton;
    private Button deleteButton;
    private Button shareButton;
    private int id;

    // Alert Dialog
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    private DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        db = new DatabaseHandler(this);


        itemName = findViewById(R.id.groceryItemDet);
        quantity = findViewById(R.id.quantityDet);
        dateAdded = findViewById(R.id.dateAddedDet);
        editButton = findViewById(R.id.editButtonDet);
        deleteButton = findViewById(R.id.deleteButtonDet);
        shareButton = findViewById(R.id.shareButtonDet);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            itemName.setText(extras.getString("Name"));
            quantity.setText(extras.getString("Quantity"));
            dateAdded.setText(extras.getString("DateAdded"));
            id = extras.getInt("ID");
        }

        getSupportActionBar().setTitle(itemName.getText().toString());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteGrocery(id);
                startActivity(new Intent(DetailsActivity.this,ListActivity.class));
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Grocery grocery = new Grocery();
                grocery.setId(id);
                grocery.setName(itemName.getText().toString());
                grocery.setQuantity(quantity.getText().toString());
                grocery.setDateItemAdded(dateAdded.getText().toString());
                createPopUp(grocery);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Item: "+itemName.getText().toString()+"\n");
                stringBuilder.append(quantity.getText().toString()+"\n");
                stringBuilder.append(dateAdded.getText().toString()+"\n");

                String result = stringBuilder.toString();

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,result);
                intent.setType("text/plain");

                //Intent shareIntent = Intent.createChooser(intent,null);
                startActivity(intent);
            }
        });

    }

    public void createPopUp(final Grocery grocery){
        alertDialogBuilder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.popup,null);

        TextView title = view.findViewById(R.id.title);
        title.setText("Edit Grocery");
        final EditText groceryItem = view.findViewById(R.id.groceryItem);
        final EditText quantityPopUp = view.findViewById(R.id.groceryQty);
        Button saveButton = view.findViewById(R.id.saveButton);

        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grocery.setName(groceryItem.getText().toString());
                grocery.setQuantity(quantityPopUp.getText().toString());

                if(!groceryItem.getText().toString().isEmpty() && !quantityPopUp.getText().toString().isEmpty()){
                    db.updateGrocery(grocery);
                    itemName.setText(grocery.getName());
                    quantity.setText(grocery.getQuantity());
                    dateAdded.setText(grocery.getDateItemAdded());
                }
                alertDialog.dismiss();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(DetailsActivity.this,ListActivity.class));
                    }
                },1500);
            }
        });
    }

}