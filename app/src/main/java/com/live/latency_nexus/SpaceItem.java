package com.live.latency_nexus;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.live.latency_nexus.Adapters.DrawerAdapter;

public class SpaceItem extends DrawerItem<SpaceItem.ViewHolder>{

    private  int spaceDp;
    public SpaceItem(int spaceDp){
        this.spaceDp=spaceDp;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        Context c=parent.getContext();
        View view=new View(c);
        int height=(int)(c.getResources().getDisplayMetrics().density*spaceDp);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
        ));
        return new ViewHolder(view);
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {

    }

    public  class ViewHolder extends DrawerAdapter.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
