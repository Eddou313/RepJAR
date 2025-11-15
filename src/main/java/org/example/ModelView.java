package org.example;

public class ModelView
{
    private String Page;

    public ModelView(){}
    public ModelView(String a)
    {
        this.Page = a;
    }
    public String getPage()
    {
        return this.Page;
    }
    public void setPage(String a)
    {
        this.Page=a;
    }
}