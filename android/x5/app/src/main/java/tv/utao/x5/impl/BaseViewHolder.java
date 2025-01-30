package tv.utao.x5.impl;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    protected final T binding;

    public BaseViewHolder(T t) {
        super(t.getRoot());
        this.binding = t;
    }

    public T getBinding() {
        return binding;
    }

}