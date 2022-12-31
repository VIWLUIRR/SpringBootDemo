package com.github.viwluirr.dto;

public class Descript implements Cloneable{
    private String filename;
    private String length;
    private String start;
    private String end;
    private String block;

    public Descript() {
    }

    public Descript(String filename, String length, String start, String end, String block) {
        this.filename = filename;
        this.length = length;
        this.start = start;
        this.end = end;
        this.block = block;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    @Override
    public Descript clone(){
        return new Descript(filename,length,start,end,block);
    }
}
