package org.apache.maven.scm.command.blame;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Evgeny Mandrikov
 */
public class BlameLine implements Serializable {
  private Date date;
  private String revision;
  private String author;

  public BlameLine(Date date, String revision, String author) {
    this.date = date;
    this.revision = revision;
    this.author = author;
  }

  public String getRevision() {
    return revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
