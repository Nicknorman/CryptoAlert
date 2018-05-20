package com.nnk.cryptoalert.tasks;

import android.os.AsyncTask;

/**
 * Created by Nick on 19/05/2018.
 */

public abstract class UpdaterTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

  protected IProgressUpdateView<Progress> viewToUpdate;
  protected ITaskResultRecipient<Result> resultRecipient;

  /**
   * Abstract class that calls associated methods in IProgressUpdateView.
   * @param viewToUpdate
   */
  public UpdaterTask(IProgressUpdateView<Progress> viewToUpdate, ITaskResultRecipient<Result> resultRecipient) {
    this.viewToUpdate = viewToUpdate;
    this.resultRecipient = resultRecipient;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    viewToUpdate.preProcess();
  }

  @Override
  protected void onProgressUpdate(Progress... progress) {
    super.onProgressUpdate(progress);
    viewToUpdate.updateProgress(progress[0], progress[1]);
  }

  /**
   * Notifies both IProgressUpdateView and ITaskResultRecipient.
   * @param result
   */
  @Override
  protected void onPostExecute(Result result) {
    super.onPostExecute(result);
    viewToUpdate.processingDone();
    resultRecipient.onTaskDone(result);
  }
}
