package com.nnk.cryptoalert.data;

import java.util.Date;
import java.util.List;

/**
 * Created by Nick on 24-03-2018.
 */

public class CoinSocialHistory {
  private int id;
  private List<CoinSocialStat> socialStats;

  public CoinSocialHistory(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public List<CoinSocialStat> getSocialStats() {
    return socialStats;
  }

  public void setSocialStats(List<CoinSocialStat> socialStats) {
    this.socialStats = socialStats;
  }

  public class CoinSocialStat {
    private Date time;
    private int points;

    public Date getTime() {
      return time;
    }

    public void setTime(Date time) {
      this.time = time;
    }

    public int getPoints() {
      return points;
    }

    public void setPoints(int points) {
      this.points = points;
    }
  }
}
