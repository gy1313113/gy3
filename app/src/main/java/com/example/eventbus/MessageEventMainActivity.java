package com.example.eventbus;

/**
 * EventBus的事件发送实体类,去MainActivity
 */
public class MessageEventMainActivity {
    private final int number;
    
    public MessageEventMainActivity(int number){
        this.number = number;
    }
    
    public int getNumber(){
        return number;
    }
}
