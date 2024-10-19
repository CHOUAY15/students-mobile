package com.example.projetws.repository;

import android.content.Context;
import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.entities.Etudiant;
import com.example.projetws.util.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentRepository {

    private RequestQueue requestQueue;
    private static final String BASE_URL = "http://10.0.2.2:4000/api/etudiants";
    private static final String FETCH_URL = "http://10.0.2.2:4000/api/etudiants";
    private static final String ADD_URL = "http://10.0.2.2:4000/api/etudiants";

    public StudentRepository(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    // Fetch the list of students
    public void fetchEtudiants(final FetchCallback callback) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, FETCH_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Etudiant> etudiantList = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Etudiant etudiant = new Etudiant();
                                etudiant.setId(jsonObject.getInt("id"));
                                etudiant.setNom(jsonObject.getString("nom"));
                                etudiant.setPrenom(jsonObject.getString("prenom"));
                                etudiant.setVille(jsonObject.getString("ville"));
                                etudiant.setSexe(jsonObject.getString("sexe"));
                                etudiant.setImageUrl(jsonObject.getString("imageUrl"));
                                etudiantList.add(etudiant);
                            }
                            callback.onSuccess(etudiantList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.getMessage());
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    // Add a new student
    public void addEtudiant(final String nom, final String prenom, final String ville, final String sexe, Uri selectedImageUri, Context context, final AddCallback callback) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, ADD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        try {
                            JSONObject result = new JSONObject(resultResponse);
                            callback.onSuccess(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Error adding student: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nom", nom.trim());
                params.put("prenom", prenom.trim());
                params.put("ville", ville.trim());
                params.put("sexe", sexe);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                try {
                    byte[] imageData = getFileDataFromUri(selectedImageUri, context);
                    params.put("image", new DataPart("image.jpg", imageData, "image/jpeg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        requestQueue.add(multipartRequest);
    }

    private byte[] getFileDataFromUri(Uri uri, Context context) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    // Callback interface for fetching students
    public interface FetchCallback {
        void onSuccess(List<Etudiant> etudiants);
        void onError(String error);
    }

    // Callback interface for adding students
    public interface AddCallback {
        void onSuccess(JSONObject result);
        void onError(String error);
    }

    public void deleteEtudiant(int id, final DeleteCallback callback) {
        String deleteUrl = BASE_URL + "/" + id;

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.getMessage());
                    }
                });

        requestQueue.add(deleteRequest);
    }
    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }
    public void updateEtudiant(int id, String nom, String prenom, String ville, String sexe,
                               Uri imageUri, Context context, final UpdateCallback callback) {
        String updateUrl = BASE_URL + "/" + id;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.PUT, updateUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            String resultResponse = new String(response.data);
                            JSONObject result = new JSONObject(resultResponse);
                            callback.onSuccess(result);
                        } catch (JSONException e) {
                            callback.onError(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nom", nom.trim());
                params.put("prenom", prenom.trim());
                params.put("ville", ville.trim());
                params.put("sexe", sexe);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (imageUri != null) {
                    try {
                        byte[] imageData = getFileDataFromUri(imageUri, context);
                        params.put("image", new DataPart("image.jpg", imageData, "image/jpeg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return params;
            }
        };

        requestQueue.add(multipartRequest);
    }

    public interface UpdateCallback {
        void onSuccess(JSONObject result);
        void onError(String error);
    }
}
