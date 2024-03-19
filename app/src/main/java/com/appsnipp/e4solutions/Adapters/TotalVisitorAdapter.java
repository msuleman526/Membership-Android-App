package com.appsnipp.e4solutions.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.e4solutions.Models.VisitorEdge;
import com.appsnipp.e4solutions.R;
import com.appsnipp.e4solutions.Utils.SessionUtil;
import com.appsnipp.e4solutions.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;

public class TotalVisitorAdapter extends DiffRecyclerViewAdapter<VisitorEdge, TotalVisitorAdapter.ViewHolder>
{
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT;
    private OnToDoListItemClickListener onToDoListItemClickListener;
    private OnItemClickListener onItemClickListener;
    private boolean readOnly;
    Activity context;
    static {
        SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE, MMM dd, yyyy hh:mm aaa");
    }

    public TotalVisitorAdapter(Activity context) {
        this.context = context;
        this.onToDoListItemClickListener = adapterinstance.a(OnToDoListItemClickListener.class);
        this.onItemClickListener = adapterinstance.a(OnItemClickListener.class);
    }

    @Override
    public boolean areContentsTheSame(final VisitorEdge toDo, final VisitorEdge toDo2) {
        return false;
    }

    @Override
    public long getItemId(final int n) {
        return Integer.parseInt(get(n).id);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        final Context context = viewHolder.itemView.getContext();
        final VisitorEdge visitor = (VisitorEdge) ((DiffRecyclerViewAdapter<VisitorEdge, ViewHolder>)this).get(position);

        viewHolder.phoneTxt.setText(visitor.primaryphone);
        viewHolder.nameTxt.setText(visitor.firstname + " " + visitor.surname);
        viewHolder.addressTxt.setText(""+ Utils.getAddress(visitor.address_1, visitor.address_2, visitor.suburb, visitor.state, visitor.postcode));
        viewHolder.visitorID.setText(visitor.visitorid);
        Picasso.get().load(SessionUtil.getUrl(context)+"/media?type=m&id="+visitor.id+"&field=undefined").placeholder(context.getResources().getDrawable(R.drawable.profile)).into(viewHolder.photo);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onAppointmentItemClickListener.onAppointmentClick(appointment);
                onToDoListItemClickListener.onTodoClick(visitor,position);
            }
        });

        viewHolder.covertIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onAppointmentItemClickListener.onAppointmentClick(appointment);
                onItemClickListener.onItemClick(visitor,position);
            }
        });
    }

    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_visitor_item, viewGroup, false));
    }

    public interface OnToDoListItemClickListener
    {
        void onTodoClick(VisitorEdge p0,int index);
    }

    public interface OnItemClickListener
    {
        void onItemClick(VisitorEdge p0,int index);
    }



    public void setOnEToDoItemClickListener(final OnToDoListItemClickListener onToDoItemClickListener) {
        this.onToDoListItemClickListener = onToDoItemClickListener;
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView employeeItem;
        TextView visitorID;
        TextView nameTxt, addressTxt, phoneTxt;
        ImageView photo, covertIcon;
        public ViewHolder(final View view) {
            super(view);
            this.employeeItem = view.findViewById(R.id.employeeItem);
            this.visitorID = view.findViewById(R.id.visitorIDTxt);
            this.phoneTxt = view.findViewById(R.id.phoneNo);
            this.addressTxt = view.findViewById(R.id.addressTxt);
            this.photo = view.findViewById(R.id.employee_photo_image_view);
            this.nameTxt = view.findViewById(R.id.employee_name_text_view);
            this.covertIcon = view.findViewById(R.id.covertIcon);
        }
    }
    public static class adapterinstance implements InvocationHandler {
        public static <T> T a(final Class<T> clazz) {
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new adapterinstance());
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            return null;
        }
    }
}
