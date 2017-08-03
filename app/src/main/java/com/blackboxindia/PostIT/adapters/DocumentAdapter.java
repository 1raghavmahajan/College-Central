package com.blackboxindia.PostIT.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.PostIT.HelperClasses.FileOpener;
import com.blackboxindia.PostIT.Network.CloudStorageMethods;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.dataModels.Directory;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import static com.blackboxindia.PostIT.Network.NetworkMethods.TYPE_FOLDER;
import static com.blackboxindia.PostIT.Network.NetworkMethods.TYPE_PDF;


public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.mViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private Directory directory;
    private Directory root;
    private Stack<Integer> path;


    public DocumentAdapter(Context context, Directory dir){
        this.context = context;
        directory = dir;
        root = dir;
        path = new Stack<>();
        inflater = LayoutInflater.from(context);
        ((MainActivity)context).onBackPressedListener = new MainActivity.onBackPressedListener() {
            @Override
            public boolean doneSomething() {
                return goBack();
            }
        };
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new mViewHolder(inflater.inflate(R.layout.card_doc,parent,false));
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        if(position<directory.folders.size()){
            holder.setData(directory.folders.get(position).name, TYPE_FOLDER, position);
        }else {
            holder.setData(directory.files.get(position - directory.folders.size()), TYPE_PDF, position);
        }
    }

    @Override
    public int getItemCount() {
        return (directory.files.size()+directory.folders.size());
    }

    public void changeRoot(Directory dir) {
        root = dir;
        Directory d = root;
        for (Integer integer : path) {
            d = d.folders.get(integer);
        }
        directory = d;
        notifyDataSetChanged();
    }

    private boolean goBack(){
        if(path.size()>0){
            path.pop();
            Directory d = root;
            for (Integer integer : path) {
                d = d.folders.get(integer);
            }
            directory = d;
            notifyDataSetChanged();
            return true;
        }else {
            return false;
        }
    }

    class mViewHolder extends RecyclerView.ViewHolder{

        ImageView icon;
        TextView tvTitle;
        CardView card;

        mViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.file_icon);
            tvTitle = itemView.findViewById(R.id.title);
            card = itemView.findViewById(R.id.card_doc);
        }

        public void setData(final String name, String type, final int position){
            switch (type){
                case TYPE_FOLDER:
                    icon.setImageResource(R.drawable.ic_folder);
                    card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            path.push(position);
                            directory = directory.folders.get(position);
                            notifyDataSetChanged();
                        }
                    });
                    break;
                case TYPE_PDF:
                    icon.setImageResource(R.drawable.ic_pdf);
                    card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(((MainActivity)context).offlineMode) {
                                new CloudStorageMethods(context).getDownloadedFile(name, ((MainActivity) context).userInfo.getCollegeName(), new onCompleteListener<File>() {
                                    @Override
                                    public void onSuccess(File file) {
                                        try {
                                            FileOpener.using(context).openFile(file);
                                        } catch (IOException e) {
                                            Toast.makeText(context, "Error opening file", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        ((MainActivity) context).createSnackbar("Cannot download file in offline mode", Snackbar.LENGTH_INDEFINITE, true, "Go Online", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ((MainActivity) context).goOnline();
                                            }
                                        });
                                    }
                                });
                            }else {
                                final ProgressDialog dialog = ProgressDialog.show(context, "Downloading file", "Please wait", true, true, new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        List<FileDownloadTask> activeDownloadTasks = FirebaseStorage.getInstance().getReference().getActiveDownloadTasks();
                                        for (FileDownloadTask activeDownloadTask : activeDownloadTasks) {
                                            activeDownloadTask.cancel();
                                        }
                                    }
                                });
                                new CloudStorageMethods(context).downloadFile(name, ((MainActivity) context).userInfo.getCollegeName(), new onCompleteListener<File>() {
                                    @Override
                                    public void onSuccess(File file) {
                                        dialog.cancel();
                                        try {
                                            FileOpener.using(context).openFile(file);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        dialog.cancel();
                                    }
                                });
                            }
                        }
                    });
                    break;
            }
            tvTitle.setText(name);
        }

    }

}
