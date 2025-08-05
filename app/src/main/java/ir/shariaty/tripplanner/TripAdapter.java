package ir.shariaty.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private Context context;
    private List<Trip> tripList;

    public TripAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);

        holder.tvDestination.setText(trip.getDestination());

        String info = "(" + trip.getDepartureDate() + " to " + trip.getReturnDate()
                + " / with " + trip.getCompanions()
                + " / Trip Type: " + trip.getTripType()
                + " / Budget: " + trip.getBudget() + ")";
        holder.tvTripInfo.setText(info);

        holder.checkBoxContainer1.removeAllViews();
        holder.checkBoxContainer2.removeAllViews();

        Map<String, Boolean> packingList = trip.getPackingList();
        if (packingList != null) {
            int index = 0;

            Typeface font = ResourcesCompat.getFont(context, R.font.andikanewbasici);

            for (Map.Entry<String, Boolean> entry : packingList.entrySet()) {
                String item = entry.getKey();
                boolean isChecked = Boolean.TRUE.equals(entry.getValue());

                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(item);
                checkBox.setChecked(isChecked);
                checkBox.setTextColor(0x99333333);
                checkBox.setTextSize(13f);
                checkBox.setTypeface(font);

                checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
                    if (trip.getId() != null) {
                        FirebaseFirestore.getInstance()
                                .collection("trips")
                                .document(trip.getId())
                                .update("packingList." + item, checked);
                    }
                });

                if (index % 2 == 0) {
                    holder.checkBoxContainer1.addView(checkBox);
                } else {
                    holder.checkBoxContainer2.addView(checkBox);
                }

                index++;
            }
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (trip.getId() != null) {
                FirebaseFirestore.getInstance()
                        .collection("trips")
                        .document(trip.getId())
                        .delete();
                tripList.remove(position);
                notifyItemRemoved(position);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddTripActivity.class);
            intent.putExtra("tripId", trip.getId());
            intent.putExtra("destination", trip.getDestination());
            intent.putExtra("departureDate", trip.getDepartureDate());
            intent.putExtra("returnDate", trip.getReturnDate());
            intent.putExtra("companions", trip.getCompanions());
            intent.putExtra("tripType", trip.getTripType());
            intent.putExtra("budget", trip.getBudget());

            // انتقال Map به عنوان Serializable
            if (trip.getPackingList() != null) {
                HashMap<String, Boolean> packingMap = new HashMap<>(trip.getPackingList());
                intent.putExtra("packingListMap", packingMap);
            }

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public void updateList(List<Trip> newList) {
        this.tripList = newList;
        notifyDataSetChanged();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tvDestination, tvTripInfo;
        LinearLayout checkBoxContainer1, checkBoxContainer2;
        Button btnEdit, btnDelete;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvTripInfo = itemView.findViewById(R.id.tvTripInfo);
            checkBoxContainer1 = itemView.findViewById(R.id.checkboxContainer1);
            checkBoxContainer2 = itemView.findViewById(R.id.checkboxContainer2);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

