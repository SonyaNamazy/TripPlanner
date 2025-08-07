package ir.shariaty.tripplanner;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("trips")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    tripList.clear();

                    for (DocumentSnapshot document : snapshots.getDocuments()) {
                        Trip trip = document.toObject(Trip.class);
                        if (trip != null) {
                            trip.setId(document.getId());

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


