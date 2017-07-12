package com.blackboxindia.PostIT.dataModels;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.blackboxindia.PostIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.activities.MainActivity;

import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "unused"})
public class UserInfo implements Parcelable{

    //region Variables

    private boolean hasProfileIMG;
    private String uID;
    private String name;
    private String email;
    private String roomNumber;
    private String phone;
    private String hostel;
    private String collegeName;

    private ArrayList<String> userAdKeys;

    //endregion

    //region Constructors

    public UserInfo(){
        uID = null;
        hasProfileIMG = false;
        userAdKeys = new ArrayList<>();
    }

    protected UserInfo(Parcel in) {
        hasProfileIMG = in.readByte() != 0;
        uID = in.readString();
        name = in.readString();
        email = in.readString();
        roomNumber = in.readString();
        phone = in.readString();
        hostel = in.readString();
        collegeName = in.readString();
        userAdKeys = in.createStringArrayList();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public UserInfo createCopy() {
        UserInfo userInfo = new UserInfo();
        userInfo.setData(name,email, roomNumber,phone);
        userInfo.setuID(uID);
        userInfo.setHasProfileIMG(hasProfileIMG);
        for(String s:userAdKeys){
            userInfo.addUserAd(s);
        }
        if(collegeName!=null)
            userInfo.setCollegeName(collegeName);
        return userInfo;
    }

    //endregion

    public void setData(String name, String email, String roomNumber, String phone) {
        this.name = name;
        this.email = email;
        this.roomNumber = roomNumber;
        this.phone = phone;
        this.hostel = "Simrol Hostel";
        this.collegeName = "IIT Indore";
    }

    public void setData(String name, String email, String roomNumber, String phone, String hostel, String collegeName) {
        this.name = name;
        this.email = email;
        this.roomNumber = roomNumber;
        this.phone = phone;
        this.hostel = hostel;
        this.collegeName = collegeName;
    }

    public void login(final String email, final String password, final Context context) {

        final ProgressDialog dialog = ProgressDialog.show(context, "Logging in..", "", true, false);
        NetworkMethods net = new NetworkMethods(context);
        net.Login(email, password, new onLoginListener() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                dialog.cancel();
                UserCred userCred = new UserCred(email, password);
                userCred.save_cred(context);

                ((MainActivity) context).UpdateUI(userInfo, true, true);
            }

            @Override
            public void onFailure(Exception e) {
                if (e != null) {
                    dialog.cancel();
                    if (e.getMessage().contains("network")) {
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                    } else if (e.getMessage().contains("password") && e.getMessage().contains("invalid")) {
                        Toast.makeText(context, "Invalid Password.", Toast.LENGTH_SHORT).show();
                    } else if (e.getMessage().contains("no user record")) {
                        Toast.makeText(context, "Invalid Email ID.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void addUserAd(String userAdKey) {
        if(userAdKeys==null)
            userAdKeys = new ArrayList<>();
        userAdKeys.add(userAdKey);
    }

    public void removeUserAd(String userAdKey) {
        if(userAdKeys.contains(userAdKey))
            userAdKeys.remove(userAdKey);
    }

    //region Cache User Details
    /*
        public static void cacheUserDetails(UserInfo userInfo, Context context) {
            final String FILENAME = "profile_Img";

            SharedPreferences cache = context.getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = cache.edit();

            String profileIMG = userInfo.getHasProfileIMG();
            if(profileIMG !=null) {
                Log.i("YOYO","write: "+profileIMG);

                FileOutputStream fos;
                try {
                    fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    byte[] bytes = profileIMG.getBytes("UTF-8");
                    Log.i("YOYO",new String(bytes,"UTF-8"));
                    fos.write(bytes);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //edit.putString("hasProfileIMG", userInfo.getHasProfileIMG());
            edit.putString("uID", userInfo.getuID());
            edit.putString("name", userInfo.getName());
            edit.putString("email", userInfo.getEmail());
            edit.putString("roomNumber", userInfo.getRoomNumber());
            edit.putString("phone", userInfo.getPhone());

            Set<String> UserAdKeys = new HashSet<>(userInfo.getUserAdKeys());

            edit.putStringSet("userAdKeys", UserAdKeys );

            if(userInfo.getCollegeName()!=null)
                edit.putString("collegeName", userInfo.getCollegeName());

            edit.apply();
        }

        public static UserInfo getCachedUserDetails(String uID, Context context) {
        final String FILENAME = "profile_Img";

        UserInfo userInfo = new UserInfo();

        SharedPreferences cache = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String id = cache.getString("uID", null);
        if(id != null) {

            if(id.equals(uID)) {

                try {
                    FileInputStream fileInputStream = context.openFileInput(FILENAME);
                    byte[] bytes = new byte[1024*200];
                    //noinspection ResultOfMethodCallIgnored
                    fileInputStream.read(bytes);
                    String s = new String(bytes,"UTF-8");
                    Log.i("YOYO","read: "+s);
                    userInfo.setHasProfileIMG(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                userInfo.setuID(id);
                userInfo.setName(cache.getString("name", null));
                userInfo.setEmail(cache.getString("email", null));
                userInfo.setRoomNumber(cache.getString("roomNumber", null));
                userInfo.setPhone(cache.getString("phone", null));

                Set<String> userAdKeys = cache.getStringSet("userAdKeys", null);
                ArrayList<String> keys;
                if (userAdKeys != null)
                    keys = new ArrayList<>(userAdKeys);
                else
                    keys = new ArrayList<>();

                userInfo.setUserAdKeys(keys);

                userInfo.setHasProfileIMG(cache.getString("collegeName", "IIT Indore"));

                return userInfo;
            }
        }
        return null;
    }
    */
    //endregion

    //region Getters and Setters

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public ArrayList<String> getUserAdKeys() {
        return userAdKeys;
    }

    public void setUserAdKeys(ArrayList<String> userAdKeys) {
        this.userAdKeys = userAdKeys;
    }

    public boolean getHasProfileIMG() {
        return hasProfileIMG;
    }

    public void setHasProfileIMG(boolean hasProfileIMG) {
        this.hasProfileIMG = hasProfileIMG;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getHostel() {
        return hostel;
    }

    public void setHostel(String hostel) {
        this.hostel = hostel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (hasProfileIMG ? 1 : 0));
        dest.writeString(uID);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(roomNumber);
        dest.writeString(phone);
        dest.writeString(hostel);
        dest.writeString(collegeName);
        dest.writeStringList(userAdKeys);
    }

    //endregion

}
