package com.appsnipp.e4solutions.Adapters;//
// Decompiled by Procyon v0.5.36
//

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public abstract class DiffRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
{
    protected AdapterDataChangedListener adapterDataChangedListener;
    private List<T> objects;
    protected AdapterView.OnItemClickListener onItemClickListener;
    private List<T> temp;

    public DiffRecyclerViewAdapter() {
        this((List)new ArrayList());
    }

    public DiffRecyclerViewAdapter(final List<T> list) {
        this.objects = new ArrayList<T>((Collection<? extends T>)list);
        this.temp = new ArrayList<T>((Collection<? extends T>)list);
    }

    private void notifyChanged() {
        DiffUtil.Callback callback= new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return DiffRecyclerViewAdapter.this.objects.size();
            }

            @Override
            public int getNewListSize() {
                return DiffRecyclerViewAdapter.this.temp.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                final DiffRecyclerViewAdapter this$0 = DiffRecyclerViewAdapter.this;
                return this$0.areContentsTheSame(this$0.objects.get(oldItemPosition), DiffRecyclerViewAdapter.this.temp.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                final DiffRecyclerViewAdapter this$0 = DiffRecyclerViewAdapter.this;
                return this$0.areItemsTheSame(this$0.objects.get(oldItemPosition), DiffRecyclerViewAdapter.this.temp.get(newItemPosition));
            }
        };
        this.objects.clear();
        this.objects.addAll((Collection<? extends T>)this.temp);
        // callback.
        final AdapterDataChangedListener adapterDataChangedListener = this.adapterDataChangedListener;
        if (adapterDataChangedListener != null) {
            adapterDataChangedListener.onChanged();
        }
    }

    public void add(final int n, final T t) {
        this.temp.add(n, t);
        this.notifyChanged();
    }

    public void add(final T t) {
        this.temp.add(t);
        this.notifyChanged();
    }

    public void addAll(final List<T> list) {
        this.temp.addAll((Collection<? extends T>)list);
        this.notifyChanged();
    }

    public boolean areContentsTheSame(final T t, final T obj) {
        return t.equals(obj);
    }

    public boolean areItemsTheSame(final T t, final T obj) {
        return t.equals(obj);
    }

    public void clear() {
        this.objects.clear();
    }

    public boolean contains(final T t) {
        return this.objects.contains(t);
    }

    public T get(final int n) {
        return this.objects.get(n);
    }

    public List<T> get() {
        return Collections.unmodifiableList((List<? extends T>)this.objects);
    }

    @Override
    public int getItemCount() {
        return this.objects.size();
    }

    public int indexOf(final T t) {
        return this.objects.indexOf(t);
    }


    @Override
    public void onBindViewHolder(final VH vh, final int n) {
        final T value = this.get(n);
        //vh.itemView.setOnClickListener((AdapterView.OnItemClickListener) this);
        this.onBindViewHolder((VH) vh, value);
    }

    public void onBindViewHolder(VH paramVH, T paramT) {}

    public void remove(final int n) {
        this.temp.remove(n);
        this.notifyChanged();
    }

    public void remove(final T t) {
        this.temp.remove(t);
        this.notifyChanged();
    }

    public void set(final int n, final T t) {
        this.temp.set(n, t);
        this.notifyChanged();
    }

    public void set(final List<T> c) {
        this.temp = new ArrayList<T>((Collection<? extends T>)c);
        this.notifyChanged();
    }

    public void setAdapterDataChangedListener(final AdapterDataChangedListener adapterDataChangedListener) {
        this.adapterDataChangedListener = adapterDataChangedListener;
    }

    public void setOnItemClickListener(final AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void swap(final int i, final int j) {
        Collections.swap(this.temp, i, j);
        this.notifyChanged();
    }

    public interface AdapterDataChangedListener
    {
        void onChanged();
    }
}
