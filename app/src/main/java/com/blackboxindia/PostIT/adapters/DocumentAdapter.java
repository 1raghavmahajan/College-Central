package com.blackboxindia.PostIT.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.dataModels.Directory;


public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.mViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private Directory directory;

    public DocumentAdapter(Context context, Directory dir){
        this.context = context;
        directory = dir;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new mViewHolder(inflater.inflate(R.layout.card_doc,parent,false));
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class mViewHolder extends RecyclerView.ViewHolder{

        ImageView icon;
        TextView name;

        mViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.file_icon);
            name = (TextView) itemView.findViewById(R.id.title);
        }

        public void setData(){

        }

    }

}
