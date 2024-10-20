package com.example.projetws;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mHeaders;

    // Ajout du paramètre headers
    public VolleyMultipartRequest(int method, String url, Map<String, String> headers,
                                  Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mHeaders = headers != null ? headers : new HashMap<>(); // Assurer que mHeaders n'est pas nul
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // Écrire les paramètres texte (si présents)
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buildTextPart(bos, entry.getKey(), entry.getValue());
                }
            }

            // Écrire les données binaires (fichiers)
            Map<String, DataPart> data = getByteData();
            if (data != null && data.size() > 0) {
                for (Map.Entry<String, DataPart> entry : data.entrySet()) {
                    buildDataPart(bos, entry.getValue(), entry.getKey());
                }
            }

            bos.write(("--" + boundary + "--\r\n").getBytes());
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream: %s", e.toString());
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    // Méthode pour obtenir les données binaires (peut être implémentée dans la classe étendue)
    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        return new HashMap<>(); // Retourne un map vide par défaut
    }

    /**
     * Ajoute une partie de texte dans la requête multipart.
     */
    private void buildTextPart(ByteArrayOutputStream bos, String name, String value) throws IOException {
        bos.write(("--" + boundary + "\r\n").getBytes());
        bos.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n").getBytes());
        bos.write("\r\n".getBytes());
        bos.write(value.getBytes("UTF-8"));
        bos.write("\r\n".getBytes());
    }

    /**
     * Ajoute une partie de données binaires (fichiers) dans la requête multipart.
     */
    private void buildDataPart(ByteArrayOutputStream bos, DataPart dataFile, String inputName) throws IOException {
        bos.write(("--" + boundary + "\r\n").getBytes());
        bos.write(("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + dataFile.getFileName() + "\"\r\n").getBytes());
        bos.write(("Content-Type: " + dataFile.getType() + "\r\n").getBytes());
        bos.write("\r\n".getBytes());

        bos.write(dataFile.getContent());
        bos.write("\r\n".getBytes());
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}
