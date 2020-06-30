package com.cansevin.walknoti.models;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Message Notificaton Example
 *
 * {
 *      "validate_only": false,
 *      "message":
 *      {
 *          "notification":
 *          {
 *              "title": "Big News",
 *              "body": "This is a Big News!"
 *          },
 *          "android":
 *          {
 *              "notification":
 *              {
 *                  "title": "Noti in Noti title",
 *                  "body": "Noti in Noti body",
 *                  "click_action":
 *                  {
 *                      "type": 3
 *                  }
 *              }
 *          },
 *          "token":   ["AAWWHI94sgUR2RU5_P1ZptUiwLq7W8XWJO2LxaAPuXw4_HOJFXnBlN-q5_3bwlxVW_SHeDPx_s5bWW-9DjtWZsvcm9CwXe1FHJg0u-D2pcQPcb3sTxDTJeiwEb9WBPl_9w"]
 *      }
 * }
 *
 */


public class NotificationMessage {

    @SerializedName("validate_only")
    private boolean validateOnly;
    @SerializedName("message")
    private Message message;

    public NotificationMessage(boolean validateOnly, Message message) {
        this.validateOnly = validateOnly;
        this.message = message;
    }

    private NotificationMessage(Builder builder){

    }

    public static class Builder{
        private String title;
        private String body;
        private String intent;
        private Integer eventQ;
        private String[] pushToken;
        private String[] iconArray = {"https://cdn0.iconfinder.com/data/icons/beverage/64/BOTTLED_WATER-64.png","https://cdn2.iconfinder.com/data/icons/food-drink-10/24/food-drink-28-64.png",
                "https://cdn2.iconfinder.com/data/icons/free-line-halloween-icons/24/Toilet-Paper-64.png","https://cdn2.iconfinder.com/data/icons/freecns-cumulus/32/519908-101_Warning-64.png"};
        public Builder(String title, String body, String pushToken,String intent,Integer eventQ){
            this.title = title;
            this.body = body;
            this.intent = intent;
            this.pushToken = new String[1];
            this.pushToken[0] = pushToken;
            this.eventQ = eventQ;
        }

        public NotificationMessage build(){
            ClickAction clickAction = new ClickAction(1,"com.cansevin.walknoti.intent.action.test");
            SetButton[] btnArr = new SetButton[] { new SetButton("Yönlendir",1,"com.cansevin.walknoti.intent.action.test"),
                                                   new SetButton("Yoksay",1,"nothing") };
            AndroidNotification androidNotification = new AndroidNotification(title,body,clickAction,btnArr,
                    iconArray[eventQ]);
            AndroidConfig androidConfig = new AndroidConfig(androidNotification);
            Notification notification = new Notification(title,body);
            Message message = new Message(notification, androidConfig, pushToken);
            NotificationMessage notificationMessage =
                    new NotificationMessage(false, message);
            return notificationMessage;
        }
    }


    public boolean isValidateOnly() {
        return validateOnly;
    }

    public void setValidateOnly(boolean validateOnly) {
        this.validateOnly = validateOnly;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static class Message{

        @SerializedName("notification")
        private Notification notification;
        @SerializedName("android")
        private AndroidConfig android;
        @SerializedName("token")
        private String[] token;

        public Message(Notification notification, AndroidConfig android, String[] token) {
            this.notification = notification;
            this.android = android;
            this.token = token;
        }

        public Notification getNotification() {
            return notification;
        }

        public void setNotification(Notification notification) {
            this.notification = notification;
        }

        public AndroidConfig getAndroid() {
            return android;
        }

        public void setAndroid(AndroidConfig android) {
            this.android = android;
        }

        public String[] getToken() {
            return token;
        }

        public void setToken(String[] token) {
            this.token = token;
        }
    }

    public static class Notification{
        @SerializedName("title")
        private String title;
        @SerializedName("body")
        private String body;

        public Notification(String title, String body) {
            this.title = title;
            this.body = body;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }


        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    public static class AndroidConfig{
        @SerializedName("notification")
        private AndroidNotification notification;

        public AndroidConfig(AndroidNotification notification) {
            this.notification = notification;
        }

        public AndroidNotification getNotification() {
            return notification;
        }

        public void setNotification(AndroidNotification notification) {
            this.notification = notification;
        }
    }

    public static class AndroidNotification{
        @SerializedName("title")
        private String title;
        @SerializedName("body")
        private String body;
        @SerializedName("click_action")
        private ClickAction clickAction;
        @SerializedName("buttons")
        private SetButton[] setButton;
        @SerializedName("image")
        private String image;

        public AndroidNotification(String title, String body,ClickAction clickAction,SetButton[] setButton,String image) {
            this.title = title;
            this.body = body;
            this.image = image;
            this.setButton = setButton;
            this.clickAction = clickAction;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public ClickAction getClickAction() {
            return clickAction;
        }

        public void setClickAction(ClickAction clickAction) {
            this.clickAction = clickAction;
        }
    }

    public static class ClickAction{
        @SerializedName("type")
        private int type;
        @SerializedName("intent")
        private String intent;
        @SerializedName("action")
        private String action;
        @SerializedName("url")
        private String url;


        public ClickAction(int type) {
            this.type = type;
            this.url = "https://duckduckgo.com/";
        }


        public ClickAction(int type, String aciton) {
            this.type = type;
            this.action = aciton;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }
    }

    public static class SetButton {
        @SerializedName("name")
        private String name;
        @SerializedName("action_type")
        private int action_type;
        @SerializedName("intent_type")
        private int intent_type;
        @SerializedName("intent")
        private String intent;

        public SetButton(String name, int action_type,String intent) {
            this.name = name;
            this.action_type = action_type;
            this.intent_type = 1;
            this.intent = intent;
        }
    }
}
