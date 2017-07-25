package com.blackboxindia.PostIT.HelperClasses;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.blackboxindia.PostIT.BuildConfig;

import java.io.File;
import java.io.IOException;

public class FileOpener {

    private Context context;

    private FileOpener(){}

    public static FileOpener using(Context context) {
        FileOpener fileOpener = new FileOpener();
        fileOpener.context = context;
        return fileOpener;
    }

    public void openFile(File url) throws IOException {

        Uri uri = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider", url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Word document
        if (url.toString().contains(".doc") || url.toString().contains(".docx"))
            intent.setDataAndType(uri, "application/msword");

        else if(url.toString().contains(".pdf"))
            intent.setDataAndType(uri, "application/pdf");

        else if(url.toString().contains(".ppt") || url.toString().contains(".pptx"))
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

        else if(url.toString().contains(".xls") || url.toString().contains(".xlsx"))
            intent.setDataAndType(uri, "application/vnd.ms-excel");

        else if(url.toString().contains(".zip") || url.toString().contains(".rar"))
            intent.setDataAndType(uri, "application/zip");

        else if(url.toString().contains(".rtf"))
            intent.setDataAndType(uri, "application/rtf");

        else if(url.toString().contains(".wav") || url.toString().contains(".mp3"))
            intent.setDataAndType(uri, "audio/x-wav");

        else if(url.toString().contains(".gif"))
            intent.setDataAndType(uri, "image/gif");

        else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png"))
            intent.setDataAndType(uri, "image/jpeg");

        else if(url.toString().contains(".txt"))
            intent.setDataAndType(uri, "text/plain");

        else if(url.toString().contains(".3gp") ||
                                url.toString().contains(".mpg") ||
                                url.toString().contains(".mpeg") ||
                                url.toString().contains(".mpe") ||
                                url.toString().contains(".mp4") ||
                                url.toString().contains(".avi"))
            intent.setDataAndType(uri, "video/*");

        else
            intent.setDataAndType(uri, "*/*");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
