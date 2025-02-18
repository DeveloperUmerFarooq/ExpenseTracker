package com.example.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
    private int total = 0;

    FirebaseAuth auth;
    ImageButton logout, deleteExpenseButton;
    ImageView profile;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        auth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logoutButton);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(activity_expense_list.this, Login.class);
            startActivity(intent);
            finish();
        }

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(activity_expense_list.this, Login.class);
            startActivity(intent);
            finish();
        });

        profile = findViewById(R.id.profile);
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(activity_expense_list.this, Profile.class);
            intent.putExtra("total", total);
            startActivity(intent);
        });

        expenseListView = findViewById(R.id.expenseListView);
        ImageButton addExpenseButton = findViewById(R.id.addExpenseButton);
        deleteExpenseButton = findViewById(R.id.deleteExpenseButton);
        totalExpense = findViewById(R.id.total);

        expenses = loadExpenses();
        total = loadTotal();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, expenses);

        totalExpense.setText(String.valueOf(total));
        expenseListView.setAdapter(adapter);
        expenseListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // Enable multiple selection

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

        // Multi-Delete Button Listener
        deleteExpenseButton.setOnClickListener(v -> {
            multiDeleteExpenses();
        });
    }

    // Multi-Delete Function
    private void multiDeleteExpenses() {
        int count = expenseListView.getCount();
        ArrayList<String> selectedExpenses = new ArrayList<>();
        int removedTotal = 0;

        for (int i = count - 1; i >= 0; i--) {
            if (expenseListView.isItemChecked(i)) {
                String expenseItem = expenses.get(i);
                String[] expenseParts = expenseItem.split(" - ");
                if (expenseParts.length >= 2) {
                    String amountString = expenseParts[1].split("\n")[0].trim();
                    int expenseAmount = Integer.parseInt(amountString);
                    removedTotal += expenseAmount;
                }
                selectedExpenses.add(expenseItem);
            }
        }

        if (selectedExpenses.isEmpty()) {
            Toast.makeText(this, "Please select expenses to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        expenses.removeAll(selectedExpenses);
        total -= removedTotal;
        saveExpenses(expenses);
        saveTotal(total);

        adapter.notifyDataSetChanged();
        totalExpense.setText(String.valueOf(total));
        Toast.makeText(this, "Selected Expenses Deleted Successfully!", Toast.LENGTH_SHORT).show();
    }

    private ArrayList<String> loadExpenses() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String savedExpenses = sharedPreferences.getString(KEY_EXPENSES, "");
        if (!savedExpenses.isEmpty()) {
            return new ArrayList<>(Arrays.asList(savedExpenses.split(",")));
        }
        return new ArrayList<>();
    }

    private void saveExpenses(ArrayList<String> expenses) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EXPENSES, String.join(",", expenses));
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
            int expenseAmount = Integer.parseInt(data.getStringExtra("expense-amount"));
            total += expenseAmount;
            saveTotal(total);
            totalExpense.setText(String.valueOf(total));
            Toast.makeText(this, "New Item Added in List.", Toast.LENGTH_SHORT).show();
        }
    }
}
