package com.iseasoft.iseaiptv.api;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iseasoft.iseaiptv.models.Catalog;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.parsers.CatalogParser;
import com.iseasoft.iseaiptv.parsers.ChannelParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.iseasoft.iseaiptv.Constants.ADS_KEY;
import static com.iseasoft.iseaiptv.Constants.CATALOG_COLLECTION;
import static com.iseasoft.iseaiptv.Constants.CHANNEL_KEY;
import static com.iseasoft.iseaiptv.Constants.CONFIG_COLLECTION;

public class IndiaTvAPI {
    private static final String TAG = IndiaTvAPI.class.getSimpleName();
    private static final String MATCH_URL_REGEX = "Hosted by <a href=\"(.*?)\" target=\"_blank\"";
    private static final String URL_REGEX = "href=\"(.*?)\">";
    private static final String IMAGE_URL_REGEX = "poster:\"(.*?)\",name:";
    private static final String STREAM_URL_REGEX = "hls:\"(.*?)\"\\};settings";
    private static final String NAME_REGEX = "name:\"(.*?)\",contentTitle:";


    private static IndiaTvAPI instance;

    public static String getBaseURLDev() {
        return "http://hoofoot.com";
    }

    public static synchronized IndiaTvAPI getInstance() {
        if (instance == null) {
            instance = new IndiaTvAPI();
        }
        return instance;
    }

    public void getConfig(APIListener<Task<QuerySnapshot>> listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(CONFIG_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onRequestCompleted(task, task.getResult().getMetadata().toString());
                    } else {
                        listener.onError(new Error(task.getException()));
                    }
                });
    }

    public void getConfiAds(APIListener<Map<String, Object>> listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(CONFIG_COLLECTION).document(ADS_KEY)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> ads = new ArrayList<>();
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data == null) {
                            listener.onError(new Error(task.getException()));
                            return;
                        }
                        listener.onRequestCompleted(data, data.toString());
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        listener.onError(new Error(task.getException()));
                    }
                });
    }

    public void getAllCatalog(APIListener<ArrayList<Catalog>> listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(CATALOG_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    boolean addDataSuccess = false;
                    if (task.isSuccessful()) {
                        ArrayList<Catalog> catalogs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            try {
                                JSONObject jsonObject = new JSONObject(document.getData());
                                //Log.d(TAG, jsonObject.toString());
                                Catalog catalog = CatalogParser.createLeagueFromJSONObject(jsonObject);
                                if (catalog.getChannels().size() > 0) {
                                    catalogs.add(catalog);
                                }
                                addDataSuccess = true;
                            } catch (JSONException e) {
                                addDataSuccess = false;
                                e.printStackTrace();
                            }
                        }

                        if (addDataSuccess) {
                            listener.onRequestCompleted(catalogs, "");
                        } else {
                            listener.onError(new Error("Get league list failed"));
                        }

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        listener.onError(new Error(task.getException()));
                    }
                });
    }

    public void getChannelList(String league, APIListener<ArrayList<M3UItem>> listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(CATALOG_COLLECTION).document(league)
                .get()
                .addOnCompleteListener(task -> {
                    boolean addDataSuccess = false;
                    if (task.isSuccessful()) {
                        ArrayList<M3UItem> channels = new ArrayList<>();
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data == null) {
                            listener.onError(new Error(task.getException()));
                            return;
                        }
                        ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) data.get(CHANNEL_KEY);
                        for (HashMap<String, String> tmp : list) {
                            JSONObject object = new JSONObject(tmp);
                            try {
                                M3UItem channel = ChannelParser.createMatchFromJSONObject(object);
                                if (channel != null && channel.isVisible()) {
                                    channels.add(channel);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onError(new Error(e));
                            }
                        }
                        listener.onRequestCompleted(channels, (String) data.get("name"));
                    } else {
                        listener.onError(new Error(task.getException()));
                    }
                });
    }
}
