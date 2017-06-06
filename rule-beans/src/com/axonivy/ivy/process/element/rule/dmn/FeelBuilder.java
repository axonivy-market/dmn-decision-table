package com.axonivy.ivy.process.element.rule.dmn;

public class FeelBuilder
{
  private boolean cdata = false;
  private boolean not = false;
  private String text = "";

  public static FeelBuilder create()
  {
    return new FeelBuilder();
  }

  public FeelBuilder cdata()
  {
    cdata = true;
    return this;
  }

  public FeelBuilder appendText(String txt)
  {
    this.text += txt;
    return this;
  }

  public FeelBuilder appendEscapedText(String txt)
  {
    this.text += escape(txt);
    return this;
  }

  public String build()
  {
    if (cdata)
    {
      if (not)
      {
        return "not(" + toCDATA(text) + ")";
      }
      return toCDATA(text);
    }

    if (not)
    {
      return "not(" + text + ")";
    }
    return text;
  }

  public FeelBuilder not()
  {
    not = true;
    return this;
  }

  private String toCDATA(String value)
  {
    return "<![CDATA[" + value + "]]>";
  }

  private String escape(String value)
  {
    return "\"" + value + "\"";
  }
}
