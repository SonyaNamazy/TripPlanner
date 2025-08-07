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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

        int days = calculateDays(trip.getDepartureDate(), trip.getReturnDate());
        String info = "(" + days + " days / with " + trip.getCompanions()
                + " / Trip Type: " + trip.getTripType()
                + " / Budget: " + trip.getBudget() + ")";
        holder.tvTripInfo.setText(info);

        holder.checkBoxContainer1.removeAllViews();
        holder.checkBoxContainer2.removeAllViews();

        Map<String, Boolean> packingList = trip.getPackingList();
        boolean[] isExpanded = {false};

        List<View> hiddenItemsCol1 = new ArrayList<>();
        List<View> hiddenItemsCol2 = new ArrayList<>();

        if (packingList != null && !packingList.isEmpty()) {
            List<Map.Entry<String, Boolean>> items = new ArrayList<>(packingList.entrySet());
            Typeface font = ResourcesCompat.getFont(context, R.font.andikanewbasici);

            for (int i = 0; i < items.size(); i++) {
                Map.Entry<String, Boolean> entry = items.get(i);
                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(entry.getKey());
                checkBox.setChecked(Boolean.TRUE.equals(entry.getValue()));
                checkBox.setTextColor(0x99333333);
                checkBox.setTextSize(13f);
                checkBox.setTypeface(font);

                int finalI = i;
                checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
                    if (trip.getId() != null) {
                        FirebaseFirestore.getInstance()
                                .collection("trips")
                                .document(trip.getId())
                                .update("packingList." + items.get(finalI).getKey(), checked);
                    }
                });

                if (i % 2 == 0) {
                    holder.checkBoxContainer1.addView(checkBox);
                    if (i >= 4) hiddenItemsCol1.add(checkBox);
                } else {
                    holder.checkBoxContainer2.addView(checkBox);
                    if (i >= 4) hiddenItemsCol2.add(checkBox);
                }
            }

            for (View v : hiddenItemsCol1) v.setVisibility(View.GONE);
            for (View v : hiddenItemsCol2) v.setVisibility(View.GONE);

            if (items.size() > 4) {
                holder.btnToggleDetails.setVisibility(View.VISIBLE);
                holder.btnToggleDetails.setText("more details...");

                holder.btnToggleDetails.setOnClickListener(v -> {
                    isExpanded[0] = !isExpanded[0];
                    for (View cb : hiddenItemsCol1) cb.setVisibility(isExpanded[0] ? View.VISIBLE : View.GONE);
                    for (View cb : hiddenItemsCol2) cb.setVisibility(isExpanded[0] ? View.VISIBLE : View.GONE);
                    holder.btnToggleDetails.setText(isExpanded[0] ? "less details..." : "more details...");
                });
            } else {
                holder.btnToggleDetails.setVisibility(View.GONE);
            }
        } else {
            holder.btnToggleDetails.setVisibility(View.GONE);
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

            if (trip.getPackingList() != null) {
                HashMap<String, Boolean> packingMap = new HashMap<>(trip.getPackingList());
                intent.putExtra("packingListMap", packingMap);
            }

            context.startActivity(intent);
        });
    }

    private int calculateDays(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        try {
            Date d1 = sdf.parse(start);
            Date d2 = sdf.parse(end);
            long diff = d2.getTime() - d1.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            return 0;
        }
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
        TextView tvDestination, tvTripInfo, btnToggleDetails;
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
            btnToggleDetails = itemView.findViewById(R.id.btnToggleDetails);
        }
    }
}





