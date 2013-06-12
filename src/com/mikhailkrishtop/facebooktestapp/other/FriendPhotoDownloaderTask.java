package com.mikhailkrishtop.facebooktestapp.other;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.GridView;

import com.facebook.Session;
import com.mikhailkrishtop.facebooktestapp.adapter.ImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class FriendPhotoDownloaderTask extends AsyncTask<Void, Void, Void> {

	private static final String ALBUMS_TEMPLATE_URL = "https://graph.facebook.com/%s/albums?access_token=%s&limit=10";
	private static final String PHOTOS_TEMPLATE_URL = "https://graph.facebook.com/%s/photos?access_token=%s";

	String userId;
	GridView grid;
	ImageAdapter adapter;
	Activity activity;
	
	public FriendPhotoDownloaderTask(String id, GridView grid, ImageAdapter adapter, Activity activity) {
		super();
		this.userId = id;
		this.grid = grid;
		this.adapter = adapter;
		this.activity = activity;
	}
	
	@Override
	protected Void doInBackground(Void... params) {

		String albumUrl = String.format(ALBUMS_TEMPLATE_URL, userId, 
				Session.getActiveSession().getAccessToken());

		try {
			downloadAlbums(albumUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private void downloadAlbums(String url) throws Exception {
		HttpClient hc = sslClient(new DefaultHttpClient());
		HttpGet get = new HttpGet(url);
		HttpResponse albumsResponse = hc.execute(get);

		if (albumsResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

			String queryAlbums = EntityUtils.toString(albumsResponse
					.getEntity());
			JSONObject JOTemp = new JSONObject(queryAlbums);
			JSONArray JAAlbums = JOTemp.getJSONArray("data");

			for (int i = 0; i < JAAlbums.length(); i++) {
				JSONObject JOAlbum = JAAlbums.getJSONObject(i);

				if (JOAlbum.has("id")) {
					String albumId = JOAlbum.getString("id");
					String photosUrl = String.format(PHOTOS_TEMPLATE_URL,
							albumId, Session.getActiveSession()
									.getAccessToken());

					downloadPhotos(photosUrl);
				}
			}

		}
	}
	
	private void downloadPhotos(String url) throws Exception {
		HttpClient photosClient = sslClient(new DefaultHttpClient());
		HttpGet photosGet = new HttpGet(url);
		HttpResponse photosResponse = photosClient.execute(photosGet);

		if (photosResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

			String queryPhotos = EntityUtils.toString(photosResponse
					.getEntity());
			JSONArray JAPhotos = (new JSONObject(queryPhotos))
					.getJSONArray("data");

			for (int i = 0; i < JAPhotos.length(); i++) {
				JSONObject JOPhoto = JAPhotos.getJSONObject(i);

				if (JOPhoto.has("picture") && JOPhoto.has("source")) {

					final String imageUrl = JOPhoto.getString("picture");
					final String sourceUrl = JOPhoto.getString("source");

					activity.runOnUiThread(new InsertImageInGridView(sourceUrl,
							imageUrl, grid, adapter));

				}
			}
		}
	}
	
	private class InsertImageInGridView implements Runnable {

		GridView grid;
		ImageAdapter adapter;
		String sourceUrl;
		String thumbUrl;

		public InsertImageInGridView(String sourceUrl, String thumbUrl,
				GridView grid, ImageAdapter adapter) {
			this.grid = grid;
			this.adapter = adapter;
			this.sourceUrl = sourceUrl;
			this.thumbUrl = thumbUrl;
		}

		@Override
		public void run() {
			ImageLoader.getInstance().loadImage(thumbUrl,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							adapter.addImage(sourceUrl, loadedImage);
							grid.invalidateViews();
						}
					});
		}
	}
	
	private HttpClient sslClient(HttpClient client) {
		try {
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new MySSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = client.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, client.getParams());
		} catch (Exception ex) {
			return null;
		}
	}
}
