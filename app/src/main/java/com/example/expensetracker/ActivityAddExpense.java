package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityAddExpense extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        EditText nameInput = findViewById(R.id.expenseNameInput);
        EditText amountInput = findViewById(R.id.expenseAmountInput);
        EditText descriptionInput=findViewById(R.id.expenseDescriptionInput);
        Button submitButton = findViewById(R.id.submitExpenseButton);

        submitButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String amount = amountInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(ActivityAddExpense.this, "Enter name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(description)) {
                Toast.makeText(ActivityAddExpense.this, "Enter description", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(amount)) {
                Toast.makeText(ActivityAddExpense.this, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }
            String expense = name + " - " + amount+"\n"+description;
            Intent intent = new Intent();
            String total= amountInput.getText().toString();

            intent.putExtra("expense", expense);
            intent.putExtra("expense-amount",total);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}