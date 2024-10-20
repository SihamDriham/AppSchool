package com.example.projetws;

public class DataPart {
    private String fileName;
    private byte[] content;
    private String type;

    public DataPart() {}

    public DataPart(String fileName, byte[] content) {
        this.fileName = fileName;
        this.content = content;
        this.type = "image/jpeg"; // Type par défaut, tu peux le modifier selon tes besoins
    }

    public DataPart(String fileName, byte[] content, String type) {
        this.fileName = fileName;
        this.content = content;
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

