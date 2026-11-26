package org.example.pojo;
// создал дополнительный класс для ручки авторизации курьера без поля имени
public class CourierForLogin {
    private String login;
    private String password;

    public CourierForLogin(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public CourierForLogin() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
