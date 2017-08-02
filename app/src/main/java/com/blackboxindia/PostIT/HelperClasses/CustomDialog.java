package com.blackboxindia.PostIT.HelperClasses;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.PostIT.R;

public class CustomDialog {

    private Context context;

    public static CustomDialog using(Context context){
        CustomDialog customDialog = new CustomDialog();
        customDialog.context = context;
        return customDialog;
    }

    public void create(String title, final ClickListener listener){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_text);
        dialog.findViewById(R.id.dialog_Submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = dialog.findViewById(R.id.dialog_text);
                String s = editText.getText().toString().trim();
                if (s.equals(""))
                    Toast.makeText(context, "Invalid name", Toast.LENGTH_SHORT).show();
                else if(s.contains(".") || s.contains("#") || s.contains("$") || s.contains("[") || s.contains("]") || s.contains("/"))
                    editText.setError("'.', '#', '$', '[', ']', '/'  not allowed");
                else {
                    dialog.cancel();
                    listener.onNewItem(s);
                }
            }
        });
        dialog.findViewById(R.id.dialog_Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        ((TextView)dialog.findViewById(R.id.dialog_Title)).setText(title);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public interface ClickListener {
        void onItemSelect(String name);
        void onNewItem(String name);
    }

}
