package com.nnk.cryptoalert.tasks;

/**
 * Created by Nick on 19/05/2018.
 */

public interface ITaskResultRecipient<Result> {

  /**
   * Will be called when result of UpdaterTask has been found.
   * @param result
   */
  public void onTaskDone(Result result);
}
