package ir.shariaty.tripplanner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTripActivity extends AppCompatActivity {

    EditText etDestination, etDepartureDate, etReturnDate, etCompanions, etTripType, etBudget, etPackingItem;
    CheckBox checkboxReminder;
    FloatingActionButton btnAddPacking;
    Button btnSave, btnCancel;

    Map<String, Boolean> packingList = new HashMap<>();
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etDestination = findViewById(R.id.etDestination);
        etDepartureDate = findViewById(R.id.etDepartureDate);
        etReturnDate = findViewById(R.id.etReturnDate);
        etCompanions = findViewById(R.id.etCompanions);
        etTripType = findViewById(R.id.etTripType);
        etBudget = findViewById(R.id.etBudget);
        etPackingItem = findViewById(R.id.etPackingItem);
        checkboxReminder = findViewById(R.id.checkboxReminder);
        btnAddPacking = findViewById(R.id.btnAddPacking);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        etDepartureDate.setOnClickListener(v -> showDatePickerDialog(etDepartureDate));
        etReturnDate.setOnClickListener(v -> showDatePickerDialog(etReturnDate));

        btnAddPacking.setOnClickListener(v -> {
            String item = etPackingItem.getText().toString().trim();
            if (!item.isEmpty()) {
                packingList.put(item, false);
                etPackingItem.setText("");
                Toast.makeText(this, "Item added to packing list", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a packing item", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> saveTrip());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog(final EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String dateStr = year1 + "/" + (month1 + 1) + "/" + dayOfMonth;
                    targetEditText.setText(dateStr);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveTrip() {
        String destination = etDestination.getText().toString().trim();
        String departureDate = etDepartureDate.getText().toString().trim();
        String returnDate = etReturnDate.getText().toString().trim();
        String companions = etCompanions.getText().toString().trim();
        String tripType = etTripType.getText().toString().trim();
        String budget = etBudget.getText().toString().trim();

        if (destination.isEmpty() || departureDate.isEmpty()) {
            Toast.makeText(this, "Destination and Departure Date are required", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean reminder = checkboxReminder.isChecked();
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> trip = new HashMap<>();
        trip.put("destination", destination);
        trip.put("departureDate", departureDate);
        trip.put("returnDate", returnDate);
        trip.put("companions", companions);
        trip.put("tripType", tripType);
        trip.put("budget", budget);
        trip.put("packingList", new HashMap<>(packingList));
        trip.put("reminder", reminder);
        trip.put("userId", userId);

        firestore.collection("trips")
                .add(trip)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Trip saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving trip: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}




