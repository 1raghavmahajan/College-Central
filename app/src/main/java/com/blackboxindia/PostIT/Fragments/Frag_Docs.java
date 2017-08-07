package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboxindia.PostIT.Network.CloudStorageMethods;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.adapters.DocumentAdapter;
import com.blackboxindia.PostIT.dataModels.Directory;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class Frag_Docs extends Fragment {

    private static final String TAG = Frag_Docs.class.getSimpleName()+" YOYO";

    View  mainView;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    Context context;
    Directory directory;
    CloudStorageMethods cloudStorageMethods;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.frag_docs, container, false);
        context = mainView.getContext();
        cloudStorageMethods = new CloudStorageMethods(context);
        Paper.init(context);

        swipeRefreshLayout = mainView.findViewById(R.id.docs_swipe_refresh_layout);
        recyclerView = mainView.findViewById(R.id.docs_recycler);

        if(((MainActivity)context).offlineMode){
            swipeRefreshLayout.setRefreshing(true);
            directory = Paper.book().read("Root", null);

            if(directory != null){
                //Log.i(TAG, "onCreateView: nice");
                setUpRecycler();
            }else
                //Log.i(TAG, "onCreateView: ehh");

            swipeRefreshLayout.setRefreshing(false);

        }else {
            swipeRefreshLayout.setRefreshing(true);
            getData();
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getData();
                }
            });
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                ((MainActivity) context).createSnackbar("Showing documents for IIT Indore");
            }
        }

        return mainView;
    }

    private void setUpRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new DocumentAdapter(context,directory));
    }

    public void getData() {

        new NetworkMethods(context).getAllFiles(((MainActivity)context).userInfo.getCollegeName(), new onCompleteListener<Directory>() {

            @Override
            public void onSuccess(Directory dir) {

                Paper.init(context);
                Paper.book().write("Root",dir);

                directory = dir;
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);

                if(recyclerView.getAdapter()!=null)
                    ((DocumentAdapter)recyclerView.getAdapter()).changeRoot(directory);
                else
                    setUpRecycler();
            }

            @Override
            public void onFailure(Exception e) {
                //Log.e(TAG, "onFailure: getDir", e);
            }

        });

    }

    @Override
    public void onResume() {
        ((MainActivity)context).toolbar.setTitle(MainActivity.TITLE_Documents);
//        MenuItem item = ((MainActivity) getActivity()).toolbar.getMenu().findItem(R.id.toolbar_download);
//        item.setVisible(true);
//        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if(item.getItemId() == R.id.toolbar_download){
////                    downloadAll();
//                }
//                return true;
//            }
//        });
        super.onResume();
    }

//    private void downloadAll() {
//        final int i = directory.folders.size();
//        for (int j = 0; j< directory.files.size(); j++){
//
//            final int finalJ = j;
//            String name = directory.files.get(j);
//            String college;
//            if(((MainActivity)context).userInfo!=null)
//                college = ((MainActivity)context).userInfo.getCollegeName();
//            else
//                college = "IIT Indore";
//
//            final DonutProgress donutProgress = recyclerView.getChildAt(i + j).findViewById(R.id.donut_progress);
//            donutProgress.setMax(100);
//            donutProgress.setVisibility(View.VISIBLE);
//
//            recyclerView.getLayoutManager().isViewPartiallyVisible(recyclerView.getChildAt(i+finalJ),false,true)
//
//            final File file = new File(context.getExternalFilesDir(DIRECTORY_DOCUMENTS), name + ".pdf");
//            if (!file.exists()) {
//                FirebaseStorage.getInstance().getReference().child(DIRECTORY_DATA).child(college).child(name + ".pdf")
//                        .getFile(file)
//                        .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    //Log.i(TAG, "downloadAll: "+i+" "+finalJ);
//                                    donutProgress.setProgress(100);
////                                    ((DocumentAdapter.mViewHolder) recyclerView.findViewHolderForAdapterPosition(i + finalJ)).setProgress(100);
//                                } else {
//                                    //Log.e(TAG, "onComplete: failure "+finalJ+" ", task.getException());
//                                }
//                            }
//                        })
//                        .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
//                            @Override
//                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                float p = (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) * 100;
//                                //Log.i(TAG, "onProgress downloadFile percentage: " + p);
//                                donutProgress.setProgress(p);
//                                //Log.i(TAG, "downloadAll: "+i+" "+finalJ);
////                                ((DocumentAdapter.mViewHolder) recyclerView.findViewHolderForAdapterPosition(i + finalJ)).setProgress(p);
//                            }
//                        });
//            }else {
//                donutProgress.setProgress(100);
//                //Log.i(TAG, "downloadAll: "+i+" "+finalJ);
////                ((DocumentAdapter.mViewHolder) recyclerView.findViewHolderForAdapterPosition(i + finalJ)).setProgress(100);
//            }
//        }
//    }

    @Override
    public void onDestroy() {
        ((MainActivity)context).backPressedListener = null;
//        List<FileDownloadTask> activeDownloadTasks = FirebaseStorage.getInstance().getReference().getActiveDownloadTasks();
//        for (FileDownloadTask activeDownloadTask : activeDownloadTasks) {
//            activeDownloadTask.cancel();
//        }
        super.onDestroy();
    }

}
