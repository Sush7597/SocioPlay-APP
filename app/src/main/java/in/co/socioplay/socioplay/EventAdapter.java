package in.co.socioplay.socioplay;

        import android.support.annotation.NonNull;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;
        import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private List<Event> eventList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date , desc;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            desc = view.findViewById(R.id.desc);
            date = view.findViewById(R.id.date);
        }
    }


    EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.title.setText(event.getTitle());
        holder.desc.setText(event.getDesc());
        holder.date.setText(event.getDate());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}