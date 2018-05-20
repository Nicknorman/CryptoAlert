package com.nnk.cryptoalert.tasks;

/**
 * Created by Nick on 18/05/2018.
 */

public interface IProgressUpdateView<Progress> {
  /**
   * Runs on UI thread.
   */
  public void preProcess();

  /**
   * Runs on UI thread.
   * @param current
   * @param total
   */
  public void updateProgress(Progress current, Progress total);

  /**
   * Runs on UI thread.
   * @param result
   */
  public void processingDone();

  /**
   * Does not run on UI thread.
   * @param message
   */
  public void processingError(String message);
}
