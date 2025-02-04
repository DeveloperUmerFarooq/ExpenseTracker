package com.example.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;


public class activity_expense_list extends AppCompatActivity {
    private static final String SHARED_PREFS = "ExpensePrefs";
    private static final String KEY_EXPENSES = "Expenses";
    private static final String KEY_TOTAL = "Total";
    private ArrayList<String> expenses;
    private ArrayAdapter<String> adapter;
    private ListView expenseListView;
    private TextView totalExpense;
    private int total=0;

    FirebaseAuth auth;
    ImageButton logout;

    ImageView profile;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        auth=FirebaseAuth.getInstance();
        logout=findViewById(R.id.logoutButton);
        user=auth.getCurrentUser();

        if(user==null){
            Intent intent = new Intent(activity_expense_list.this, Login.class);
            startActivity(intent);
            finish();
        }


        logout.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(activity_expense_list.this, Login.class);
            startActivity(intent);
            finish();
        });


        profile=findViewById(R.id.profile);
        profile.setOnClickListener(v->{
            Intent intent = new Intent(activity_expense_list.this,Profile.class);
            intent.putExtra("total", total);
            startActivity(intent);
        });


        expenseListView = findViewById(R.id.expenseListView);
        ImageButton addExpenseButton = findViewById(R.id.addExpenseButton);
        ImageButton deleteExpenseButton = findViewById(R.id.deleteExpenseButton);
        totalExpense=findViewById(R.id.total);

        expenses = loadExpenses();
        total = loadTotal();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, expenses);
        
        String totalAmount =String.valueOf(total);
        totalExpense.setText(totalAmount);
        expenseListView.setAdapter(adapter);
        expenseListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        ImageButton exportExpenseButton = findViewById(R.id.exportExpenseButton);
        exportExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_expense_list.this, ActivityExportPreview.class);
            intent.putStringArrayListExtra("expenses", expenses);
            intent.putExtra("total", total);
            startActivity(intent);
        });


        // Add Expense Button Listener
        addExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity_expense_list.this, ActivityAddExpense.class);
            startActivityForResult(intent, 1);
        });

        // Delete Expense Button Listener
        deleteExpenseButton.setOnClickListener(v -> {
            int position = expenseListView.getCheckedItemPosition();
            if (position != ListView.INVALID_POSITION) {
                String expenseItem = expenses.get(position);
                String[] expenseParts = expenseItem.split(" - ");

                if (expenseParts.length >= 2) {
                    String amountString = expenseParts[1].split("\n")[0].trim();
                    int expenseAmount = Integer.parseInt(amountString);
                    total -= expenseAmount;
                    saveTotal(total);
                    totalExpense.setText(String.valueOf(total));

                    expenses.remove(position);
                    saveExpenses(expenses);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Expense Deleted Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Invalid expense format!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select an expense to delete.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private ArrayList<String> loadExpenses() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String savedExpenses = sharedPreferences.getString(KEY_EXPENSES, "");
        if (!savedExpenses.isEmpty()) {
            String[] expenseArray = savedExpenses.split(",");
            return new ArrayList<>(Arrays.asList(expenseArray));
        }
        return new ArrayList<>();
    }

    private void saveExpenses(ArrayList<String> expenses) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder sb = new StringBuilder();
        for (String expense : expenses) {
            sb.append(expense).append(",");
        }
        editor.putString(KEY_EXPENSES, sb.toString());
        editor.apply();
    }

    private void saveTotal(int total) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_TOTAL, total);
        editor.apply();
    }

    private int loadTotal() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_TOTAL, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String expense = data.getStringExtra("expense");
            expenses.add(expense);
            saveExpenses(expenses);
            adapter.notifyDataSetChanged();
            String amount = data.getStringExtra("expense-amount");
            int expenseAmount = Integer.parseInt(amount);
            total+=expenseAmount;
            saveTotal(total);
            totalExpense.setText(String.valueOf(total));
            Toast.makeText(this,"New Item Added in List.",Toast.LENGTH_SHORT).show();
        }
    }
}