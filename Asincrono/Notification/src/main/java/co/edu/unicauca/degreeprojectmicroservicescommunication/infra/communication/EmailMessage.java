package co.edu.unicauca.degreeprojectmicroservicescommunication.infra.communication;

import java.util.List;

public class EmailMessage {
    public String de;
    public List<String> para;
    public String asunto;
    public String body;

    public EmailMessage(String asunto, String body, String de, List<String> para) {
        this.asunto = asunto;
        this.body = body;
        this.de = de;
        this.para = para;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public List<String> getPara() {return para;}

    public void setPara(List<String> para) {
        this.para = para;
    }
}
