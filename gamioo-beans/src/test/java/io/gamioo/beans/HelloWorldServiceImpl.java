package io.gamioo.beans;

public class HelloWorldServiceImpl implements HelloWorldService {

    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String helloWorld() {
        System.out.println(text);
        return text;
    }

}