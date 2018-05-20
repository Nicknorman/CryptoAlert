package com.nnk.cryptoalert.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 19/05/2018.
 */

public class ImageDownloaderTask extends UpdaterTask<Map.Entry<String, String>, Integer, Map<String, Bitmap>> {

  /**
   * Task that downloads images from specified map of URLs and returns the images as a map with corresponding keys.
   * If an image download was unsuccessful, the Bitmap instance will be set to null.
   * @param viewToUpdate
   */
  public ImageDownloaderTask(IProgressUpdateView<Integer> viewToUpdate, ITaskResultRecipient<Map<String, Bitmap>> resultRecipient) {
    super(viewToUpdate, resultRecipient);
  }

  @Override
  protected Map<String, Bitmap> doInBackground(Map.Entry<String, String>[] urlMap) {
    Map<String, Bitmap> imageMap = new HashMap();
    int count = 0;
    for (Map.Entry<String, String> entry : urlMap) {
      String url = entry.getValue();
      Bitmap image = null;
      try {
        if (url != null && !url.isEmpty()) {
          InputStream in = new java.net.URL(url).openStream();
          image = BitmapFactory.decodeStream(in);
        }
      } catch (MalformedURLException e) {
        e.printStackTrace();
        viewToUpdate.processingError(e.getMessage());
      } catch (IOException e) {
        e.printStackTrace();
        viewToUpdate.processingError(e.getMessage());
      }
      imageMap.put(entry.getKey(), image);
      count++;
      publishProgress(count, urlMap.length);
    }
    return imageMap;
  }
}
