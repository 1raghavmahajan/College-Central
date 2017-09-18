package com.blackboxindia.PostIT.HelperClasses;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.PostIT.R;

public class OptionsDialog {

    private Context context;

    public static OptionsDialog using(Context context){
        OptionsDialog textDialog = new OptionsDialog();
        textDialog.context = context;
        return textDialog;
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

    void populate(String college){
//        new NetworkMethods(context)
    }

    public interface ClickListener {
        void onItemSelect(String name);
        void onNewItem(String name);
    }

}
