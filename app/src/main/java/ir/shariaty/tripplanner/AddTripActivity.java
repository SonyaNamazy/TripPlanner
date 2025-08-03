package ir.shariaty.tripplanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTripActivity extends AppCompatActivity {

    EditText etDestination, etDepartureDate, etReturnDate, etCompanions, etTripType, etBudget, etPackingItem;
    CheckBox checkboxReminder;
    FloatingActionButton btnAddPacking;
    Button btnSave, btnCancel;

    ArrayList<String> packingList = new ArrayList<>();
    UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        dbHelper = new UserDatabaseHelper(this);

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
                packingList.add(item);
                etPackingItem.setText("");
                Toast.makeText(this, "Item added to packing list", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a packing item", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> saveTrip());

        btnCancel.setOnClickListener(v -> {
            finish(); // یا startActivity(new Intent(this, HomeActivity.class));
        });
    }

    private void showDatePickerDialog(final EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String dateStr = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
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

        String packingListString = TextUtils.join(", ", packingList);

        boolean reminder = checkboxReminder.isChecked();

        boolean inserted = dbHelper.insertTrip(destination, departureDate, returnDate,
                companions, tripType, budget, packingListString, reminder);

        if (inserted) {
            Toast.makeText(this, "سفر شما ذخیره شد", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "خطا در ذخیره سفر", Toast.LENGTH_SHORT).show();
        }
    }
}

