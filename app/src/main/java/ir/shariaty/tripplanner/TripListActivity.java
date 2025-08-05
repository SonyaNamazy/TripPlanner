package ir.shariaty.tripplanner;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TripListActivity extends AppCompatActivity {

    private RecyclerView recyclerTripList;
    private TripAdapter tripAdapter;
    private List<Trip> tripList;
    private FirebaseFirestore firestore;
    private TextView tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        recyclerTripList = findViewById(R.id.recyclerTripList);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        firestore = FirebaseFirestore.getInstance();
        tripList = new ArrayList<>();

        recyclerTripList.setLayoutManager(new LinearLayoutManager(this));
        tripAdapter = new TripAdapter(this, tripList);
        recyclerTripList.setAdapter(tripAdapter);

        loadTripsFromFirebase();
    }
    private void loadTripsFromFirebase() {
        CollectionReference tripsRef = firestore.collection("trips");

        tripsRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                return;
            }

            tripList.clear();

            for (DocumentSnapshot document : snapshots.getDocuments()) {
                Trip trip = document.toObject(Trip.class);
                if (trip != null) {
                    trip.setId(document.getId());

                    // اصلاح نوع packingList
                    Object packingObj = document.get("packingList");
                    if (packingObj instanceof Map) {
                        try {
                            Map<String, Boolean> packingMap = (Map<String, Boolean>) packingObj;
                            trip.setPackingList(packingMap);
                        } catch (Exception ex) {
                            trip.setPackingList(null);
                        }
                    } else {
                        trip.setPackingList(null);
                    }

                    tripList.add(trip);
                }
            }

            if (tripList.isEmpty()) {
                tvEmptyMessage.setVisibility(View.VISIBLE);
            } else {
                tvEmptyMessage.setVisibility(View.GONE);
            }

            tripAdapter.updateList(tripList);
        });
    }

}

