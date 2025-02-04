package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends AppCompatActivity {

    TextView expense,email,remainingAmountView;

    double salary;
    Button logout;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        int total = intent.getIntExtra("total", 0);
        expense=findViewById(R.id.totalExpenseView);
        expense.setText(String.valueOf(total));
        email=findViewById(R.id.emailView);
        logout=findViewById(R.id.logout);
        remainingAmountView=findViewById(R.id.remainingAmountView);
        EditText salaryInput=findViewById(R.id.salaryInput);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user !=null){
            String userEmail=user.getEmail();
            email.setText(userEmail);
        }

        logout.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            Intent ntent = new Intent(Profile.this, Login.class);
            startActivity(ntent);
            finish();
        });

        salaryInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Calculate remaining amount
                try {
                    double salary = Double.parseDouble(s.toString());
                    double remainingAmount = salary - total;
                    remainingAmountView.setText(String.valueOf(remainingAmount));
                } catch (NumberFormatException e) {
                    // Handle empty or invalid input
                    remainingAmountView.setText("-----");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        });
    }
}